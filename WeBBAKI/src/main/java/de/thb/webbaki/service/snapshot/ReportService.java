package de.thb.webbaki.service.snapshot;

import com.lowagie.text.DocumentException;
import de.thb.webbaki.entity.*;
import de.thb.webbaki.entity.questionnaire.Questionnaire;
import de.thb.webbaki.entity.snapshot.Report;
import de.thb.webbaki.entity.snapshot.ReportScenario;
import de.thb.webbaki.entity.snapshot.Snapshot;
import de.thb.webbaki.enums.ReportFocus;
import de.thb.webbaki.repository.snapshot.ReportRepository;
import de.thb.webbaki.service.BranchService;
import de.thb.webbaki.service.Exceptions.UnknownReportFocusException;
import de.thb.webbaki.service.UserService;
import de.thb.webbaki.service.helper.ReportScenarioHashMap;
import de.thb.webbaki.service.questionnaire.QuestionnaireService;
import de.thb.webbaki.service.questionnaire.UserScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;


@Service
public class  ReportService {

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    ReportScenarioService reportScenarioService;
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    BranchService branchService;

    Report getReportBySnapshotIdAndUsername(long id, String username){return reportRepository.findBySnapshot_IdAndUser_Username(id, username);}
    Report getReportBySnapshotIdAndBranchName(long id, String branchName){return reportRepository.findBySnapshot_IdAndBranch_Name(id,branchName);}
    /**
     * Creates all Report types (company, branch, sector and national)
     * @param snapshot
     */
    public void createReports(Snapshot snapshot){
        //A map mapped by the Branch. The value is another map with the Scenario as Key. The value is a List of ReportScenarios (important for calculating the average)
        Map<Branch, Map<Scenario, List<ReportScenario>>> branchMapOftReportScenarioListMaps = new HashMap<>();
        //A map mapped by the sector. The value is another map with the Scenario as Key. The value is a List of ReportScenarios (important for calculating the average)
        Map<Sector, Map<Scenario, List<ReportScenario>>> sectorMapOftReportScenarioListMaps = new HashMap<>();
        for (User user : userService.getAllUsers()){
            //only use quest of a user if this user is a KRITIS_BETREIBER and the user is enabled
            if(userService.existsUserByIdAndRoleName(user.getId(), "ROLE_KRITIS_BETREIBER") && user.isEnabled()) {
                //get last questionnaire (automatically the last one in the lis)
                Questionnaire questionnaire = user.getQuestionnaires().get(user.getQuestionnaires().size() - 1);
                if (questionnaire != null) {
                    //Company part
                    Report companyReport = Report.builder().snapshot(snapshot).user(questionnaire.getUser()).numberOfQuestionnaires(1).build();
                    reportRepository.save(companyReport);

                    //calculate all the ReportScenarios from the questionnaires UserScenarios and add later branch, sector and national reports
                    List<ReportScenario> companyReportScenarios = reportScenarioService.calculateReportScenariosFromUserScenarios(questionnaire.getUserScenarios());
                    for(ReportScenario reportScenario : companyReportScenarios){
                        reportScenario.setReport(companyReport);

                        //Branch part
                        //create map inside the map if not exists
                        if(!branchMapOftReportScenarioListMaps.containsKey(user.getBranch())){
                            branchMapOftReportScenarioListMaps.put(user.getBranch(), new HashMap<Scenario, List<ReportScenario>>());
                        }
                        //create list inside the map inside the map if not exists
                        if(!branchMapOftReportScenarioListMaps.get(user.getBranch()).containsKey(reportScenario.getScenario())){
                            branchMapOftReportScenarioListMaps.get(user.getBranch()).put(reportScenario.getScenario(), new ArrayList<>());
                        }
                        //now add the reportScenario to the right list
                        branchMapOftReportScenarioListMaps.get(user.getBranch()).get(reportScenario.getScenario()).add(reportScenario);

                        //Sector part
                        //create map inside the map if not exists
                        if(!sectorMapOftReportScenarioListMaps.containsKey(user.getBranch().getSector())){
                            sectorMapOftReportScenarioListMaps.put(user.getBranch().getSector(), new HashMap<Scenario, List<ReportScenario>>());
                        }
                        //create list inside the map inside the map if not exists
                        if(!sectorMapOftReportScenarioListMaps.get(user.getBranch().getSector()).containsKey(reportScenario.getScenario())){
                            sectorMapOftReportScenarioListMaps.get(user.getBranch().getSector()).put(reportScenario.getScenario(), new ArrayList<>());
                        }
                        //now add the reportScenario to the right list
                        sectorMapOftReportScenarioListMaps.get(user.getBranch().getSector()).get(reportScenario.getScenario()).add(reportScenario);
                    }
                    //save all CompanyReportScenarios
                    reportScenarioService.saveAllReportScenarios(companyReportScenarios);

                }
            }
        }

        List<ReportScenario> branchAverageReportScenarios = new ArrayList<>();
        //go through all branches with their ReportScenarios, calculate the average for every Scenario, and save it for a Report
        branchMapOftReportScenarioListMaps.forEach((branch, mapOfReportScenarioLists) -> {
            //get of questionnaires by taking one list of ReportScenarios and his size
            //TODO number of questionnaire could be false if not every UserScenario is there for every Scenario
            int numberOfQuestionnaires = mapOfReportScenarioLists.values().iterator().next().size();
            Report branchReport = Report.builder().snapshot(snapshot).branch(branch).numberOfQuestionnaires(numberOfQuestionnaires).build();
            reportRepository.save(branchReport);

            mapOfReportScenarioLists.forEach((scenario, branchReportScenarios) -> {
                ReportScenario branchReportScenario = reportScenarioService.calculateReportScenarioAverage(branchReportScenarios);
                branchReportScenario.setReport(branchReport);
                branchReportScenario.setScenario(scenario);
                branchAverageReportScenarios.add(branchReportScenario);
            });
        });
        //save all BranchReportScenarios
        reportScenarioService.saveAllReportScenarios(branchAverageReportScenarios);



    }

