var scenarioString =   '<div class="scenario" id="scenario{masterScenarioIndex}_{scenarioIndex}">\
                            <div className="moveScenario">\
                                   <button onClick="moveScenarioUp(this)" value="{masterScenarioIndex};{scenarioIndex}" type="button" class="moveScenarioButton btn btn-outline-primary"><i class="bi-arrow-up-square"></i></button>\
                                   <button onClick="moveScenarioDown(this)" value="{masterScenarioIndex};{scenarioIndex}" type="button" class="moveScenarioButton btn btn-outline-primary"><i class="bi-arrow-down-square"></i></button>\
                            </div>\
                            <input hidden="" id="masterScenarios{masterScenarioIndex}.scenarios{scenarioIndex}.id" name="masterScenarios[{masterScenarioIndex}].scenarios[{scenarioIndex}].id" value="{scenarioId}">\
                            <input class="positionInRow" hidden id="masterScenarios{masterScenarioIndex}.scenarios{scenarioIndex}.positionInRow" name="masterScenarios[{masterScenarioIndex}].scenarios[{scenarioIndex}].positionInRow" value="{positionInRow}">\
                            <textarea placeholder="Szenarioname..." class="form-control scenarioName" id="masterScenarios{masterScenarioIndex}.scenarios{scenarioIndex}.name" name="masterScenarios[{masterScenarioIndex}].scenarios[{scenarioIndex}].name"></textarea>\
                            <textarea placeholder="Szenariobeschreibung..." class="form-control scenarioDescription" id="masterScenarios{masterScenarioIndex}.scenarios{scenarioIndex}.description" name="masterScenarios[{masterScenarioIndex}].scenarios[{scenarioIndex}].description"></textarea>\
                            <div class="deleteScenario">\
                                <button class="btn btn-outline-danger" type="button" value="{masterScenarioIndex};{scenarioIndex}" onclick="deleteScenario(this)">Löschen</button>\
                            </div>\
                        </div>';

var masterScenarioString =  '<div class="masterScenario" id="masterScenario{masterScenarioIndex}">\
                                <div class="masterScenarioAttributes">\
                                    <input hidden="" id="masterScenarios{masterScenarioIndex}.id" name="masterScenarios[{masterScenarioIndex}].id" value="{masterScenarioId}">\
                                    <div class="select-masterScenarioName">\
                                        <input class="masterScenarioWeight" placeholder="Gewicht" id="masterScenarios{masterScenarioIndex}.positionInRow" name="masterScenarios[{masterScenarioIndex}].positionInRow" value="0">\
                                        <select onchange="onLayerChange(this)" style="background-color: #9fd8ee" class="form-select" id="masterScenarios{masterScenarioIndex}.layer" name="masterScenarios[{masterScenarioIndex}].layer">\
                                            <option style="background-color: #fe80a4" value="1">Kritische Dienstleistung</option>\
                                            <option style="background-color: #ffcf93" value="2">Zugriff</option>\
                                            <option style="background-color: #f6e37b" value="3">Zugang</option>\
                                            <option style="background-color: #b5d48c" value="4">Zutritt</option>\
                                            <option style="background-color: #9fd8ee" value="5" selected>Außenwelt</option>\
                                        </select>\
                                        <button class="deleteScenario btn btn-outline-danger" type="button" value="{masterScenarioIndex}" onclick="deleteMasterScenario(this)">Löschen</button>\
                                    </div>\
                                    <textarea placeholder="Masterszenarioname..." class="form-control masterScenarioName" id="masterScenarios{masterScenarioIndex}.name" name="masterScenarios[{masterScenarioIndex}].name"></textarea>\
                                </div>\
                                <div class="scenarios">\
                                    <div class="scenario" id="scenario{masterScenarioIndex}_0">\
                                    <div className="moveScenario">\
                                       <button onClick="moveScenarioUp(this)" value="{masterScenarioIndex};0" type="button" class="moveScenarioButton btn btn-outline-primary"><i class="bi-arrow-up-square"></i></button>\
                                       <button onClick="moveScenarioDown(this)" value="{masterScenarioIndex};0" type="button" class="moveScenarioButton btn btn-outline-primary"><i class="bi-arrow-down-square"></i></button>\
                                    </div>\
                                        <input hidden="" id="masterScenarios{masterScenarioIndex}.scenarios0.id" name="masterScenarios[{masterScenarioIndex}].scenarios[0].id" value="-1">\
                                        <input class="positionInRow" hidden id="masterScenarios{masterScenarioIndex}.scenarios0.positionInRow" name="masterScenarios[{masterScenarioIndex}].scenarios[0].positionInRow" value="1">\
                                          <textarea placeholder="Szenarioname..." class="form-control scenarioName" id="masterScenarios{masterScenarioIndex}.scenarios0.name" name="masterScenarios[{masterScenarioIndex}].scenarios[0].name"></textarea>\
                                        <textarea placeholder="Szenariobeschreibung..." class="form-control scenarioDescription" id="masterScenarios{masterScenarioIndex}.scenarios0.description" name="masterScenarios[{masterScenarioIndex}].scenarios[0].description"></textarea>\
                                        <div class="deleteScenario">\
                                            <button class="btn btn-outline-danger" type="button" value="{masterScenarioIndex};0" onclick="deleteScenario(this)">Löschen</button>\
                                        </div>\
                                    </div>\
                                    <button onclick="createScenario(this)" type="button" class="submitScenario btn btn-outline-primary addScenarioButton" value="{masterScenarioIndex};0">Scenario hinzufügen</button>\
                              </div>\
                            </div>';

