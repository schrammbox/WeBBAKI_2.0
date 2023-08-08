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
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;

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

        final var snapshotList = snapshotService.getAllSnapshots();

        LocalDate myLocal = LocalDate.now();
        int quarter = myLocal.get(IsoFields.QUARTER_OF_YEAR);
        final int year = myLocal.getYear();
        String tempQuarter = "";

        snapshotList.removeIf(s -> !s.getName().contains(String.valueOf(year) + " Quartal ") && !s.getName().contains(String.valueOf(year - 1) + " Quartal "));

        List<Snapshot> relevantSnapshots = new ArrayList<>();

        for(Snapshot snap : snapshotList){
            if(snap.getDate().getYear() != year) {
                if (snap.getDate().get(IsoFields.QUARTER_OF_YEAR) < quarter) snapshotList.remove(snap);
            }
        }

        model.addAttribute("snaps", snapshotList);

        model.addAttribute("quarter", quarter);
        model.addAttribute("year", year);


        quarter = quarter - 1 == 0 ? 4 : --quarter;
        model.addAttribute("q1", quarter);

        for(Snapshot snap : snapshotList){
            if(snap.getDate().get(IsoFields.QUARTER_OF_YEAR) == quarter){
                MappingReport companyReportQ1 = reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, authentication.getName(), snap);
                model.addAttribute("companyReportQ1", companyReportQ1);

                MappingReport branchReportQ1 = reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, authentication.getName(), snap);
                model.addAttribute("branchReportQ1", branchReportQ1);

                MappingReport sectorReportQ1 = reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, authentication.getName(), snap);
                model.addAttribute("sectorReportQ1", sectorReportQ1);

                MappingReport nationalReportQ1 = reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, authentication.getName(), snap);
                model.addAttribute("nationalReportQ1", nationalReportQ1);
                break;
            }
        }

        quarter = quarter - 1 == 0 ? 4 : --quarter;
        model.addAttribute("q2", quarter);

        for(Snapshot snap : snapshotList){
            if(snap.getDate().get(IsoFields.QUARTER_OF_YEAR) == quarter){
                MappingReport companyReportQ2 = reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, authentication.getName(), snap);
                model.addAttribute("companyReportQ2", companyReportQ2);

                MappingReport branchReportQ2 = reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, authentication.getName(), snap);
                model.addAttribute("branchReportQ2", branchReportQ2);

                MappingReport sectorReportQ2 = reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, authentication.getName(), snap);
                model.addAttribute("sectorReportQ2", sectorReportQ2);

                MappingReport nationalReportQ2 = reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, authentication.getName(), snap);
                model.addAttribute("nationalReportQ2", nationalReportQ2);
                break;
            }
        }

        quarter = quarter - 1 == 0 ? 4 : --quarter;
        model.addAttribute("q3", quarter);

        for(Snapshot snap : snapshotList){
            if(snap.getDate().get(IsoFields.QUARTER_OF_YEAR) == quarter){
                MappingReport companyReportQ3 = reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, authentication.getName(), snap);
                model.addAttribute("companyReportQ3", companyReportQ3);

                MappingReport branchReportQ3 = reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, authentication.getName(), snap);
                model.addAttribute("branchReportQ3", branchReportQ3);

                MappingReport sectorReportQ3 = reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, authentication.getName(), snap);
                model.addAttribute("sectorReportQ3", sectorReportQ3);

                MappingReport nationalReportQ3 = reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, authentication.getName(), snap);
                model.addAttribute("nationalReportQ3", nationalReportQ3);
                break;
            }
        }

        quarter = quarter - 1 == 0 ? 4 : --quarter;
        model.addAttribute("q4", quarter);

        for(Snapshot snap : snapshotList){
            if(snap.getDate().get(IsoFields.QUARTER_OF_YEAR) == quarter){
                MappingReport companyReportQ4 = reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, authentication.getName(), snap);
                model.addAttribute("companyReportQ4", companyReportQ4);

                MappingReport branchReportQ4 = reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, authentication.getName(), snap);
                model.addAttribute("branchReportQ4", branchReportQ4);

                MappingReport sectorReportQ4 = reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, authentication.getName(), snap);
                model.addAttribute("sectorReportQ4", sectorReportQ4);

                MappingReport nationalReportQ4 = reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, authentication.getName(), snap);
                model.addAttribute("nationalReportQ4", nationalReportQ4);
                break;
            }
        }




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

        final var snapshotList = snapshotService.getAllSnapshots();

        LocalDate myLocal = LocalDate.now();
        int quarter = myLocal.get(IsoFields.QUARTER_OF_YEAR);
        final int year = myLocal.getYear();
        String tempQuarter = "";

        snapshotList.removeIf(s -> !s.getName().contains(String.valueOf(year) + " Quartal ") && !s.getName().contains(String.valueOf(year - 1) + " Quartal "));

        List<Snapshot> relevantSnapshots = new ArrayList<>();

        for(Snapshot snap : snapshotList){
            if(snap.getDate().getYear() != year) {
                if (snap.getDate().get(IsoFields.QUARTER_OF_YEAR) < quarter) snapshotList.remove(snap);
            }
        }

        context.setVariable("snaps", snapshotList);

        context.setVariable("quarter", quarter);
        context.setVariable("year", year);


        quarter = quarter - 1 == 0 ? 4 : --quarter;
        context.setVariable("q1", quarter);

        for(Snapshot snap : snapshotList){
            if(snap.getDate().get(IsoFields.QUARTER_OF_YEAR) == quarter){
                MappingReport companyReportQ1 = reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, authentication.getName(), snap);
                context.setVariable("companyReportQ1", companyReportQ1);

                MappingReport branchReportQ1 = reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, authentication.getName(), snap);
                context.setVariable("branchReportQ1", branchReportQ1);

                MappingReport sectorReportQ1 = reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, authentication.getName(), snap);
                context.setVariable("sectorReportQ1", sectorReportQ1);

                MappingReport nationalReportQ1 = reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, authentication.getName(), snap);
                context.setVariable("nationalReportQ1", nationalReportQ1);
                break;
            }
        }

        quarter = quarter - 1 == 0 ? 4 : --quarter;
        context.setVariable("q2", quarter);

        for(Snapshot snap : snapshotList){
            if(snap.getDate().get(IsoFields.QUARTER_OF_YEAR) == quarter){
                MappingReport companyReportQ2 = reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, authentication.getName(), snap);
                context.setVariable("companyReportQ2", companyReportQ2);

                MappingReport branchReportQ2 = reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, authentication.getName(), snap);
                context.setVariable("branchReportQ2", branchReportQ2);

                MappingReport sectorReportQ2 = reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, authentication.getName(), snap);
                context.setVariable("sectorReportQ2", sectorReportQ2);

                MappingReport nationalReportQ2 = reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, authentication.getName(), snap);
                context.setVariable("nationalReportQ2", nationalReportQ2);
                break;
            }
        }

        quarter = quarter - 1 == 0 ? 4 : --quarter;
        context.setVariable("q3", quarter);

        for(Snapshot snap : snapshotList){
            if(snap.getDate().get(IsoFields.QUARTER_OF_YEAR) == quarter){
                MappingReport companyReportQ3 = reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, authentication.getName(), snap);
                context.setVariable("companyReportQ3", companyReportQ3);

                MappingReport branchReportQ3 = reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, authentication.getName(), snap);
                context.setVariable("branchReportQ3", branchReportQ3);

                MappingReport sectorReportQ3 = reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, authentication.getName(), snap);
                context.setVariable("sectorReportQ3", sectorReportQ3);

                MappingReport nationalReportQ3 = reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, authentication.getName(), snap);
                context.setVariable("nationalReportQ3", nationalReportQ3);
                break;
            }
        }

        quarter = quarter - 1 == 0 ? 4 : --quarter;
        context.setVariable("q4", quarter);

        for(Snapshot snap : snapshotList){
            if(snap.getDate().get(IsoFields.QUARTER_OF_YEAR) == quarter){
                MappingReport companyReportQ4 = reportService.getMappingReportByReportFocus(ReportFocus.COMPANY, authentication.getName(), snap);
                context.setVariable("companyReportQ4", companyReportQ4);

                MappingReport branchReportQ4 = reportService.getMappingReportByReportFocus(ReportFocus.BRANCHE, authentication.getName(), snap);
                context.setVariable("branchReportQ4", branchReportQ4);

                MappingReport sectorReportQ4 = reportService.getMappingReportByReportFocus(ReportFocus.SECTOR, authentication.getName(), snap);
                context.setVariable("sectorReportQ4", sectorReportQ4);

                MappingReport nationalReportQ4 = reportService.getMappingReportByReportFocus(ReportFocus.NATIONAL, authentication.getName(), snap);
                context.setVariable("nationalReportQ4", nationalReportQ4);
                break;
            }
        }


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