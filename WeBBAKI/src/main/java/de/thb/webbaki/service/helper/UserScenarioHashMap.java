package de.thb.webbaki.service.helper;

import de.thb.webbaki.entity.questionnaire.UserScenario;

import java.util.HashMap;
import java.util.Map;

/**
 * Is a LinkedList of ThreatSituations including the comment
 * of the Threatmatrix and the amount of used Questionnaires for this List
 */
public class UserScenarioHashMap extends HashMap<Long, UserScenario> {
    private String comment;
    private int questionnaireAmount;

    public UserScenarioHashMap(Map<Long, UserScenario> userScenarioMap, String comment, int questionnaireAmount){
        this.putAll(userScenarioMap);
        this.setComment(comment);
        this.setQuestionnaireAmount(questionnaireAmount);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getQuestionnaireAmount() {
        return questionnaireAmount;
    }

    public void setQuestionnaireAmount(int questionnaireAmount) {
        this.questionnaireAmount = questionnaireAmount;
    }
}
