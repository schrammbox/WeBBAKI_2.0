package de.thb.webbaki.service.helper;

import de.thb.webbaki.entity.MasterScenario;
import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.entity.snapshot.Report;
import de.thb.webbaki.entity.snapshot.ReportScenario;
import de.thb.webbaki.service.snapshot.ReportScenarioService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Is a better Report with a Mapping (from Scenario to ReportScenario) for the view (by the scenario)
 */
@Getter
public class MappingReport extends Report {

    private Map<Scenario, ReportScenario> reportScenarioMap;

    public MappingReport(Report report){
        //create the MappingReport from Report, if the Report is created from questionnaires (not 0)
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
     * @return a Map of all given ReportScenarios mapped by the Scenario
     */
    private Map<Scenario, ReportScenario> getReportScenarioMapFromList(List<ReportScenario> reportScenarios){
        return reportScenarios.stream().collect(Collectors.toMap(
                (ReportScenario reportScenario) -> reportScenario.getScenario(),
                (ReportScenario reportScenario) -> reportScenario));
    }

    public ReportScenario getReportScenario(Scenario scenario){
        return reportScenarioMap.get(scenario);
    }

    /**
     * Important Method for the view.
     * @param masterScenario
     * @return if a MasterScenario (with its Scenarios) is represented in the ReportScenarioMap
     */
    public boolean checkIfContainsMasterScenario(MasterScenario masterScenario){
        //check if the scenarios of the masterScenario are included in the reportScenarioMap-Keylist
        return !Collections.disjoint(masterScenario.getScenarios(), reportScenarioMap.keySet());
    }

    /**
     * Important Method for the view
     * @param scenario
     * @return if a Scenario is represented in the ReportScenarioMap
     */
    public boolean checkIfContainsScenario(Scenario scenario){
        return reportScenarioMap.keySet().contains(scenario);
    }
}
