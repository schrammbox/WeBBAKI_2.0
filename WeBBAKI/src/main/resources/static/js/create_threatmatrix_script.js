$(document).ready(function (){
    //go through all tr-elements and calculate threatsituation
    trList = $(".threatTr");
    for(let i = 0; i < trList.length; i++){
        calculateThreatSituation(trList[i]);
    }
});

//calculates threatSituation from impact and probability, write it to the div and change to the right color
function calculateThreatSituation(trParent){

    let probabilitySelect = trParent.getElementsByClassName("probabilitySelect")[0];
    let impactSelect = trParent.getElementsByClassName("impactSelect")[0];
    let threatSituationDiv = trParent.getElementsByClassName("threatsituationDiv")[0];

    let probabilityNum = probabilitySelect.value;
    let impactNum = impactSelect.value;

    result = 0;

    if(probabilityNum < 0 || impactNum < 0){
        result = -1;
    }else if(probabilityNum == 4 && impactNum == 2){
        result = 6;
    }else if(probabilityNum == 4 && impactNum == 1){
        result = 3;
    }else{
        result = probabilityNum * impactNum;
    }

    if(result <= -1){
        threatSituationDiv.textContent = "Unbekannt";
        threatSituationDiv.style.backgroundColor = "white";
    }else if(result == 0){
        threatSituationDiv.textContent = "keine Gefährdung";
        threatSituationDiv.style.backgroundColor = "white";
    }else if(result < 5){
        threatSituationDiv.textContent = "geringe Gefährdung";
        threatSituationDiv.style.backgroundColor = "rgb(102, 255, 102)";
    }else if(result < 10){
        threatSituationDiv.textContent = "erhöhte Gefährdung";
        threatSituationDiv.style.backgroundColor = "rgb(255, 255, 102)";
    }else if(result < 13){
        threatSituationDiv.textContent = "hohe Gefährdung";
        threatSituationDiv.style.backgroundColor = "rgb(255, 178, 102)";
    }else{
        threatSituationDiv.textContent = "sehr hohe Gefährdung";
        threatSituationDiv.style.backgroundColor = "rgb(255, 102, 102)";
    }
}

function onChange(element){
    calculateThreatSituation(element.parentElement.parentElement);
}