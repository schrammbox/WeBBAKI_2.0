package de.thb.webbaki.entity.snapshot;

import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.entity.User;
import lombok.*;

import javax.persistence.*;
import java.text.DecimalFormat;

/**
 * A ReportScenario is one result row of a Report.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "report_scenario")
public class ReportScenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    float threatSituation;
    int numberOfValues;



    @ManyToOne
    @JoinColumn(name="scenario_id", nullable = false)
    private Scenario Scenario;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;


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

    public String getRoundedThreatSituationString(){
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return decimalFormat.format(threatSituation);
    }

}
