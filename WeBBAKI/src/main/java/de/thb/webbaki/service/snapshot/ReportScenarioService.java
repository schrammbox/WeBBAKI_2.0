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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReportScenarioService {
    @Autowired
    ReportScenarioRepository reportScenarioRepository;

    public void saveAllReportScenarios(List<ReportScenario> reportScenarios){reportScenarioRepository.saveAll(reportScenarios);}

    /**
     * Takes the respective impacts and probabilities, calculates the threatsituations, create ReportScenarios from this
     * @param userScenarios
     * @return the created ReportScenarios
     */
    public List<ReportScenario> calculateReportScenariosFromUserScenarios(List<UserScenario> userScenarios){
        List<ReportScenario> reportScenarios = new ArrayList<>();

        for(UserScenario userScenario : userScenarios){
            float threatSituation = calculateThreatSituation((long)userScenario.getImpact(), (long)userScenario.getProbability());
            reportScenarios.add(ReportScenario.builder()
                    .threatSituation(threatSituation)
                    .Scenario(userScenario.getScenario())
                    .numberOfValues((threatSituation == -1) ? 0 : 1).build());
        }

        return reportScenarios;
    }

    public ReportScenario calculateReportScenarioAverage(List<ReportScenario> reportScenarios){
        float sumValue = 0;
        int dataAmountCounter = 0;
        for(ReportScenario reportScenario : reportScenarios){
            //Dont use Unkown values for the average and the dataAmount
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
