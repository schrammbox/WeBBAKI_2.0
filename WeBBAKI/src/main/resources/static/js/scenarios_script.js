var scenarioString = "<div class=\"scenario\" id=\"scenario{masterScenarioIndex}_{scenarioIndex}\">\n" +
    "              <input hidden=\"\" id=\"masterScenarios{masterScenarioIndex}.scenarios{scenarioIndex}.id\" name=\"masterScenarios[{masterScenarioIndex}].scenarios[{scenarioIndex}].id\" value=\"{scenarioId}\">\n" +
    "              <textarea placeholder=\"Szenarioname...\" class=\"form-control scenarioName\" id=\"masterScenarios{masterScenarioIndex}.scenarios{scenarioIndex}.name\" name=\"masterScenarios[{masterScenarioIndex}].scenarios[{scenarioIndex}].name\"></textarea>\n" +
    "              <textarea placeholder=\"Szenariobeschreibung...\" class=\"form-control scenarioDescription\" id=\"masterScenarios{masterScenarioIndex}.scenarios{scenarioIndex}.description\" name=\"masterScenarios[{masterScenarioIndex}].scenarios[{scenarioIndex}].description\"></textarea>\n" +
    "              <div class=\"deleteScenario\">\n" +
    "                <button class=\"btn btn-outline-danger\" type=\"button\" value=\"{masterScenarioIndex};{scenarioIndex}\" onclick=\"deleteScenario(this)\">Löschen</button>\n" +
    "              </div>\n" +
    "            </div>"

var masterScenarioString = "<div class=\"masterScenario\" id=\"masterScenario{masterScenarioIndex}\">\n" +
    "          <div class=\"masterScenarioAttributes\">\n" +
    "            <input hidden=\"\" id=\"masterScenarios{masterScenarioIndex}.id\" name=\"masterScenarios[{masterScenarioIndex}].id\" value=\"{masterScenarioId}\">\n" +
    "            <textarea placeholder=\"Masterszenarioname...\" class=\"form-control masterScenarioName\" id=\"masterScenarios{masterScenarioIndex}.name\" name=\"masterScenarios[{masterScenarioIndex}].name\"></textarea>\n" +
    "            <div class=\"select-masterScenarioName\">\n" +
    "              <select class=\"form-select\" id=\"masterScenarios{masterScenarioIndex}.layer\" name=\"masterScenarios[{masterScenarioIndex}].layer\">\n" +
    "                <option value=\"1\">Kritische Dienstleistung</option>\n" +
    "                <option value=\"2\">Zugriff</option>\n" +
    "                <option value=\"3\" >Zugang</option>\n" +
    "                <option value=\"4\">Zutritt</option>\n" +
    "                <option value=\"5\" selected=\"selected\">Außenwelt</option>\n" +
    "              </select>\n" +
    "              <button class=\"deleteScenario btn btn-outline-danger\" type=\"button\" value=\"{masterScenarioIndex}\" onclick=\"deleteMasterScenario(this)\">Löschen</button>\n" +
    "            </div>\n" +
    "          </div>\n" +
    "          <div class=\"scenarios\">\n" +
    "            <div class=\"scenario\" id=\"scenario{masterScenarioIndex}_0\">\n" +
    "              <input hidden=\"\" id=\"masterScenarios{masterScenarioIndex}.scenarios0.id\" name=\"masterScenarios[{masterScenarioIndex}].scenarios[0].id\" value=\"-1\">\n" +
    "              <textarea placeholder=\"Szenarioname...\" class=\"form-control scenarioName\" id=\"masterScenarios{masterScenarioIndex}.scenarios0.name\" name=\"masterScenarios[{masterScenarioIndex}].scenarios[0].name\"></textarea>\n" +
    "              <textarea placeholder=\"Szenariobeschreibung...\" class=\"form-control scenarioDescription\" id=\"masterScenarios{masterScenarioIndex}.scenarios0.description\" name=\"masterScenarios[{masterScenarioIndex}].scenarios[0].description\">" +
    "</textarea>\n" +
    "              <div class=\"deleteScenario\">\n" +
    "                <button class=\"btn btn-outline-danger\" type=\"button\" value=\"{masterScenarioIndex};0\" onclick=\"deleteScenario(this)\">Löschen</button>\n" +
    "              </div>\n" +
    "            </div>\n" +
    "            \n" +
    "            <button onclick=\"createScenario(this)\" type=\"button\" class=\"submitScenario btn btn-outline-primary addScenarioButton\" value=\"{masterScenarioIndex};0\">Scenario hinzufügen</button>\n" +
    "          </div>\n" +
    "        </div>"

function deleteScenario(element){
    //get the right indices of scenario and masterScenario from button value
    let buttonValue = element.value;
    let buttonValues = buttonValue.split(";");
    let masterScenarioIndex = parseInt(buttonValues[0]);
    let scenarioIndex = parseInt(buttonValues[1]);

    //get the right scenario element
    let scenario = $("#scenario" + masterScenarioIndex + "_" + scenarioIndex);

    //confirm the deletion
    if(confirm("Wollen sie das Scenario wirklich löschen?")){
        scenario.remove();
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
    element.parentElement.insertBefore(createNewScenario(masterScenarioIndex, scenarioIndex), element);

    //change the value of the button
    scenarioIndex++;
    element.value=masterScenarioIndex + ";" + scenarioIndex;
}

//creates and returns the right scenarioElement
function createNewScenario(lastMasterScenarioIndex, lastScenarioIndex){
    let scenarioIndex = lastScenarioIndex + 1;
    //create the matching element as a string
    let newScenarioString = scenarioString.replaceAll("{masterScenarioIndex}", lastMasterScenarioIndex).replaceAll("{scenarioIndex}", scenarioIndex).replaceAll("{scenarioId}", - 1);
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