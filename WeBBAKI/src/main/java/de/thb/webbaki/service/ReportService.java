package de.thb.webbaki.service;

import com.lowagie.text.DocumentException;
import de.thb.webbaki.entity.Branche;
import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.Snapshot;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.enums.ReportFocus;
import de.thb.webbaki.service.Exceptions.UnknownReportFocusException;
import de.thb.webbaki.service.helper.ThreatSituation;
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
    private BrancheService brancheService;

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
    public void generatePdfFromHtml(String html, OutputStream outputStream) throws IOException, DocumentException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(bufferedOutputStream);

        bufferedOutputStream.close();
    }

    /**
     *
     * @param reportFocus
     * @param username
     * @return the right Queue with ThreatSituations
     * @throws UnknownReportFocusException
     */
    public Queue<ThreatSituation> getThreatSituationQueueByReportFocus(ReportFocus reportFocus, String username, Snapshot snapshot) throws UnknownReportFocusException {
        List<Queue<ThreatSituation>> queueList = new LinkedList<Queue<ThreatSituation>>();
        List<Questionnaire> snapshotQuestionnaireList = snapshotService.getAllQuestionnaires(snapshot.getId());
        //should get the queueList in another way with reportFocus national
        //Average over all branche-averages
        if(reportFocus == ReportFocus.NATIONAL){
            for(Branche branche : brancheService.getAllBranches()){
                List<Queue<ThreatSituation>> brancheQueueList = new LinkedList<Queue<ThreatSituation>>();
                //remove all unimportant questionnaires
                List<Questionnaire> questionnaireList = questionnaireService.getQuestionnairesWithUsersInside(snapshotQuestionnaireList, userService.getUsersByBranche(branche.getName()));

                for (Questionnaire questionnaire : questionnaireList) {
                    final Map<Long, String[]> questMap = questionnaireService.getMapping(questionnaire);
                    brancheQueueList.add(questionnaireService.getThreatSituationQueueFromMapping(questMap));
                }
                if(brancheQueueList.size() != 0) {
                    queueList.add(questionnaireService.getThreatSituationAverageQueueFromQueues(brancheQueueList));
                }
            }
        }else {
            List<User> userList;
            switch (reportFocus) {
                case COMPANY:
                    userList = Collections.singletonList(userService.getUserByUsername(username));
                    break;
                case BRANCHE:
                    userList = userService.getUsersByBranche(userService.getUserByUsername(username).getBranche());
                    break;
                case SECTOR:
                    userList = userService.getUsersBySector(userService.getUserByUsername(username).getSector());
                    break;
                default:
                    throw new UnknownReportFocusException();
            }

            //remove all unimportant questionnaires
            List<Questionnaire> questionnaireList = questionnaireService.getQuestionnairesWithUsersInside(snapshotQuestionnaireList, userList);

            for (Questionnaire questionnaire : questionnaireList) {
                final Map<Long, String[]> questMap = questionnaireService.getMapping(questionnaire);
                queueList.add(questionnaireService.getThreatSituationQueueFromMapping(questMap));
            }
        }

        if(queueList.size() == 0){
            //return null and handle it later in the template
            return null;
        }else{
            return questionnaireService.getThreatSituationAverageQueueFromQueues(queueList);
        }
    }
}
