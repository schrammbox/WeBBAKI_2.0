package de.thb.webbaki.service.snapshot;

import de.thb.webbaki.entity.snapshot.Snapshot;
import de.thb.webbaki.repository.snapshot.SnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SnapshotService {

    @Autowired
    private SnapshotRepository snapshotRepository;
    @Autowired
    private ReportService reportService;

    /**
     * Checks every 3th month on day 1, 5 and 10 in hour 0 and 12
     * if there already is the right quarter Snapshot. If not
     * it creates the right one.
     */
    @Scheduled(cron = "0 0 0,12 1,5,10,15 1/3 *", zone = "CET")
    @Transactional
    public void createSnapshotBySchedule() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int currentQuarter = (month + 2) / 3;
        // in this case previous Q is needed this a snapshot is a summary and not a current overview
        int previousQuarter = (currentQuarter - 1 + 4) % 4;
        String snapshotName = today.getYear() + " Quartal " + previousQuarter;
        if (!ExistsByName(snapshotName)) {
            Snapshot snapshot = new Snapshot();
            snapshot.setName(snapshotName);
            createSnap(snapshot);
        }
    }

    /**
     * creates a Snapshot every day based on the previous day.
     * There is only one Daily Snapshot existing. Therefor it is searching if a snapshot is existing
     * and if it is it will update that and not create a new one
     */
    @Scheduled(cron = "0 6 * * * *", zone = "CET") // Runs every 24 hours
    @Transactional
    public void createOrUpdateDailySnapshot() {
        LocalDate today = LocalDate.now();
        LocalDate previousDay = today.minusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String snapshotName = "Täglicher Bericht vom: " + previousDay.format(formatter);

        List<Snapshot> allSnapshots = snapshotRepository.findAll();

        List<Snapshot> matchingSnapshots = allSnapshots.stream()
                .filter(snapshot -> snapshot.getName() != null && snapshot.getName().contains("Täglicher Bericht vom: "))
                .collect(Collectors.toList());

        if (!matchingSnapshots.isEmpty()) {
            for (Snapshot snapshot : matchingSnapshots) {
                snapshotRepository.delete(snapshot);
            }
        }

        Snapshot snapshot = new Snapshot();
        snapshot.setName(snapshotName);
        createSnap(snapshot);
    }

    public List<Snapshot> getAllSnapshots() {
        return snapshotRepository.findAll();
    }

    public List<Snapshot> getAllSnapshotOrderByDESC() {
        return snapshotRepository.findAllByOrderByIdDesc();
    }

    public Optional<Snapshot> getSnapshotByID(Long id) {
        return snapshotRepository.findById(id);
    }

    public Optional<Snapshot> getNewestSnapshot() {
        return snapshotRepository.findTopByOrderByIdDesc();
    }

    public boolean ExistsByName(String name) {
        return snapshotRepository.existsSnapshotByName(name);
    }

    public void createSnap(Snapshot snap) {
        // Persist Snapshot
        snap.setDate(LocalDateTime.now());
        snapshotRepository.save(snap);
        reportService.createReports(snap);
    }

    public List<Snapshot> getLast5QuartalSnapshots(){
        final var snapshotList = getAllSnapshots();

        LocalDate myLocal = LocalDate.now();
        int quarter = myLocal.get(IsoFields.QUARTER_OF_YEAR);
        final int year = myLocal.getYear();

        snapshotList.removeIf(s -> !s.getName().matches("\\d{4} Quartal \\d") || s.getDate().getYear() < year - 1 || (s.getDate().getYear() != year && s.getDate().get(IsoFields.QUARTER_OF_YEAR) < quarter));

        return snapshotList;
    }
}