    /**
     * Creates all branch-reports
     * @param snapshot
     */
    private void createBranchReports(Snapshot snapshot){
        List<Branch> branches = branchService.getAllBranches();
        for(Branch branch : branches){
            List<User> usersFromBranch = userService.getUsersByBranch(branch.getName());
            //questionnaireService.getQuestionnairesWithUsersInside(usersFromBranch );
            //TODO dooooo
        }
    }

    public ReportScenarioHashMap getReportScenarioLinkedListByReportFocus(ReportFocus reportFocus, String username, Snapshot snapshot) throws UnknownReportFocusException {
        Report report;
        switch (reportFocus) {
            case COMPANY:
                report =  getReportBySnapshotIdAndUsername(snapshot.getId(),username);
                break;
            case BRANCHE:
                report = getReportBySnapshotIdAndBranchName(snapshot.getId(), userService.getUserByUsername(username).getBranch().getName());
                break;
            case SECTOR:
                report = new Report();
                break;
            case NATIONAL:
                report = new Report();
                break;
            default:
                throw new UnknownReportFocusException();
        }

        if(report == null){
            return null;//TODO questionnaireamount = 0?
        }else{
            //TODO save comment in Report
            return new ReportScenarioHashMap(reportScenarioService.getReportScenarioMapFromList(report.getReportScenarios()), "test", report.getNumberOfQuestionnaires());
        }
    }

    /**
     *
     * @param template Does not need the .html.
     * @param context Contains the variables that we want to be passed to Thymeleaf.
     *                Addable with context.setVariable(key, value);
     * @return the html code as a String
     */
    public String parseThymeleafTemplateToHtml(String template, Context context){
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine.process("templates/" + template, context);
    }

    /**
     * All styles in the html have to be inline.
     * Not closed elements like <br> and <link> have to be closed like this
     * <br></br> and <link></link>
     * @param html
     * @param outputStream
     * @throws IOException
     */
    public void generatePdfFromHtml(String html, String baseUrl, OutputStream outputStream) throws IOException, DocumentException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html,baseUrl);
        renderer.layout();
        renderer.createPDF(bufferedOutputStream);

        bufferedOutputStream.close();
    }
}
