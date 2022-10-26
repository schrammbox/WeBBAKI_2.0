package de.thb.webbaki.service.helper;

import java.util.Collection;
import java.util.LinkedList;

public class ThreatSituationLinkedList extends LinkedList<ThreatSituation> {
    String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ThreatSituationLinkedList(Collection<ThreatSituation> threatSituationCollection, String comment){
        this.addAll(threatSituationCollection);
        this.setComment(comment);
    }
}
