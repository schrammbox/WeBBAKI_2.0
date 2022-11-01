package de.thb.webbaki.service.helper;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Is a LinkedList of ThreatSituations including the comment
 * of the Threatmatrix and the amount of used Questionnaires for this List
 */
public class ThreatSituationLinkedList extends LinkedList<ThreatSituation> {
    String comment;
    int questionnaireAmount;

    public ThreatSituationLinkedList(Collection<ThreatSituation> threatSituationCollection, String comment, int questionnaireAmount){
        this.addAll(threatSituationCollection);
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
