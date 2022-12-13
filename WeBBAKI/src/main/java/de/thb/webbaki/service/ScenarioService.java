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
     * 0 means it is a deleted one
     */
    public void saveAndDeleteScenariosFromForm(ScenarioFormModel form){
        List<MasterScenario> masterScenarios = form.getMasterScenarios();
        //list of masterScenarios that have to be saved
        List<MasterScenario> masterScenarioSaveList = new ArrayList<>();
        //list of masterScenarios that have to be saved
        List<Scenario> scenarioSaveList = new ArrayList<>();

        //list of masterScenarios that have to be deleted (start with all and remove existing elements)
        List<MasterScenario> masterScenarioDeleteList = masterScenarioService.getAllMasterScenarios();
        //list of scenarios that have to be deleted (start with all and remove existing elements)
        List<Scenario> scenarioDeleteList = scenarioRepository.findAll();

        //go through all masterScenarios of the form
        if(masterScenarios != null) {
            for (MasterScenario masterScenario : masterScenarios) {
                //a new masterScenario
                if (masterScenario.getId() == -1) {
                    //change id to 0 for jpa (otherwise it would search for the id -1)
                    masterScenario.setId(0);
                    //is active
                    masterScenario.setActive(true);
                    //add masterScenario to the save list
                    masterScenarioSaveList.add(masterScenario);

                    //save all new Scenarios
                    if (masterScenario.getScenarios() != null) {
                        for (Scenario scenario : masterScenario.getScenarios()) {
                            if (scenario.getId() == -1) {//only create Scenario which aren't deleted before(elements with id = 0)
                                //change id to 0 for jpa (otherwise it would search for the id -1)
                                scenario.setId(0);
                                //is active
                                scenario.setActive(true);
                                scenario.setMasterScenario(masterScenario);
                                //add scenario to the save list
                                scenarioSaveList.add(scenario);
                            }
                        }
                    }

                } else if (masterScenario.getId() != 0) {//an old MasterScenario
                    //is active
                    masterScenario.setActive(true);
                    //remove this one from the delete-list (it was not deleted)
                    masterScenarioDeleteList.remove(masterScenario);
                    //add this one to the save-list (could have changes)
                    masterScenarioSaveList.add(masterScenario);

                    //go through all the scenarios of this masterScenario
                    if (masterScenario.getScenarios() != null) {
                        for (Scenario scenario : masterScenario.getScenarios()) {
                            scenario.setActive(true);
                            scenario.setMasterScenario(masterScenario);
                            if (scenario.getId() == -1) {//only create Scenario which aren't deleted before(elements with id = 0)
                                //change id to 0 for jpa (otherwise it would search for the id -1)
                                scenario.setId(0);
                                //add scenario to the save list
                                scenarioSaveList.add(scenario);
                            } else if (scenario.getId() != 0) {
                                //remove this one from the delete-list (it was not deleted)
                                scenarioDeleteList.remove(scenario);
                                //add this one to the save-list (could have changes)
                                scenarioSaveList.add(scenario);
                            }
                        }
                    }
                }
            }
        }
        //go through the deleted scenarios and masterScenarios, set them as not active and add them to the saveLists
        for(MasterScenario masterScenario : masterScenarioDeleteList){
            if(masterScenario.isActive()) {
                masterScenario.setActive(false);
                masterScenarioSaveList.add(masterScenario);
            }
        }
        for(Scenario scenario : scenarioDeleteList){
            if(scenario.isActive()) {
                scenario.setActive(false);
                scenarioSaveList.add(scenario);
            }
        }

        //save all masterScenarios and Scenarios
        masterScenarioService.saveALlMasterScenarios(masterScenarioSaveList);
        scenarioRepository.saveAll(scenarioSaveList);


    }
}
