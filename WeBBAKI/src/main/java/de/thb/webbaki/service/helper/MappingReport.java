package de.thb.webbaki.service.helper;

import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.entity.snapshot.Report;
import de.thb.webbaki.entity.snapshot.ReportScenario;
import de.thb.webbaki.service.snapshot.ReportScenarioService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Is a better Report with a Mapping for the view (by the scenario
 */
public class MappingReport extends Report {

    private Map<Long, ReportScenario> reportScenarioMap;

    public MappingReport(Report report){
        if(report.getNumberOfQuestionnaires() > 0) {
            this.setNumberOfQuestionnaires(report.getNumberOfQuestionnaires());
            this.setBranch(report.getBranch());
            this.setReportScenarios(report.getReportScenarios());
            this.setSnapshot(report.getSnapshot());
            this.setSector(report.getSector());
            this.setUser(report.getUser());
            this.setId(report.getId());
            this.setComment(report.getComment());
            reportScenarioMap = getReportScenarioMapFromList(this.getReportScenarios());
        }
    }

    /**
     *
     * @param reportScenarios
     * @return a Map of all given ReportScenarios mapped by the ScenarioId
     */
    private Map<Long, ReportScenario> getReportScenarioMapFromList(List<ReportScenario> reportScenarios){
        return reportScenarios.stream().collect(Collectors.toMap(
                (ReportScenario reportScenario) -> reportScenario.getScenario().getId(),
                (ReportScenario reportScenario) -> reportScenario));
    }

    public ReportScenario getReportScenario(Scenario scenario){
        return reportScenarioMap.get(scenario.getId());
    }
}
