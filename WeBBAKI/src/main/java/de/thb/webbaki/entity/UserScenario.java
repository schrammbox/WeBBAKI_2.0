package de.thb.webbaki.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserScenario{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private float probability;

    private float impact;

    private float threatSituation;

    @Column(length = 1000)
    @Size(max = 10000)
    private String smallComment;

    @ManyToOne
    private Scenario scenario;

    @ManyToOne
    @JoinColumn(name="questionnaire_id", nullable=false)
    private Questionnaire questionnaire;

    public String getStringRepresentation() {
        if(threatSituation <= -1){
            return "Unbekannt";
        }else if(threatSituation == 0){
            return "keine Gefährdung";
        }else if(threatSituation < 5){
            return "geringe Gefährdung";
        }else if(threatSituation < 10){
            return "erhöhte Gefährdung";
        }else if(threatSituation < 13){
            return "hohe Gefährdung";
        }else{
            return "sehr hohe Gefährdung";
        }
    }

    public String getColor(){
        if(threatSituation <= -1){
            return "white";
        }else if(threatSituation == 0){
            return "white";
        }else if(threatSituation < 5){
            return "rgb(102, 255, 102)";
        }else if(threatSituation < 10){
            return "rgb(255, 255, 102)";
        }else if(threatSituation < 13){
            return "rgb(255, 178, 102)";
        }else{
            return "rgb(255, 102, 102)";
        }
    }
}
