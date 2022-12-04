package de.thb.webbaki.service.snapshot;

import de.thb.webbaki.entity.questionnaire.Questionnaire;
import de.thb.webbaki.entity.snapshot.Snapshot;
import de.thb.webbaki.repository.snapshot.SnapshotRepository;
import de.thb.webbaki.service.UserService;
import de.thb.webbaki.service.questionnaire.QuestionnaireService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SnapshotService {

    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private SnapshotRepository snapshotRepository;
    @Autowired
    private ReportService reportService;

    /**
     * Checks every 3th month on day 1, 5 and 10 in hour 0 and 12
     * if there already is the right quarter Snapshot. If not
     * it creates the right one.
     */
    @Scheduled(cron = "0 0 0,12 1,5,10 1/3 *", zone="CET")
    public void createSnapshotBySchedule(){
        LocalDate today = LocalDate.now();
        String snapshotName = today.getYear() + " Quartal " + (int)((today.getMonthValue() / 4) + 1);
        if(!doesSnapshotExistsByName(snapshotName)){
            Snapshot snapshot = new Snapshot();
            snapshot.setName(snapshotName);
            createSnap(snapshot);
        }
    }

    public List<Snapshot> getAllSnapshots(){return snapshotRepository.findAll();}

    public List<Snapshot> getAllSnapshotOrderByDESC(){return snapshotRepository.findAllByOrderByIdDesc();}

    public Optional<Snapshot> getSnapshotByID(Long id){return snapshotRepository.findById(id);}

    public Snapshot getNewestSnapshot(){return snapshotRepository.findTopByOrderByIdDesc();}
    public boolean doesSnapshotExistsByName(String name){return snapshotRepository.existsSnapshotByName(name);}

    public void createSnap(Snapshot snap){

        /* Perist Snapshot */
        snap.setDate(LocalDateTime.now());
        snapshotRepository.save(snap);
        reportService.createReports(snap);
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
