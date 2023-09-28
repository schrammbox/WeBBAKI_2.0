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

    let result = probabilityNum * impactNum;

    if(probabilityNum <= -1 || impactNum <= -1){
        threatSituationDiv.textContent = "Unbekannt";
        threatSituationDiv.style.backgroundColor = "white";
    }else if(result <= 7){
        threatSituationDiv.textContent = "geringe Gefährdung";
        threatSituationDiv.style.backgroundColor = "rgb(179, 255, 179)";
    }else if(result <= 23){
        threatSituationDiv.textContent = "erhöhte Gefährdung";
        threatSituationDiv.style.backgroundColor = "rgb(255, 255, 179)";
    }else if(result <= 47){
        threatSituationDiv.textContent = "hohe Gefährdung";
        threatSituationDiv.style.backgroundColor = "rgb(255, 204, 153)";
    }else{
        threatSituationDiv.textContent = "sehr hohe Gefährdung";
        threatSituationDiv.style.backgroundColor = "rgb(255, 153, 153)";
    }
}

function onThreatSituationChange(element){
    calculateThreatSituation(element.parentElement.parentElement);
}

function onCommentChange(element){
    //delete old class and add new one based on the emptiness of the textarea
    let text = element.value.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "")

    if(!text || text.length == 0){
        if(element.previousElementSibling.classList.contains("bi-chat-left-text")){
            element.previousElementSibling.classList.remove("bi-chat-left-text")
            element.previousElementSibling.classList.add("bi-chat-left")
        }
    }else{
        if(element.previousElementSibling.classList.contains("bi-chat-left")){
            element.previousElementSibling.classList.remove("bi-chat-left")
            element.previousElementSibling.classList.add("bi-chat-left-text")
        }
    }
}