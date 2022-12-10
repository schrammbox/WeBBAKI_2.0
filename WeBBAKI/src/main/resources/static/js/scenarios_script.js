function deleteElement(element){
    //search for the parent masterScenario or Scenario class and delete

    let scenario = element;
    do{
        scenario = scenario.parentElement;
    }while(!scenario.classList.contains("masterScenario") && !scenario.classList.contains("scenarioAttributes"));
    let parent = scenario.parentElement;
    parent.removeChild(scenario);
}