function deleteScenario(element){
    //get the right scenario element
    let scenario = getScenarioElementFromValue(element.value);

    //confirm the deletion
    if(confirm("Wollen sie das Scenario wirklich löschen?")){
        let scenariosElement = element.closest(".scenarios");
        let positionsInRow = scenariosElement.getElementsByClassName("positionInRow");
        scenario.remove();
        //reset all positionsInRow
        for(let i = 0; i < positionsInRow.length; i++){
            positionsInRow[i].value = (i+1);
            console.log(positionsInRow[i].value, i);
        }
        //reset the AddButtons

    }
}

function deleteMasterScenario(element){
    //get the right indices of masterScenario from button value
    let masterScenarioIndex = parseInt(element.value);

    //get the right master scenario element
    let masterScenario = $("#masterScenario" + masterScenarioIndex);

    //confirm the deletion
    if(confirm("Wollen sie das MasterScenario wirklich löschen?")){
        masterScenario.remove();
    }

}

function createScenario(element){
    //get the right indices of scenario and masterScenario from button value
    let buttonValue = element.value;
    let buttonValues = buttonValue.split(";");
    let masterScenarioIndex = parseInt(buttonValues[0]);
    let scenarioIndex = parseInt(buttonValues[1]);

    //insert the new element
    element.parentElement.insertBefore(createNewScenario(masterScenarioIndex, scenarioIndex, element), element);

    //change the value of the button
    scenarioIndex++;
    element.value=masterScenarioIndex + ";" + scenarioIndex;
}

//creates and returns the right scenarioElement
function createNewScenario(lastMasterScenarioIndex, lastScenarioIndex, button){
    let scenarioIndex = lastScenarioIndex + 1;
    //calculate positionInRow by the last Element
    let positionInRows = button.closest(".scenarios").getElementsByClassName("positionInRow");
    console.log(positionInRows);
    let positionInRow = 1;
    //the position in row is 1 if there is no last element
    if(positionInRows.length > 0) {
        positionInRow = parseInt(positionInRows[positionInRows.length - 1].value) + 1;
    }
    //create the matching element as a string
    let newScenarioString = scenarioString.replaceAll("{masterScenarioIndex}", lastMasterScenarioIndex)
        .replaceAll("{scenarioIndex}", scenarioIndex)
        .replaceAll("{scenarioId}", - 1)
        .replaceAll("{positionInRow}", positionInRow);
    //insert it into a template element
    let template = document.createElement('template');
    template.innerHTML = newScenarioString;

    //return the element
    return template.content.firstChild;
}

