package de.thb.webbaki.service;


import de.thb.webbaki.controller.form.ScenarioFormModel;
import de.thb.webbaki.entity.MasterScenario;
import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.repository.ScenarioRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Builder
@AllArgsConstructor
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;
    @Autowired
    private MasterScenarioService masterScenarioService;

    public List<Scenario> getAllScenarios(){return (List<Scenario>) scenarioRepository.findAll();}

    public void addScenario(Scenario s){scenarioRepository.save(s); }

    public long getNumberOfScenarios(){return scenarioRepository.count();}

    public Scenario getById(long id){return scenarioRepository.findById(id);}

    public List<Scenario> getAllScenariosByActiveTrue(){return scenarioRepository.findByActive(true);}

    public List<String> getAllDescriptions(){
        List<Scenario> allScenarios = getAllScenarios();
        List<String> allDescriptions = null;

        for(Scenario scenario : allScenarios){
            allDescriptions.add(scenario.getDescription());
        }
        return allDescriptions;
    }

    /**
     * Creates new and deletes old MasterScenarios and Scenarios by
     * @param form
     * -1 as id means it is a new element
     * 0 means there was a deleted one
     */
    public void saveAndDeleteScenariosFromForm(ScenarioFormModel form){
        //list of alle MasterScenarios from the form
        List<MasterScenario> masterScenarios = form.getMasterScenarios();
        //list of all active masterScenarios
        List<MasterScenario> masterScenarioUpdateList = masterScenarioService.getAllMasterScenariosByActiveTrue();
        //list of all active scenarios
        List<Scenario> scenarioUpdateList = getAllScenariosByActiveTrue();

        //set all masterScenarios to inactive (so we now later which are not represented in the form and update them as inactive)
        for(MasterScenario masterScenario : masterScenarioUpdateList){ masterScenario.setActive(false); }
        //set scenarios to inactive (so we now later which are not represented in the form and update them as inactive)
        for(Scenario scenario : scenarioUpdateList){ scenario.setActive(false); }

        //go through all masterScenarios of the form
        if(masterScenarios != null) {
            for (MasterScenario masterScenario : masterScenarios) {
                if (masterScenario.getId() != 0) {
                    //is active
                    masterScenario.setActive(true);
                    if (masterScenario.getId() == -1) {//a new masterScenario
                        //change id to 0 for jpa (otherwise it would search for the id -1)
                        masterScenario.setId(0);
                        //save this new MasterScenario in the update List
                        masterScenarioUpdateList.add(masterScenario);
                    }else{
                        //find the masterScenario in the update List with the right id
                        MasterScenario oldMasterScenario = masterScenarioUpdateList.get(masterScenarioUpdateList.indexOf(masterScenario));
                        //check if the attributes changed or not
                        if(oldMasterScenario.getName().equals(masterScenario.getName()) && oldMasterScenario.getLayer() == masterScenario.getLayer()){
                            //delete the masterScenario from updateList if nothing changed
                            masterScenarioUpdateList.remove(masterScenario);
                        }else{//if something changed
                            //activate the masterScenario
                            masterScenario.setActive(true);
                            //change id to 0 for jpa (otherwise it would update the old and not create a new one)
                            masterScenario.setId(0);
                            //add the masterScenario from the form to the UpdateList
                            masterScenarioUpdateList.add(masterScenario);
                            //reset all Scenarios as new ones
                            for(Scenario scenario : masterScenario.getScenarios()){
                                //change id to 0 for jpa
                                scenario.setId(-1);
                            }
                        }
                    }

                    //go through all the scenarios of this masterScenario
                    if (masterScenario.getScenarios() != null) {
                        for (Scenario scenario : masterScenario.getScenarios()) {
                            scenario.setMasterScenario(masterScenario);
                            if (scenario.getId() == -1) {//only create Scenario which aren't deleted before (known by elements with id = 0)
                                //activate the scenario
                                scenario.setActive(true);
                                //change id to 0 for jpa (otherwise it would search for the id -1)
                                scenario.setId(0);
                                //add scenario to the update list
                                scenarioUpdateList.add(scenario);
                            } else if (scenario.getId() != 0) {
                                //find the scenario in the update List with the right id
                                Scenario oldScenario = scenarioUpdateList.get(scenarioUpdateList.indexOf(scenario));

                                //check if the attributes changed or not
                                if(scenario.getName().equals(oldScenario.getName()) && scenario.getDescription().equals(oldScenario.getDescription())){
                                    //delete the scenario from updateList if nothing changed
                                    scenarioUpdateList.remove(scenario);
                                }else{//if something changed
                                    //activate the masterScenario
                                    scenario.setActive(true);
                                    //change id to 0 for jpa (otherwise it would update the old and not create a new one)
                                    scenario.setId(0);
                                    //add the scenario from the form to the UpdateList
                                    scenarioUpdateList.add(scenario);
                                }
                            }
                        }
                    }
                }
            }
        }
        //save all masterScenarios and Scenarios
        masterScenarioService.saveALlMasterScenarios(masterScenarioUpdateList);
        scenarioRepository.saveAll(scenarioUpdateList);


    }
}
