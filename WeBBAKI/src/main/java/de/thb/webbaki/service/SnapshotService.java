package de.thb.webbaki.service;

import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.Snapshot;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.repository.SnapshotRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Builder
@AllArgsConstructor

public class SnapshotService {

    private final UserService userService;
    private final QuestionnaireService questionnaireService;
    private final SnapshotRepository snapshotRepository;

    public List<Snapshot> getAllSnapshots(){return snapshotRepository.findAll();}

    public Optional<Snapshot> getSnapshotByID(Long id){return snapshotRepository.findById(id);}

    public void createSnap(Snapshot snap){

        List<User> userList = userService.getAllUsers();
        List<Long> questIDs = new ArrayList<>();

        for (long i = 1; i < userList.size(); i++){
            long userID = userList.get((int)i).getId();


            Questionnaire quest = questionnaireService.getNewestQuestionnaireByUserId(userID);
            if(quest != null) {
                questIDs.add(quest.getId());
            }
        }

        /* Perist Snapshot */

        snap.setDate(LocalDateTime.now());
        snap.setQuestionaireIDs(questIDs.toString());
        snapshotRepository.save(snap);

    }

    public List<String> getLongList(String list){
        List<String> longList = new ArrayList<>();
        list = list.replace("[","");
        list = list.replace("]","");
        list = list.replace(" ","");
        longList = Arrays.stream(list.split(",")).toList();

        return longList;
    }

    public List<Questionnaire> getAllQuestionnaires(long snapID) {
        Optional<Snapshot> snap = getSnapshotByID(snapID);
        List<String> questIDs = getLongList(snap.orElseThrow().getQuestionaireIDs());
        List<Questionnaire> quests = new ArrayList<>();

        for(String id : questIDs){
            quests.add(questionnaireService.getQuestionnaire(Long.parseLong(id)));
        }

        return quests;
    }

    public Map<Long, Long[]> calcAverageValues(long snapID){
        List<Questionnaire> quests = getAllQuestionnaires(snapID);
        Map<Long, Long[]> finalMap = new HashMap<>();
        int questcount = 0, prob = 0, imp = 0;

        for(long i= 1; i <= 100; i++){
            finalMap.put(i, new Long[] {(long)0,(long)0});
        }

        for(Questionnaire quest : quests){
            Map<Long, String[]> map = questionnaireService.getMapping(quest);
            questcount ++;
            for(long i= 1; i <= map.size(); i++){
                finalMap.put(i, new Long[] {finalMap.get(i)[0] + getProbNum(map.get(i)[0]),finalMap.get(i)[1] + getImpNum(map.get(i)[1])});
            }
        }
        for(long i= 1; i <= finalMap.size(); i++){
            finalMap.put(i, new Long[] {finalMap.get(i)[0] / questcount ,finalMap.get(i)[1] / questcount });
        }

        return finalMap;
    }

    public Map<Long, Long[]> createQuestMap(long snapID) {

        return null;
    }



        public long getImpNum(String impValue){
        long impNum = 0;

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
    public long getProbNum(String probValue){
        long probNum = 0;
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

}