function createMasterScenario(element){
    //get the right index from the button
    let masterScenarioIndex = parseInt(element.value);

    //insert the new element
    element.parentElement.insertBefore(createNewMasterScenario(masterScenarioIndex), element);

    //change the value of the button
    masterScenarioIndex++;
    element.value=masterScenarioIndex;
}

//creates and returns the right masterScenarioElement
function createNewMasterScenario(lastMasterScenarioIndex){
    let masterScenarioIndex = lastMasterScenarioIndex + 1;
    //create the matching element as a string
    let newMasterScenarioString = masterScenarioString.replaceAll("{masterScenarioIndex}", masterScenarioIndex).replaceAll("{masterScenarioId}", - 1);
    //insert it into a template element
    let template = document.createElement('template');
    template.innerHTML = newMasterScenarioString;

    //return the element
    return template.content.firstChild;
}

function moveScenarioUp(element){
    let scenario = getScenarioElementFromValue(element.value);
    //get the scenario element before
    let previousSibling = scenario.previousElementSibling;
    //only change if there is an element
    if(previousSibling != null){
        swapScenarioElements(previousSibling, scenario);
    }
}

function moveScenarioDown(element){
    let scenario = getScenarioElementFromValue(element.value);
    let nextSibling = scenario.nextElementSibling;
    //only change if there is an element
    if(nextSibling.tagName == "DIV"){
        swapScenarioElements(scenario, nextSibling);
    }
}

//returns the right scenario element from value (masterScenarioIndex;ScenarioIndex)
function getScenarioElementFromValue(value){
    let buttonValues = value.split(";");
    let masterScenarioIndex = parseInt(buttonValues[0]);
    let scenarioIndex = parseInt(buttonValues[1]);

    //get the right scenario element
    return $("#scenario" + masterScenarioIndex + "_" + scenarioIndex)[0];
}

//swaps the first given scenario with the second one
function swapScenarioElements(scenario1, scenario2){
    //swap the elements in dom
    scenario1.parentElement.insertBefore(scenario2,scenario1);

    //swap all important attributes
    //swap the ids of the scenarios
    let notice = scenario1.id;
    scenario1.id = scenario2.id;
    scenario2.id = notice;
    //swap the values of the move buttons
    notice = scenario1.firstElementChild.firstElementChild.value;
    scenario1.firstElementChild.firstElementChild.value = scenario2.firstElementChild.firstElementChild.value;
    scenario2.firstElementChild.firstElementChild.value = notice;
    notice = scenario1.firstElementChild.children[1].value;
    scenario1.firstElementChild.children[1].value = scenario2.firstElementChild.children[1].value;
    scenario2.firstElementChild.children[1].value = notice;
    //swap the values of the positionInRowElements
    let positionInRowElement1 = scenario1.getElementsByClassName("positionInRow")[0]
    let positionInRowElement2 = scenario2.getElementsByClassName("positionInRow")[0]
    notice = positionInRowElement1.value;
    positionInRowElement1.value = positionInRowElement2.value;
    positionInRowElement2.value = notice;
}

function onLayerChange(element){
    switch (parseInt(element.value)){
        case 5:
            element.style.backgroundColor = "#9fd8ee";
            break;
        case 4:
            element.style.backgroundColor ="#b5d48c";
            break;
        case 3:
            element.style.backgroundColor = "#f6e37b";
            break;
        case 2:
            element.style.backgroundColor = "#ffcf93";
            break;
        default:
            element.style.backgroundColor = "#fe80a4";
            break;
    }
}
