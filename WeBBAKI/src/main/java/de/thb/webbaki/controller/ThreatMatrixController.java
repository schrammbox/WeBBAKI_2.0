package de.thb.webbaki.controller;

import de.thb.webbaki.controller.form.ThreatMatrixFormModel;
import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.service.MasterScenarioService;
import de.thb.webbaki.service.QuestionnaireService;
import de.thb.webbaki.service.ScenarioService;
import de.thb.webbaki.service.UserService;
import de.thb.webbaki.service.helper.Counter;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Map;

@Controller
@AllArgsConstructor
public class ThreatMatrixController {

    private final QuestionnaireService questionnaireService;
    private final MasterScenarioService masterScenarioService;
    private final ScenarioService scenarioService;
    private final UserService userService;



    @GetMapping("/threatmatrix")
    public String showQuestionnaireForm(Model model,Authentication authentication) {

        final var masterScenarioList = masterScenarioService.getAllMasterScenarios();
        model.addAttribute("masterScenarioList",masterScenarioList);

        Questionnaire quest = questionnaireService.getNewestQuestionnaireByUserId(userService.getUserByUsername(authentication.getName()).getId());
        model.addAttribute("quest", quest);

        ThreatMatrixFormModel threatMatrixFormModel = ThreatMatrixFormModel.builder().smallComments(quest.getSmallComment().replace("[","").replace("]", "").split(",")).build();
        model.addAttribute("threatmatrix", threatMatrixFormModel);
        model.addAttribute("value", new Counter());

        Map<Long, String[]> questMap = questionnaireService.getMapping(quest);
        model.addAttribute("questMap", questMap);

        return "threatmatrix/create_threatmatrix";
    }
    @PostMapping("/threatmatrix")
    public String submitQuestionnaire(@ModelAttribute("threatmatrix") @Valid ThreatMatrixFormModel questionnaireFormModel,
                                      BindingResult result, Authentication authentication,
                                      RedirectAttributes redirectAttributes) {

        if (userService.getUserByUsername(authentication.getName()) != null){
            User user = userService.getUserByUsername(authentication.getName());
            questionnaireFormModel.setUser(user);
            questionnaireService.saveQuestionaire(questionnaireFormModel);
        }

        return "redirect:/threatmatrix/chronic";
    }


    @GetMapping("/threatmatrix/chronic")
    public String showQuestChronic(Authentication authentication,Model model) {

        if (userService.getUserByUsername(authentication.getName()) != null) {
            User user = userService.getUserByUsername(authentication.getName());
            final var questList = questionnaireService.getAllQuestByUser(user.getId());
            model.addAttribute("questList", questList);

        }
        return "threatmatrix/chronic";
    }

    @GetMapping("/threatmatrix/open/{questID}")
    public String showThreatMatrixByID(@PathVariable("questID") long questID, Model model) {

        Questionnaire quest = questionnaireService.getQuestionnaire(questID);
        model.addAttribute("quest", quest);

        Map<Long, String[]> questMap = questionnaireService.getMapping(quest);
        model.addAttribute("questMap", questMap);

        // NEEDED
        ThreatMatrixFormModel threatMatrixFormModel = ThreatMatrixFormModel.builder().smallComments(quest.getSmallComment().replace("[","").replace("]", "").split(",")).build();
        model.addAttribute("threatmatrix", threatMatrixFormModel);
        model.addAttribute("value", new Counter());

        final var masterScenarioList = masterScenarioService.getAllMasterScenarios();
        model.addAttribute("masterScenarioList",masterScenarioList);

        return "threatmatrix/create_threatmatrix";
    }


    @Transactional
    @GetMapping(path = "/threatmatrix/chronic/{questID}")
    public String deleteQuestionnaireByID(@PathVariable("questID") long questID){
        questionnaireService.delQuest(questID);

        return "redirect:/threatmatrix/chronic";
    }

    @GetMapping("/threatmatrix/add_szenario")
    public String addSzenario(Model model){
        final var masterScenarioList = masterScenarioService.getAllMasterScenarios();
        model.addAttribute("masterScenarioList",masterScenarioList);

        Scenario scenario = new Scenario();
        model.addAttribute("scenario",scenario);

        return "threatmatrix/add_szenario";
    }

    @PostMapping("/threatmatrix/add_szenario")
    public String submitSzenario(@ModelAttribute("scenario") Scenario scenario){
        String[] sList = scenario.getName().split(",");
        scenario.setName(sList[0]);
        scenario.setMasterScenario(masterScenarioService.getAllMasterScenarios()
                .get(Integer.parseInt(sList[1])));
        scenarioService.addScenario(scenario);


        return "redirect:/threatmatrix";
    }


}