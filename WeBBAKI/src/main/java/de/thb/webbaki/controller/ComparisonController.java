package de.thb.webbaki.controller;

import com.lowagie.text.DocumentException;
import de.thb.webbaki.service.ComparisonService;
import de.thb.webbaki.service.Exceptions.UnknownReportFocusException;
import de.thb.webbaki.service.MasterScenarioService;
import de.thb.webbaki.service.snapshot.ReportService;
import de.thb.webbaki.service.snapshot.SnapshotService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@AllArgsConstructor
public class ComparisonController {

    private final MasterScenarioService masterScenarioService;
    private final SnapshotService snapshotService;
    private final ReportService reportService;
    private final ComparisonService comparisonService;

    @GetMapping("/horizontal_vertical_comparison")
    public String showComparison(Model model, Authentication authentication) throws UnknownReportFocusException {

        final var masterScenarioList = masterScenarioService.getAllByActiveTrueOrderByPositionInRow();
        model.addAttribute("masterScenarioList",masterScenarioList);

        model.addAttribute("comparison", comparisonService.getReportComparisonFromUsername(authentication.getName()));


        return "comparison/comparison_container";
    }

    @GetMapping("/horizontal_vertical_comparison/download")
    public void downloadReportPdf(HttpServletResponse response, Authentication authentication, HttpServletRequest request) throws UnknownReportFocusException, IOException, DocumentException {

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=horizontaler-vertikaler-vergleich.pdf";
        response.setHeader(headerKey, headerValue);
        Context context = new Context();

        final var masterScenarioList = masterScenarioService.getAllByActiveTrueOrderByPositionInRow();

        context.setVariable("masterScenarioList",masterScenarioList);
        context.setVariable("comparison", comparisonService.getReportComparisonFromUsername(authentication.getName()));

        reportService.generatePdfFromHtml(reportService.parseThymeleafTemplateToHtml("comparison/comparison", context),
                request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort(), response.getOutputStream());
    }


}
