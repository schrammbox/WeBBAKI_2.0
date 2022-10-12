package de.thb.webbaki.service;

import de.thb.webbaki.entity.Snapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ScheduleService {
    @Autowired
    SnapshotService snapshotService;

    @Scheduled(cron = "0 0 0 1 3/3 *", zone="CET")
    public void test(){
        LocalDate today = LocalDate.now();
        String snapshotName = today.getYear() + " Quartal " + (today.getMonthValue() / 4);
        Snapshot snapshot = new Snapshot();
        snapshot.setName(snapshotName);
        snapshotService.createSnap(snapshot);
    }
}
