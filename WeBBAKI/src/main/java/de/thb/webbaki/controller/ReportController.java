package de.thb.webbaki.controller;

import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.Snapshot;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.enums.ReportFocus;
import de.thb.webbaki.service.*;
import de.thb.webbaki.service.Exceptions.WrongPathException;
import de.thb.webbaki.service.helper.ThreatSituation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletResponse;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private MasterScenarioService masterScenarioService;
    @Autowired
    private SnapshotService snapshotService;

    /**
     * Redirect to url with newest snap-id(because of the Nav elements in the layout.html).
     * @param reportFocusString
     * @return
     */
    @GetMapping("report/{reportFocus}")
    public String showQuestionnaireForm(@PathVariable("reportFocus") String reportFocusString){
        long snapId = snapshotService.getNewestSnapshot().getId();
        return "redirect:/report/"+reportFocusString+"/"+String.valueOf(snapId);
    }
    @GetMapping("report/{reportFocus}/{snapId}")
    public String showQuestionnaireForm(@PathVariable("reportFocus") String reportFocusString, @PathVariable("snapId") long snapId,
                                        Model model, Authentication authentication) throws WrongPathException{
        final var masterScenarioList = masterScenarioService.getAllMasterScenarios();
        model.addAttribute("masterScenarioList",masterScenarioList);
        ReportFocus reportFocus = ReportFocus.getReportFocusByEnglishRepresentation(reportFocusString);
        model.addAttribute("reportFocus", reportFocus);

        Queue<ThreatSituation> threatSituationQueue = reportService.getThreatSituationQueueByReportFocus(reportFocus, authentication.getName(), snapId);

        model.addAttribute("threatSituationQueue", threatSituationQueue);

        final List<Snapshot> snapshotList = snapshotService.getAllSnapshotOrderByDESC();
        model.addAttribute("snapshotList", snapshotList);

        model.addAttribute("currentSnapId", snapId);

        return "report/report_container";
    }

    @GetMapping("report/{reportFocus}/{snapId}/download")
    public void downloadPdf(@PathVariable("reportFocus") String reportFocusString, @PathVariable("snapId") long snapId,
                            HttpServletResponse response, Authentication authentication) throws WrongPathException, IOException{

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=report.pdf";
        response.setHeader(headerKey, headerValue);

        Context context = new Context();
        final var masterScenarioList = masterScenarioService.getAllMasterScenarios();
        context.setVariable("masterScenarioList",masterScenarioList);
        ReportFocus reportFocus = ReportFocus.getReportFocusByEnglishRepresentation(reportFocusString);
        context.setVariable("reportFocus", reportFocus);

        Queue<ThreatSituation> threatSituationQueue = reportService.getThreatSituationQueueByReportFocus(reportFocus, authentication.getName(), snapId);

        context.setVariable("threatSituationQueue", threatSituationQueue);

        reportService.generatePdfFromHtml(reportService.parseThymeleafTemplateToHtml("report/report", context),
                    response.getOutputStream());

    }

}