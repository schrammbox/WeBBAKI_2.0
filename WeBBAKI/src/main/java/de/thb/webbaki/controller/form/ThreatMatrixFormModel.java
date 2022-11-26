package de.thb.webbaki.controller.form;

import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.UserScenario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatMatrixFormModel {
    Map<Long, Integer> scenarioIdToIndex;
    List<Float> probabilities;
    List<Float> impacts;
    List<String> smallComments;
    String comment;

    public ThreatMatrixFormModel(Questionnaire questionnaire){

        scenarioIdToIndex = new HashMap<>();
        comment = questionnaire.getComment();
        probabilities = new ArrayList<>();
        impacts = new ArrayList<>();
        smallComments = new ArrayList<>();
        List<UserScenario> userScenarios = questionnaire.getUserScenarios();

        for(int i = 0; i< userScenarios.size(); i++){
            Long userScenarioId = userScenarios.get(i).getId();
            probabilities.add(userScenarios.get(i).getProbability());
            impacts.add(userScenarios.get(i).getImpact());
            smallComments.add(userScenarios.get(i).getSmallComment());
            scenarioIdToIndex.put(userScenarioId, i);
        }
    }
}
