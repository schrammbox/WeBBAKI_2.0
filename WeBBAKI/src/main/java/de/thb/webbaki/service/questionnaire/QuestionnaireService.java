package de.thb.webbaki.service.questionnaire;

import de.thb.webbaki.controller.form.ThreatMatrixFormModel;
import de.thb.webbaki.entity.questionnaire.Questionnaire;
import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.entity.questionnaire.UserScenario;
import de.thb.webbaki.repository.questionnaire.QuestionnaireRepository;
import de.thb.webbaki.repository.UserRepository;
import de.thb.webbaki.service.ScenarioService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Builder
public class QuestionnaireService {
    private final QuestionnaireRepository questionnaireRepository;
    private final UserRepository userRepository;

    private final ScenarioService scenarioService;
    private final UserScenarioService userScenarioService;

    public boolean existsByUserId(long id){return questionnaireRepository.existsByUser_id(id);}
    public boolean existsByIdAndUserId(long questId, long userId){return questionnaireRepository.existsByIdAndUser_Id(questId, userId);}
    public void save(Questionnaire questionnaire){questionnaireRepository.save(questionnaire);}
    public Questionnaire getQuestionnaire(long id) {return questionnaireRepository.findById(id);}
    public Questionnaire getNewestQuestionnaireByUserId(long id) {return questionnaireRepository.findFirstByUser_IdOrderByIdDesc(id);}
    public List<Questionnaire> getAllQuestByUser(long id) {return questionnaireRepository.findAllByUser(userRepository.findById(id).get());}

    /**
     * @param user
     * @return new questionnaire for the given user.
     * Creates and save the new Questionnaire
     */
    public Questionnaire createQuestionnaireForUser(User user){
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setDate(LocalDateTime.now());
        questionnaire.setUser(user);
        questionnaireRepository.save(questionnaire);

        //create a UserScenario for every active Scenario
        List<Scenario> scenarios = scenarioService.getAllScenarios();
        List<UserScenario> userScenarios = new LinkedList<>();
        for(Scenario scenario : scenarios){
            if(scenario.isActive()) {
                UserScenario userScenario = UserScenario.builder().smallComment("")
                        .scenario(scenario)
                        .questionnaire(questionnaire)
                        .impact(-1)
                        .probability(-1).build();

                userScenarios.add(userScenario);
            }
        }
        userScenarioService.saveAllUserScenario(userScenarios);

        return questionnaire;
    }

    /**
     * Delete Questionnaire and all of its UserScenarios by given id
     * @param id
     */
    public void deleteQuestionnaireById(long id) {
        userScenarioService.deleteAllUserScenariosByQuestionnaireId(id);
        questionnaireRepository.deleteQuestionnaireById(id);
    }

    /**
     * Checks if all active scenarios have a representation in the questionnaires UserScenarios
     * --> add an empty UserScenario from this Scenario if not.
     * delete UserScenarios with an inactive Scenario from this questionnaire
     * @param questionnaire
     */
    public void checkIfMatchingWithActiveScenariosFromDB(Questionnaire questionnaire){
        List<Scenario> activeScenarios = scenarioService.getAllScenariosByActiveTrue();
        //create a copy of the UserScenarioList. Because we need to delete items from the list inside the Questionnaire.
        List<UserScenario> userScenarios = new ArrayList<>(questionnaire.getUserScenarios());

        //try to remove every UserScenario of the Questionnaire from the activeScenario-list
        //and remove it from the questionnaire List, if its not part of the List
        for(UserScenario userScenario : userScenarios){
            //try to remove the Scenario
            if(!activeScenarios.remove(userScenario.getScenario())){
                //delete the UserScenario from the Questionnaire, if it is not from an active Scenario (not in active list)
                questionnaire.getUserScenarios().remove(userScenario);
            }
        }

        //all scenarios which were not deleted have to be created as new UserScenario for the Questionnaire
        for(Scenario scenario : activeScenarios){
            if (!userScenarioService.existsUerScenarioByScenarioIdAndQuestionnaireId(scenario.getId(), questionnaire.getId())) {
                UserScenario userScenario = UserScenario.builder().smallComment("")
                        .scenario(scenario)
                        .questionnaire(questionnaire)
                        .impact(-1)
                        .probability(-1).build();
                questionnaire.getUserScenarios().add(userScenario);
            }
        }
    }

    /**
     * Create a new Questionnaire with the UserScenarios from inside the form and save it
     * @param form
     * @param user
     */
    public void saveQuestionnaireFromThreatMatrixFormModel(ThreatMatrixFormModel form, User user) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setDate(LocalDateTime.now());
        questionnaire.setUser(user);
        questionnaire.setComment(form.getComment());
        questionnaireRepository.save(questionnaire);

        List<UserScenario> userScenarios = form.getUserScenarios();

        for(UserScenario userScenario : userScenarios){
            if(userScenario.getScenario() != null) {
                userScenario.setQuestionnaire(questionnaire);
                userScenario.setScenario(scenarioService.getById(userScenario.getScenario().getId()));
            }
        }

        userScenarioService.saveAllUserScenario(userScenarios);
    }

}