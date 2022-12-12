package de.thb.webbaki.controller;

import de.thb.webbaki.controller.form.ScenarioFormModel;
import de.thb.webbaki.entity.MasterScenario;
import de.thb.webbaki.service.MasterScenarioService;
import de.thb.webbaki.service.ScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ScenarioController {
    @Autowired
    MasterScenarioService masterScenarioService;
    @Autowired
    ScenarioService scenarioService;
    @GetMapping("/scenarios")
    public String showScenarios(Model model){
        List<MasterScenario> masterScenarios = masterScenarioService.getAllMasterScenarios();
        ScenarioFormModel scenarioFormModel = new ScenarioFormModel(masterScenarios);
        model.addAttribute("form", scenarioFormModel);
        return "scenarios";
    }

    @PostMapping("/scenarios")
    public String changeScenarios(ScenarioFormModel scenarioFormModel){
        scenarioService.saveAndDeleteScenariosFromForm(scenarioFormModel);
        return "redirect:/scenarios";
    }
}
