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

    let probability = probabilitySelect.value;
    let impact = impactSelect.value;

    let probabilityNum, impactNum, result = 0;
    switch (probability) {
        case "none":
            probabilityNum = -1;
            break;
        case 'nie':
            probabilityNum = 0;
            break;
        case 'selten':
            probabilityNum = 1;
            break;
        case 'mittel':
            probabilityNum = 2;
            break;
        case 'haeufig':
            probabilityNum = 3;
            break;
        case 'sehr haeufig':
            probabilityNum = 4;
            break;
    }
    switch (impact) {
        case "none":
            impactNum = -1;
            break;
        case 'keine':
            impactNum = 1
            break;
        case 'geringe':
            impactNum = 2
            break;
        case 'hohe':
            impactNum = 3
            break;
        case 'existenzielle':
            impactNum = 4
            break;
    }

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