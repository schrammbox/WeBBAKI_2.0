package de.thb.webbaki.service.helper;

import de.thb.webbaki.entity.questionnaire.UserScenario;
import de.thb.webbaki.entity.snapshot.Report;
import de.thb.webbaki.entity.snapshot.ReportScenario;

import java.util.HashMap;
import java.util.Map;

/**
 * Is a LinkedList of ThreatSituations including the comment
 * of the Threatmatrix and the amount of used Questionnaires for this List
 */
public class ReportScenarioHashMap extends HashMap<Long, ReportScenario> {
    private String comment;
    private int numberOfQuestionnaires;

    public ReportScenarioHashMap(Map<Long, ReportScenario> reportScenarioMap, String comment, int numberOfQuestionnaires){
        this.putAll(reportScenarioMap);
        this.setComment(comment);
        this.setNumberOfQuestionnaires(numberOfQuestionnaires);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getNumberOfQuestionnaires() {
        return numberOfQuestionnaires;
    }

    public void setNumberOfQuestionnaires(int numberOfQuestionnaires) {
        this.numberOfQuestionnaires = numberOfQuestionnaires;
    }
}
