package de.thb.webbaki.service;

import de.thb.webbaki.controller.form.ThreatMatrixFormModel;
import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.entity.UserScenario;
import de.thb.webbaki.repository.QuestionnaireRepository;
import de.thb.webbaki.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        //TODO delte old stuff
        questionnaire.setSmallComment("");
        questionnaire.setMapping("{1=none;none, 2=none;none, 3=none;none, 4=none;none, 5=none;none, 6=none;none, 7=none;none, 8=none;none, 9=none;none, 10=none;none, 11=none;none, 12=none;none, 13=none;none, 14=none;none, 15=none;none, 16=none;none, 17=none;none, 18=none;none, 19=none;none, 20=none;none, 21=none;none, 22=none;none, 23=none;none, 24=none;none, 25=none;none, 26=none;none, 27=none;none}");

        //create a UserScenario for every Scenario
        List<Scenario> scenarios = scenarioService.getAllScenarios();
        List<UserScenario> userScenarios = new LinkedList<>();
        questionnaire.setUser(user);
        questionnaireRepository.save(questionnaire);

        //save all UserScenarios for this questionnaire
        for(Scenario scenario : scenarios){
            UserScenario userScenario = UserScenario.builder().smallComment("")
                    .scenario(scenario)
                    .questionnaire(questionnaire)
                    .impact(-1)
                    .probability(-1)
                    .threatSituation(-1).build();
            userScenarioService.saveUserScenario(userScenario);

        }
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

    public void addMissingUserScenario(Questionnaire questionnaire){
        List<Scenario> scenarios = scenarioService.getAllScenarios();
        for(Scenario scenario : scenarios){
            if(!userScenarioService.existsUerScenarioByScenarioIdAndQuestionnaireId(scenario.getId(), questionnaire.getId())){
                UserScenario userScenario = UserScenario.builder().smallComment("")
                        .scenario(scenario)
                        .questionnaire(questionnaire)
                        .impact(-1)
                        .probability(-1)
                        .threatSituation(-1).build();
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
            userScenario.setQuestionnaire(questionnaire);
            userScenario.setThreatSituation(getThreatSituationLong((long)userScenario.getImpact(), (long)userScenario.getProbability()));
            userScenario.setScenario(scenarioService.getById(userScenario.getScenario().getId()));
        }

        userScenarioService.saveAllUserScenario(userScenarios);
    }

    /**
     *
     * @param impact
     * @param probability
     * @return ThreatSituation based on table from UP KRITIS
     */
    public long getThreatSituationLong(long impact, long probability) {
        if(impact == -1 || probability == -1){
            return -1;
        }else if(probability < 4 || impact > 2){
            return impact * probability;
        }else{
            if(impact == 2) {
                return 6;
            }else{
                return 3;
            }
        }
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