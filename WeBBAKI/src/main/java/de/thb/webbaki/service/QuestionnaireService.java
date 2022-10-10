package de.thb.webbaki.service;

import com.sun.mail.util.QEncoderStream;
import de.thb.webbaki.controller.form.ThreatMatrixFormModel;
import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.repository.QuestionnaireRepository;
import de.thb.webbaki.repository.ScenarioRepository;
import de.thb.webbaki.repository.UserRepository;
import de.thb.webbaki.service.helper.ThreatSituation;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    private final ScenarioRepository scenarioRepository;

    public Questionnaire getEmptyQuestionnaire() {
        return new Questionnaire();
    }

    public Questionnaire getQuestionnaire(long id) {
        return questionnaireRepository.findById(id);
    }

    public Questionnaire getNewestQuestionnaireByUserId(long id) {
        return questionnaireRepository.findFirstByUser_IdOrderByIdDesc(id);
    }

    public List<Questionnaire> getAllQuestByUser(long id) {
        return questionnaireRepository.findAllByUser(userRepository.findById(id).get());
    }

    /*
    Delete Questionnaire by given ID
    Used Repository-Method deleteQuestionnaireById from
    @QuestionnaireRepository
     */
    public void delQuest(long id) {
        questionnaireRepository.deleteQuestionnaireById(id);
    }


    public Questionnaire saveQuestionaire(ThreatMatrixFormModel form) {
        String[] prob = form.getProb();
        String[] imp = form.getImp();

        Map<Long, String> map = new HashMap<>();
        for (long i = 1; i < prob.length; i++) {
            map.put(i, prob[(int) i] + ';' + imp[(int) i]);
        }

        final var questionnaire = questionnaireRepository.save(Questionnaire.builder()
                .date(LocalDateTime.now())
                .user(form.getUser())
                .mapping(map.toString())
                .comment(form.getComment())
                .build());

        return questionnaire;
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