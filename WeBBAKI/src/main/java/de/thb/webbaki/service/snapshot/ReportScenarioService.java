package de.thb.webbaki.service.snapshot;

import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.entity.questionnaire.UserScenario;
import de.thb.webbaki.entity.snapshot.ReportScenario;
import de.thb.webbaki.repository.snapshot.ReportScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReportScenarioService {
    @Autowired
    ReportScenarioRepository reportScenarioRepository;

    public void saveAllReportScenarios(List<ReportScenario> reportScenarios){reportScenarioRepository.saveAll(reportScenarios);}

    /**
     * Takes the respective impacts and probabilities from the UserScenarios,
     * calculates the threatSituations and create ReportScenarios from this.
     * If there is a Scenario without any ReportScenario, it creates an empty one (threatSituation = -1)
     * @param userScenarios
     * @return the created ReportScenarios
     */
    public List<ReportScenario> calculateReportScenariosFromUserScenarios(List<UserScenario> userScenarios, List<Scenario> scenarios){
        List<ReportScenario> reportScenarios = new ArrayList<>();
        //get Map from ScenarioList and set all booleans to false
        //this is important to notice which scenarios already where represented as ReportScenario
        Map<Scenario, Boolean> scenarioBooleanMap = scenarios.stream().collect(Collectors.toMap((scenario) -> scenario, (scenario) -> false));

        for(UserScenario userScenario : userScenarios){
            //set this scenario to true (already used now)
            scenarioBooleanMap.put(userScenario.getScenario(), true);
            //only create if the scenario is active
            if(userScenario.getScenario().isActive()) {
                float threatSituation = calculateThreatSituation((long) userScenario.getImpact(), (long) userScenario.getProbability());
                reportScenarios.add(ReportScenario.builder()
                        .threatSituation(threatSituation)
                        .Scenario(userScenario.getScenario())
                        .numberOfValues((threatSituation == -1) ? 0 : 1).build());
            }
        }

        //add all missing ReportScenarios that are not created from UserScenario yet (not exists)
        scenarioBooleanMap.forEach((scenario, aBoolean) ->{
            if(scenario.isActive()) {
                if (!aBoolean) {
                    reportScenarios.add(ReportScenario.builder()
                            .Scenario(scenario)
                            .threatSituation(-1)
                            .numberOfValues(0).build());
                }
            }
        });

        return reportScenarios;
    }

    /**
     * @param reportScenarios
     * @return the average ReportScenario from ReportScenario-List
     */
    public ReportScenario calculateReportScenarioAverage(List<ReportScenario> reportScenarios){
        float sumValue = 0;
        int dataAmountCounter = 0;
        for(ReportScenario reportScenario : reportScenarios){
            //Don't use Unknown values for the average and the dataAmount (threatSituation = -1)
            if(reportScenario.getThreatSituation() >= 0) {
                dataAmountCounter++;
                sumValue += reportScenario.getThreatSituation();
            }
        }

        //If the length is equal to 0 all threatSituations were unknown(so the new one should be too --> -1)
        float averageValue = -1;
        if(dataAmountCounter != 0){
            averageValue = sumValue / dataAmountCounter;
        }

        return ReportScenario.builder().threatSituation(averageValue).numberOfValues(dataAmountCounter).build();
    }

    /**
     *
     * @param impact
     * @param probability
     * @return ThreatSituation based on table from UP KRITIS
     */
    public long calculateThreatSituation(long impact, long probability) {
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
}
