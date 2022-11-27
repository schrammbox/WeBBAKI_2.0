package de.thb.webbaki.service;

import com.lowagie.text.DocumentException;
import de.thb.webbaki.entity.*;
import de.thb.webbaki.enums.ReportFocus;
import de.thb.webbaki.service.Exceptions.UnknownReportFocusException;
import de.thb.webbaki.service.helper.ThreatSituation;
import de.thb.webbaki.service.helper.ThreatSituationLinkedList;
import de.thb.webbaki.service.helper.UserScenarioLinkedList;
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
    private SnapshotService snapshotService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private UserScenarioService userScenarioService;

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

    public UserScenarioLinkedList getUserScenarioLinkedListByReportFocus(ReportFocus reportFocus, String username, Snapshot snapshot) throws UnknownReportFocusException{
        //TODO add this!!! now with UserScenario like the method on the bottom
        List<Map<Long, UserScenario>> listOfUserScenarioMaps = new LinkedList<>();
        List<Questionnaire> snapshotQuestionnaireList = snapshotService.getAllQuestionnaires(snapshot.getId());
        String comment = null;

        //should get the listOfUserScenarioMaps in another way with reportFocus national
        //Average over all branche-averages
        if(reportFocus == ReportFocus.NATIONAL){
            for(Branch branch : branchService.getAllBranches()){
                List<Map<Long, UserScenario>> branchListOfUserScenarioMaps = new LinkedList<>();
                //remove all unimportant questionnaires
                List<Questionnaire> questionnaireList = questionnaireService.getQuestionnairesWithUsersInside(snapshotQuestionnaireList, userService.getUsersByBranch(branch.getName()));

                //add all UserScenarioLists of the Questionnaires to the List
                for(Questionnaire questionnaire : questionnaireList){
                    branchListOfUserScenarioMaps.add(userScenarioService.getUserScenarioMapFromList(questionnaire.getUserScenarios()));
                }

                if(branchListOfUserScenarioMaps.size() != 0) {
                    listOfUserScenarioMaps.add(userScenarioService.getUserScenarioMapFromList(userScenarioService.calculateUserScenarioAverageList(branchListOfUserScenarioMaps)));
                }
            }
        }else {
            List<User> userList;
            switch (reportFocus) {
                case COMPANY:
                    userList = Collections.singletonList(userService.getUserByUsername(username));
                    break;
                case BRANCHE:
                    userList = userService.getUsersByBranch(userService.getUserByUsername(username).getBranch().getName());
                    break;
                case SECTOR:
                    userList = userService.getUsersBySectorName(userService.getUserByUsername(username).getBranch().getSector().getName());
                    break;
                default:
                    throw new UnknownReportFocusException();
            }

            //remove all unimportant questionnaires
            List<Questionnaire> questionnaireList = questionnaireService.getQuestionnairesWithUsersInside(snapshotQuestionnaireList, userList);

            if(reportFocus == ReportFocus.COMPANY && questionnaireList.size() == 1){
                comment = questionnaireList.get(0).getComment();
            }

            //add all UserScenarioLists of the Questionnaires to the List
            for(Questionnaire questionnaire : questionnaireList){
                listOfUserScenarioMaps.add(userScenarioService.getUserScenarioMapFromList(questionnaire.getUserScenarios()));
            }
        }

        if(listOfUserScenarioMaps.size() == 0){
            //return null and handle it later in the template
            return null;
        }else{
            return new UserScenarioLinkedList(userScenarioService.calculateUserScenarioAverageList(listOfUserScenarioMaps), comment, listOfUserScenarioMaps.size());
        }
    }
}
