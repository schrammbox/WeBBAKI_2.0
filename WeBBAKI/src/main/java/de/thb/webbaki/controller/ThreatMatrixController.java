package de.thb.webbaki.controller;

import de.thb.webbaki.controller.form.ThreatMatrixFormModel;
import de.thb.webbaki.entity.questionnaire.Questionnaire;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.service.Exceptions.NotAuthorizedException;
import de.thb.webbaki.service.MasterScenarioService;
import de.thb.webbaki.service.questionnaire.QuestionnaireService;
import de.thb.webbaki.service.ScenarioService;
import de.thb.webbaki.service.UserService;
import de.thb.webbaki.service.helper.Counter;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class ThreatMatrixController {

    private final QuestionnaireService questionnaireService;
    private final MasterScenarioService masterScenarioService;
    private final ScenarioService scenarioService;
    private final UserService userService;



    @GetMapping("/threatmatrix")
    public String showQuestionnaireForm(Model model, Authentication authentication) {

        return "redirect:/threatmatrix/open/" + questionnaireService.getNewestQuestionnaireByUserId(userService.getUserByUsername(authentication.getName()).getId()).getId();
    }
    @PostMapping("/threatmatrix")
    public String submitQuestionnaire(@ModelAttribute("threatmatrix") @Valid ThreatMatrixFormModel questionnaireFormModel,
                                      Authentication authentication) {
        questionnaireService.saveQuestionnaireFromThreatMatrixFormModel(questionnaireFormModel, userService.getUserByUsername(authentication.getName()));
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
    public String showThreatMatrixByID(@PathVariable("questID") long questID, Model model, Authentication authentication) throws NotAuthorizedException{
        if(questionnaireService.existsQuestionnaireByIdAndUserId(questID,userService.getUserByUsername(authentication.getName()).getId() )){
            final var masterScenarioList = masterScenarioService.getAllMasterScenarios();
            model.addAttribute("masterScenarioList",masterScenarioList);

            Questionnaire quest = questionnaireService.getQuestionnaire(questID);

            if(quest.getUserScenarios().size() != scenarioService.getNumberOfScenarios()){
                questionnaireService.addMissingUserScenario(quest);
            }

            ThreatMatrixFormModel threatMatrixFormModel = new ThreatMatrixFormModel(quest);
            model.addAttribute("threatmatrix", threatMatrixFormModel);
            model.addAttribute("value", new Counter());

            model.addAttribute("counter", new Counter());

            return "threatmatrix/create_threatmatrix";
        }else{
            throw new NotAuthorizedException("This user could not access this questionnaire.");
        }
    }


    @Transactional
    @GetMapping(path = "/threatmatrix/chronic/{questID}")
    public String deleteQuestionnaireByID(@PathVariable("questID") long questID){
        questionnaireService.deleteQuestionnaireById(questID);//TODO change del

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