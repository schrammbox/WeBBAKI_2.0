package de.thb.webbaki.service.helper;

import java.text.DecimalFormat;
import java.util.List;

public class ThreatSituation {
    private String symbol;
    private float value;
    private String color;
    private int dataAmount;

    public ThreatSituation(float value, int dataAmount){
        if(value <= -1){
            symbol = "Unbekannt";
            color = "white";
        }else if(value == 0){
            symbol = "keine Gefährdung";
            color = "white";
        }else if(value < 5){
            symbol = "geringe Gefährdung";
            color = "rgb(102, 255, 102)";
        }else if(value < 10){
            symbol = "erhöhte Gefährdung";
            color = "rgb(255, 255, 102)";
        }else if(value < 13){
            symbol = "hohe Gefährdung";
            color = "rgb(255, 178, 102)";
        }else{
            symbol = "sehr hohe Gefährdung";
            color = "rgb(255, 102, 102)";
        }
        setValue(value);
        this.dataAmount = dataAmount;
    }

    public ThreatSituation(float value){
        this(value, 0);
    }

    public static ThreatSituation getAverageThreatSituation(List<ThreatSituation> threatSituationList){
        float sumValue = 0;
        int dataAmountCounter = 0;
        for(ThreatSituation threatSituation : threatSituationList){
            //Dont use Unkown values for the average and the dataAmount
            if(threatSituation.getValue() >= 0) {
                dataAmountCounter++;
                sumValue += threatSituation.getValue();
            }
        }

        //If the length is equal to 0 all threatSituations were unknown(so the new one should be too --> -1)
        float averageValue = -1;
        if(dataAmountCounter != 0){
            averageValue = sumValue / dataAmountCounter;
        }

        return new ThreatSituation(averageValue, dataAmountCounter);
    }

    public void setValue(float value){
        if(value < - 1){
            this.value = -1;
        } else if(value > 16) {
            this.value = 16;
        }else{
            this.value = value;
        }
    }

    public float getValue() {
        return value;
    }

    public String getRoundedValueString(){
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return decimalFormat.format(value);
    }

    public String getSymbol() {
        return symbol;
    }

    public String getColor() {
        return color;
    }

    public int getDataAmount() {
        return dataAmount;
    }

}
