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

    public List<Snapshot> getAllSnapshotOrderByDESC(){return snapshotRepository.findAllByOrderByIdDesc();}

    public Optional<Snapshot> getSnapshotByID(Long id){return snapshotRepository.findById(id);}

    public Snapshot getNewestSnapshot(){return snapshotRepository.findTopByOrderByIdDesc();}

    public void createSnap(Snapshot snap){

        List<User> userList = userService.getAllUsers();
        List<Long> questIDs = new ArrayList<>();

        for (User user : userList){
            long userID = user.getId();

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
}
