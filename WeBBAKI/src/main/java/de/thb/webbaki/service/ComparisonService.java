package de.thb.webbaki.service;

import de.thb.webbaki.entity.snapshot.Snapshot;
import de.thb.webbaki.enums.ReportFocus;
import de.thb.webbaki.exception.SnapshotNotFoundException;
import de.thb.webbaki.service.Exceptions.UnknownReportFocusException;
import de.thb.webbaki.service.helper.MappingReport;
import de.thb.webbaki.service.helper.ReportComparison;
import de.thb.webbaki.service.snapshot.ReportService;
import de.thb.webbaki.service.snapshot.SnapshotService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.List;

@Service
@AllArgsConstructor
public class ComparisonService {

    private final SnapshotService snapshotService;
    private final ReportService reportService;

    public ReportComparison getReportComparisonFromUsername(String username) throws UnknownReportFocusException{
        List<Snapshot> snapshotList = snapshotService.getLast5QuartalSnapshots();
        ReportComparison reportComparison = new ReportComparison();

        Snapshot newestSnapshot = snapshotService.getNewestSnapshot()
                .orElseThrow(() -> new SnapshotNotFoundException("snapshot was not found with the given id"));

        MappingReport companyReport = reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, username, newestSnapshot);
        reportComparison.setCompanyReport(companyReport);

        MappingReport branchReport = reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, username, newestSnapshot);
        reportComparison.setBranchReport(branchReport);

        MappingReport sectorReport = reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, username, newestSnapshot);
        reportComparison.setSectorReport(sectorReport);

        MappingReport nationalReport = reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, username, newestSnapshot);
        reportComparison.setNationalReport(nationalReport);

        LocalDate myLocal = LocalDate.now();
        int quarter = myLocal.get(IsoFields.QUARTER_OF_YEAR);
        int year = myLocal.getYear();

        reportComparison.setQuarter(quarter);
        reportComparison.setYear(year);

        for (int i = 0; i < 4; i++){
            if(quarter - 1 == 0){
                quarter = 4;
                year--;
            }else{
                --quarter;
            }

            reportComparison.addNewQuarter(quarter);
            reportComparison.addNewYear(year);

            for(Snapshot snap : snapshotList){
                if(snap.getDate().get(IsoFields.QUARTER_OF_YEAR) == quarter){
                    reportComparison.addNewCompanyReport(reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, username, snap));

                    reportComparison.addNewBranchReport(reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, username, snap));

                    reportComparison.addNewSectorReport(reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, username, snap));

                    reportComparison.addNewNationalReport(reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, username, snap));
                    break;
                }
            }

        }

        return reportComparison;
    }
}
