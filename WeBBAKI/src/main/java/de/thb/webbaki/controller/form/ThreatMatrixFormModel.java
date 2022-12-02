package de.thb.webbaki.controller.form;

import de.thb.webbaki.entity.questionnaire.Questionnaire;
import de.thb.webbaki.entity.questionnaire.UserScenario;
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
    List<UserScenario> userScenarios;
    String comment;

    public ThreatMatrixFormModel(Questionnaire questionnaire){

        scenarioIdToIndex = new HashMap<>();
        comment = questionnaire.getComment();
        userScenarios = new ArrayList<>();

        userScenarios = questionnaire.getUserScenarios();

        for(int i = 0; i< userScenarios.size(); i++){
            scenarioIdToIndex.put(userScenarios.get(i).getScenario().getId(), i);
        }
    }
}
