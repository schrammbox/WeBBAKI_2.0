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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Builder
public class QuestionnaireService {
    private final QuestionnaireRepository questionnaireRepository;
    private final UserRepository userRepository;

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    private UserScenarioService userScenarioService;

    public boolean existsQuestionnaireByUserId(long id){return questionnaireRepository.existsByUser_id(id);}
    public boolean existsQuestionnaireByIdAndUserId(long questId, long userId){return questionnaireRepository.existsByIdAndUser_Id(questId, userId);}
    public void deleteAllByUser(User user){questionnaireRepository.deleteAllByUser(user);}
    public void save(Questionnaire questionnaire){questionnaireRepository.save(questionnaire);}

    public Questionnaire getQuestionnaire(long id) {
        return questionnaireRepository.findById(id);
    }

    public Questionnaire getNewestQuestionnaireByUserId(long id) {
        return questionnaireRepository.findFirstByUser_IdOrderByIdDesc(id);
    }

    public List<Questionnaire> getAllQuestByUser(long id) {
        return questionnaireRepository.findAllByUser(userRepository.findById(id).get());
    }

    //Factory-method for a Questionnaire
    public Questionnaire createQuestionnaireForUser(User user){
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setDate(LocalDateTime.now());
       
        //create a UserScenario for every Scenario
        List<Scenario> scenarios = scenarioService.getAllScenarios();
        List<UserScenario> userScenarios = new LinkedList<>();
        questionnaire.setUser(user);
        questionnaireRepository.save(questionnaire);
        //save all UserScenarios for this questionnaire
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

    /*
    Delete Questionnaire by given ID
    Used Repository-Method deleteQuestionnaireById from
    @QuestionnaireRepository
     */
    public void deleteQuestionnaireById(long id) {
        userScenarioService.deleteAllUserScenariosByQuestionnaireId(id);
        questionnaireRepository.deleteQuestionnaireById(id);
    }

    /**
     * checks if all active scenarios have a representation in the questionnaires UserScenarios
     * --> add an empty UserScenario from this Scenario if not.
     * delete UserScenarios with an inactive Scenario from this questionnaire
     * @param questionnaire
     */
    public void checkIfMatchingWithScenarios(Questionnaire questionnaire){
        List<Scenario> activeScenarios = scenarioService.getAllScenariosByActiveTrue();
        List<UserScenario> userScenarios = new ArrayList<>(questionnaire.getUserScenarios());
        //remove every UserScenario Scenario from the activeScenario-list
        for(UserScenario userScenario : userScenarios){
            //try to remove the Scenario
            if(!activeScenarios.remove(userScenario.getScenario())){
                //delete the UserScenario from the Questionnaire, if it is not from an active Scenario (not in active list)
                questionnaire.getUserScenarios().remove(userScenario);
            }
        }

        //all scenarios which are not deleted have to be created as UserScenario for the Questionnaire
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
                //TODO save not existing UserScenario
                userScenario.setScenario(scenarioService.getById(userScenario.getScenario().getId()));
            }
        }

        userScenarioService.saveAllUserScenario(userScenarios);
    }

    /**
     *
     * @param questionnaireList
     * @param userList
     * @return the only the questionnaires from the users in userlist.
     */
    public List<Questionnaire> getQuestionnairesWithUsersInside(List<Questionnaire> questionnaireList, List<User> userList){
        List<Questionnaire> newQuestionnaireList = new LinkedList<Questionnaire>();
        for(Questionnaire quest: questionnaireList){
            for(User user: userList){
                if(quest != null && quest.getUser().equals(user)){
                    newQuestionnaireList.add(quest);
                }
            }
        }
        return newQuestionnaireList;
    }

}