package de.thb.webbaki.controller;

import com.lowagie.text.DocumentException;
import de.thb.webbaki.controller.form.ThreatMatrixFormModel;
import de.thb.webbaki.entity.questionnaire.Questionnaire;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.entity.snapshot.Snapshot;
import de.thb.webbaki.enums.ReportFocus;
import de.thb.webbaki.exception.SnapshotNotFoundException;
import de.thb.webbaki.repository.snapshot.SnapshotRepository;
import de.thb.webbaki.service.Exceptions.NotAuthorizedException;
import de.thb.webbaki.service.Exceptions.UnknownReportFocusException;
import de.thb.webbaki.service.MasterScenarioService;
import de.thb.webbaki.service.helper.MappingReport;
import de.thb.webbaki.service.questionnaire.QuestionnaireService;
import de.thb.webbaki.service.ScenarioService;
import de.thb.webbaki.service.UserService;
import de.thb.webbaki.service.helper.Counter;
import de.thb.webbaki.service.snapshot.ReportService;
import de.thb.webbaki.service.snapshot.SnapshotService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@AllArgsConstructor
public class ThreatMatrixController {

    private final QuestionnaireService questionnaireService;
    private final MasterScenarioService masterScenarioService;
    private final ScenarioService scenarioService;
    private final UserService userService;
    private final SnapshotService snapshotService;
    private final ReportService reportService;



    @GetMapping("/threatmatrix")
    public String showQuestionnaireForm(Authentication authentication) {
        Long newestQuestionnaireId = questionnaireService.getNewestQuestionnaireByUserId(userService.getUserByUsername(authentication.getName()).getId()).getId();
        return "redirect:/threatmatrix/open/" + newestQuestionnaireId;
    }

    @PostMapping("/threatmatrix")
    public String submitQuestionnaire(@ModelAttribute("threatmatrix") @Valid ThreatMatrixFormModel questionnaireFormModel,
                                      Authentication authentication) {
        questionnaireService.saveQuestionnaireFromThreatMatrixFormModel(questionnaireFormModel, userService.getUserByUsername(authentication.getName()));
        return "redirect:/threatmatrix/chronic";
    }

    @GetMapping("/threatmatrix/open/{questID}")
    public String showThreatMatrixByID(@PathVariable("questID") long questID, Model model, Authentication authentication) throws NotAuthorizedException{
        if(questionnaireService.existsByIdAndUserId(questID,userService.getUserByUsername(authentication.getName()).getId() )){
            final var masterScenarioList = masterScenarioService.getAllByActiveTrueOrderByPositionInRow();
            model.addAttribute("masterScenarioList",masterScenarioList);

            Questionnaire quest = questionnaireService.getQuestionnaire(questID);

            questionnaireService.checkIfMatchingWithActiveScenariosFromDB(quest);

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
        questionnaireService.deleteQuestionnaireById(questID);

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


    @GetMapping("/threatmatrix/horizontal_vertical_comparison")
    public String showComparison(Model model, Authentication authentication) throws UnknownReportFocusException {

        final var masterScenarioList = masterScenarioService.getAllByActiveTrueOrderByPositionInRow();
        model.addAttribute("masterScenarioList",masterScenarioList);

        Snapshot newestSnapshot = snapshotService.getNewestSnapshot()
                .orElseThrow(() -> new SnapshotNotFoundException("snapshot was not found with the given id"));

        //final var reportList = reportService.getReportBySnapshotId(newestSnapshot.getId());

        //model.addAttribute("reportList", reportList);

        MappingReport companyReport = reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, authentication.getName(), newestSnapshot);
        model.addAttribute("companyReport", companyReport);

        MappingReport branchReport = reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, authentication.getName(), newestSnapshot);
        model.addAttribute("branchReport", branchReport);

        MappingReport sectorReport = reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, authentication.getName(), newestSnapshot);
        model.addAttribute("sectorReport", sectorReport);

        MappingReport nationalReport = reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, authentication.getName(), newestSnapshot);
        model.addAttribute("nationalReport", nationalReport);

        return "threatmatrix/horizontal_vertical_comparison";
    }

    @GetMapping("threatmatrix/horizontal_vertical_comparison/download")
    public void downloadReportPdf(HttpServletResponse response, Authentication authentication, HttpServletRequest request) throws UnknownReportFocusException, IOException, DocumentException {

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=horizontaler-vertikaler-vergleich.pdf";
        response.setHeader(headerKey, headerValue);
        Context context = new Context();
        final var masterScenarioList = masterScenarioService.getAllByActiveTrueOrderByPositionInRow();
        context.setVariable("masterScenarioList",masterScenarioList);

        Snapshot newestSnapshot = snapshotService.getNewestSnapshot()
                .orElseThrow(() -> new SnapshotNotFoundException("snapshot was not found with the given id"));

        //final var reportList = reportService.getReportBySnapshotId(newestSnapshot.getId());

        //model.addAttribute("reportList", reportList);

        MappingReport companyReport = reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, authentication.getName(), newestSnapshot);
        context.setVariable("companyReport", companyReport);

        MappingReport branchReport = reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, authentication.getName(), newestSnapshot);
        context.setVariable("branchReport", branchReport);

        MappingReport sectorReport = reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, authentication.getName(), newestSnapshot);
        context.setVariable("sectorReport", sectorReport);

        MappingReport nationalReport = reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, authentication.getName(), newestSnapshot);
        context.setVariable("nationalReport", nationalReport);


        /*
        ReportFocus reportFocus = ReportFocus.getReportFocusByEnglishRepresentation(reportFocusString);
        context.setVariable("reportFocus", reportFocus);
        */

        /*
        Snapshot currentSnapshot = snapshotService.getSnapshotByID(snapId).get();
        context.setVariable("currentSnapshot", currentSnapshot);
         */

        /*
        MappingReport report = reportService.getMappingReportByReportFocus(reportFocus, authentication.getName(), currentSnapshot);
        context.setVariable("report", report);
         */

        context.setVariable("counter", new Counter());

        // add a random csrf object for the "reportService.parseThymeleafTemplateToHtml" to not crash
        CsrfToken csrfToken = new CsrfToken() {
            @Override
            public String getHeaderName() {
                return "csrfHeader";
            }

            @Override
            public String getParameterName() {
                return "csrfParameter";
            }

            @Override
            public String getToken() {
                return "csrfToken";
            }
        };
        context.setVariable("_csrf", csrfToken);

        reportService.generatePdfFromHtml(reportService.parseThymeleafTemplateToHtml("threatmatrix/horizontal_vertical_comparison", context),
                request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort(), response.getOutputStream());
    }

}