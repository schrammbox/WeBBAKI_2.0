package de.thb.webbaki.controller.form;

import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.UserScenario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatMatrixFormModel {
    List<Float> probabilities;
    List<Float> impacts;
    List<String> smallComments;
    List<Long> scenarioIds;
    String comment;

    public ThreatMatrixFormModel(Questionnaire questionnaire){

        comment = questionnaire.getComment();
        probabilities = new ArrayList<>();
        impacts = new ArrayList<>();
        smallComments = new ArrayList<>();
        scenarioIds = new ArrayList<>();
        //TODO solve Problem with not enough UserScenarios
        for(UserScenario userScenario : questionnaire.getUserScenarios()){
            probabilities.add(userScenario.getProbability());
            impacts.add(userScenario.getImpact());
            scenarioIds.add(userScenario.getScenario().getId());
            smallComments.add(userScenario.getSmallComment());
        }
    }
}
