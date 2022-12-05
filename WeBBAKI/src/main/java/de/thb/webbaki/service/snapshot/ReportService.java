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
import de.thb.webbaki.service.helper.MappingReport;
import de.thb.webbaki.service.questionnaire.QuestionnaireService;
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

    Report getCompanyReport(Snapshot snapshot, String username){return reportRepository.findBySnapshotAndUser_Username(snapshot, username);}
    Report getBranchReport(Snapshot snapshot, Branch branch){return reportRepository.findBySnapshotAndBranch(snapshot, branch);}
    Report getSectorReport(Snapshot snapshot, Sector sector){return reportRepository.findBySnapshotAndSector(snapshot, sector);}
    Report getNationalReport(Snapshot snapshot){return reportRepository.findBySnapshotAndUserIsNullAndBranchIsNullAndSectorIsNull(snapshot);}
    /**
     * Creates all Report types (company, branch, sector and national)
     * @param snapshot
     */
    public void createReports(Snapshot snapshot){
        //A map mapped by the Branch. The value is another map with the Scenario as Key. The value is a List of ReportScenarios (important for calculating the average)
        Map<Branch, Map<Scenario, List<ReportScenario>>> branchMapOftReportScenarioListMaps = new HashMap<>();
        //A map mapped by the sector. The value is another map with the Scenario as Key. The value is a List of ReportScenarios (important for calculating the average)
        Map<Sector, Map<Scenario, List<ReportScenario>>> sectorMapOftReportScenarioListMaps = new HashMap<>();

        //List for the branch Average ReportScenarios
        List<ReportScenario> branchAverageReportScenarios = new ArrayList<>();
        //List for the sector Average ReportScenarios
        List<ReportScenario> sectorAverageReportScenarios = new ArrayList<>();
        //List for the national Average ReportScenarios
        List<ReportScenario> nationalAverageReportScenarios = new ArrayList<>();
        //A map mapped by the Scenario. The value is a List of ReportScenarios (important for calculating the average for the national Report)
        Map<Scenario, List<ReportScenario>> scenarioMapOfBranchAverageReportScenarios = new HashMap<>();

        for (User user : userService.getAllUsers()){
            //only use quest of a user if this user is a KRITIS_BETREIBER and the user is enabled
            if(userService.existsUserByIdAndRoleName(user.getId(), "ROLE_KRITIS_BETREIBER") && user.isEnabled()) {
                //get last questionnaire (automatically the last one in the lis)
                Questionnaire questionnaire = user.getQuestionnaires().get(user.getQuestionnaires().size() - 1);
                if (questionnaire != null) {
                    //Company part
                    Report companyReport = Report.builder().snapshot(snapshot).user(questionnaire.getUser()).numberOfQuestionnaires(1).comment(questionnaire.getComment()).build();
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

        //branchReport part
        //go through all branches with their ReportScenarios, calculate the average for every Scenario, and save it for a Report
        branchMapOftReportScenarioListMaps.forEach((branch, mapOfReportScenarioLists) -> {
            //get the number of questionnaires by taking one list of ReportScenarios and his size
            //TODO number of questionnaire could be false if not every UserScenario is there for every Scenario
            int numberOfQuestionnaires = mapOfReportScenarioLists.values().iterator().next().size();
            Report branchReport = Report.builder().snapshot(snapshot).branch(branch).numberOfQuestionnaires(numberOfQuestionnaires).build();
            reportRepository.save(branchReport);

            mapOfReportScenarioLists.forEach((scenario, branchReportScenarios) -> {
                ReportScenario branchReportScenario = reportScenarioService.calculateReportScenarioAverage(branchReportScenarios);
                branchReportScenario.setReport(branchReport);
                branchReportScenario.setScenario(scenario);

                //create the list if not exists by key
                if(!scenarioMapOfBranchAverageReportScenarios.containsKey(scenario)){
                    scenarioMapOfBranchAverageReportScenarios.put(scenario, new ArrayList<>());
                }
                //add the average ReportScenario of this Branch to the List mapped by the current Scenario
                scenarioMapOfBranchAverageReportScenarios.get(scenario).add(branchReportScenario);

                branchAverageReportScenarios.add(branchReportScenario);
            });
        });
        //save all BranchReportScenarios
        reportScenarioService.saveAllReportScenarios(branchAverageReportScenarios);

        //sectorReport part
        //go through all sectors with their ReportScenarios, calculate the average for every Scenario, and save it for a Report
        sectorMapOftReportScenarioListMaps.forEach((sector, mapOfReportScenarioLists) -> {
            //get the number of questionnaires by taking one list of ReportScenarios and his size
            //TODO number of questionnaire could be false if not every UserScenario is there for every Scenario
            int numberOfQuestionnaires = mapOfReportScenarioLists.values().iterator().next().size();
            Report branchReport = Report.builder().snapshot(snapshot).sector(sector).numberOfQuestionnaires(numberOfQuestionnaires).build();
            reportRepository.save(branchReport);

            mapOfReportScenarioLists.forEach((scenario, branchReportScenarios) -> {
                ReportScenario branchReportScenario = reportScenarioService.calculateReportScenarioAverage(branchReportScenarios);
                branchReportScenario.setReport(branchReport);
                branchReportScenario.setScenario(scenario);
                sectorAverageReportScenarios.add(branchReportScenario);
            });
        });
        //save all SectorReportScenarios
        reportScenarioService.saveAllReportScenarios(sectorAverageReportScenarios);

        //nationalReport part
        //get the number by the size of the map (= number of branches with an average)
        int numberOfQuestionnaires = branchMapOftReportScenarioListMaps.size();
        Report nationalReport = Report.builder().snapshot(snapshot).numberOfQuestionnaires(numberOfQuestionnaires).build();
        reportRepository.save(nationalReport);
        scenarioMapOfBranchAverageReportScenarios.forEach((scenario, branchAverageReportScenariosForScenario) -> {
            ReportScenario nationalReportScenario = reportScenarioService.calculateReportScenarioAverage(branchAverageReportScenariosForScenario);
            nationalReportScenario.setScenario(scenario);
            nationalReportScenario.setReport(nationalReport);
            nationalAverageReportScenarios.add(nationalReportScenario);
        });

        //save all nationalReportScenarios
        reportScenarioService.saveAllReportScenarios(nationalAverageReportScenarios);

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

    public MappingReport getMappingReportByReportFocus(ReportFocus reportFocus, String username, Snapshot snapshot) throws UnknownReportFocusException {
        User user = userService.getUserByUsername(username);
        Report report;
        switch (reportFocus) {
            case COMPANY:
                report =  getCompanyReport(snapshot,username);
                break;
            case BRANCHE:
                report = getBranchReport(snapshot, user.getBranch());
                break;
            case SECTOR:
                report = getSectorReport(snapshot, user.getBranch().getSector());
                break;
            case NATIONAL:
                report = getNationalReport(snapshot);
                break;
            default:
                throw new UnknownReportFocusException();
        }

        if(report == null || report.getReportScenarios() == null){
            return new MappingReport(Report.builder().numberOfQuestionnaires(0).build());
        }else{
            //TODO save comment in Report
            return new MappingReport(report);
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
