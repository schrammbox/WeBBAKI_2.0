package de.thb.webbaki.service;

import de.thb.webbaki.controller.form.ThreatMatrixFormModel;
import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.entity.UserScenario;
import de.thb.webbaki.repository.QuestionnaireRepository;
import de.thb.webbaki.repository.UserRepository;
import de.thb.webbaki.service.helper.ThreatSituation;
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
    public void delQuest(long id) {
        questionnaireRepository.deleteQuestionnaireById(id);
    }


    public void saveQuestionnaireFromThreatMatrixFormModel(ThreatMatrixFormModel form, User user) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setDate(LocalDateTime.now());
        questionnaire.setUser(user);
        questionnaire.setComment(form.getComment());
        questionnaireRepository.save(questionnaire);

        List<Float> probabilities = form.getProbabilities();
        List<Float> impacts = form.getImpacts();
        List<String> smallComments = form.getSmallComments();
        List<Long> scenarioIds = form.getScenarioIds();

        for(int i = 0; i <  probabilities.size(); i++){
            UserScenario userScenario = UserScenario.builder()
                    .questionnaire(questionnaire)
                    .scenario(scenarioService.getById(scenarioIds.get(i)))
                    .impact(impacts.get(i))
                    .probability(probabilities.get(i))
                    .threatSituation(getThreatSituationLong(impacts.get(i).longValue(), probabilities.get(i).longValue()))
                    .smallComment(smallComments.get(i)).build();
            userScenarioService.saveUserScenario(userScenario);
        }
    }

    public Map<Long, String[]> getMapping(Questionnaire quest) {
        String rawString = quest.getMapping();
        // CUT "{" & "}"
        rawString = rawString.substring(1, rawString.length() - 1);


        Map<Long, String[]> newMap = Arrays.stream(rawString.split(", "))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(s -> Long.parseLong(s[0]), s -> s[1].split(";")));

        return newMap;
    }

    public long getImpactLongFromString(String impValue){
        long impNum = -1;

        switch (impValue) {
            case "keine":
                impNum = 1;
                break;
            case "geringe":
                impNum = 2;
                break;
            case "hohe":
                impNum = 3;
                break;
            case "existenzielle":
                impNum = 4;
                break;
        }

        return impNum;
    }
    public String getImpactStringFromLong(long impNum){
        String impValue = "";

        switch ((int) impNum) {
            case 1:
                impValue = "keine";
                break;
            case 2:
                impValue = "geringe";
                break;
            case 3:
                impValue = "hohe";
                break;
            case 4:
                impValue = "existenzielle";
                break;
        }
        return impValue;
    }

    public long getProbabilityLongFromString(String probValue){
        long probNum = -1;
        switch (probValue) {
            case "nie":
                probNum = 0;
                break;
            case "selten":
                probNum = 1;
                break;
            case "mittel":
                probNum = 2;
                break;
            case "haeufig":
                probNum = 3;
                break;
            case "sehr haeufig":
                probNum = 4;
                break;
        }
        return probNum;
    }

    public String getProbabilityStringFromLong(long probNum){
        String probValue = "";
        switch ((int)probNum) {
            case 0:
                probValue = "nie";
                break;
            case 1:
                probValue = "selten";
                break;
            case 2:
                probValue = "mittel";
                break;
            case 3:
                probValue = "haeufig";
                break;
            case 4:
                probValue = "sehr haeufig";
                break;
        }
        return probValue;
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
     * @param questMap
     * @return is the Queue of Threadsituations as Long Value
     */
    public Queue<ThreatSituation> getThreatSituationQueueFromMapping(Map<Long, String[]> questMap){
        Queue<ThreatSituation> threatSituationQueue = new LinkedList<ThreatSituation>();
        for(long i = 1; i <= questMap.size(); i++ ){
            final long probability = getProbabilityLongFromString(questMap.get(i)[0]);
            final long impact = getImpactLongFromString(questMap.get(i)[1]);

            threatSituationQueue.add(new ThreatSituation(getThreatSituationLong(impact, probability)));
        }
        return threatSituationQueue;
    }

    /**
     *
     * @param queueList List of ThreadSituation-Queues
     * @return Returns the average queue
     */
    public Queue<ThreatSituation> getThreatSituationAverageQueueFromQueues(List<Queue<ThreatSituation>> queueList){
        Queue<ThreatSituation> threatSituationQueue = new LinkedList<ThreatSituation>();
        int size = queueList.get(0).size();
        //go through all possible scenarios
        for(int i= 0; i < size; i++){
            List<ThreatSituation> threatSituationList = new LinkedList<ThreatSituation>();
            for (Queue<ThreatSituation> queue : queueList){
                threatSituationList.add(queue.poll());
            }
            threatSituationQueue.add(ThreatSituation.getAverageThreatSituation(threatSituationList));
        }

        return threatSituationQueue;
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