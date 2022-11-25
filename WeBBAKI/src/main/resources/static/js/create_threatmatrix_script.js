$(document).ready(function (){
    //calculate threatsituation for every tr-element
    trList = $(".threatTr");
    for(let i = 0; i < trList.length; i++){
        calculateThreatSituation(trList[i]);
    }
});

function calculateThreatSituation(trParent){

    let probabilitySelect = trParent.getElementsByClassName("probabilitySelect")[0];
    let impactSelect = trParent.getElementsByClassName("impactSelect")[0];
    let threatsituationDiv = trParent.getElementsByClassName("threatsituationDiv")[0];

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
        threatsituationDiv.textContent = "Unbekannt";
        threatsituationDiv.style.backgroundColor = "white";
    }else if(result == 0){
        threatsituationDiv.textContent = "keine Gefährdung";
        threatsituationDiv.style.backgroundColor = "white";
    }else if(result < 5){
        threatsituationDiv.textContent = "geringe Gefährdung";
        threatsituationDiv.style.backgroundColor = "rgb(102, 255, 102)";
    }else if(result < 10){
        threatsituationDiv.textContent = "erhöhte Gefährdung";
        threatsituationDiv.style.backgroundColor = "rgb(255, 255, 102)";
    }else if(result < 13){
        threatsituationDiv.textContent = "hohe Gefährdung";
        threatsituationDiv.style.backgroundColor = "rgb(255, 178, 102)";
    }else{
        threatsituationDiv.textContent = "sehr hohe Gefährdung";
        threatsituationDiv.style.backgroundColor = "rgb(255, 102, 102)";
    }
}
function onChange(element){
    calculateThreatSituation(element.parentElement.parentElement);
}