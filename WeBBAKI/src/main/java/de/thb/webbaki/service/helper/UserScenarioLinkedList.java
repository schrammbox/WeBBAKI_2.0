package de.thb.webbaki.service.helper;

import de.thb.webbaki.entity.UserScenario;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Is a LinkedList of ThreatSituations including the comment
 * of the Threatmatrix and the amount of used Questionnaires for this List
 */
public class UserScenarioLinkedList extends LinkedList<UserScenario> {
    String comment;
    int questionnaireAmount;

    public UserScenarioLinkedList(Collection<UserScenario> userScenarioCollection, String comment, int questionnaireAmount){
        this.addAll(userScenarioCollection);
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
