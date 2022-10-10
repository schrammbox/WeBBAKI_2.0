package de.thb.webbaki.service;

import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.Snapshot;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.enums.ReportFocus;
import de.thb.webbaki.service.Exceptions.WrongPathException;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


@Service
public class ReportService {

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private SnapshotService snapshotService;

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
    public void generatePdfFromHtml(String html, OutputStream outputStream) throws IOException {
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
     * @throws WrongPathException
     */
    public Queue<ThreatSituation> getThreatSituationQueueByReportFocus(ReportFocus reportFocus, String username, long snapId) throws WrongPathException {

        List<User> userList;
        switch (reportFocus){
            case COMPANY:
                userList = userService.getUsersByCompany(userService.getUserByUsername(username).getCompany());
                break;
            case BRANCHE:
                userList = userService.getUsersByBranche( userService.getUserByUsername(username).getBranche());
                break;
            case SECTOR:
                userList = userService.getUsersBySector(userService.getUserByUsername(username).getSector());
                break;
            case NATIONAL:
                userList = userService.getAllUsers();
                break;
            default:
                //TODO better Exceptionname
                throw new WrongPathException();
        }
        Snapshot newestSnapshot = snapshotService.getSnapshotByID(snapId).get();
        snapshotService.getAllQuestionnaires(newestSnapshot.getId());

        //remove all unimportant questionnaires
        List<Questionnaire> questionnaireList = questionnaireService.getQuestionnairesWithUsersInside(snapshotService.getAllQuestionnaires(newestSnapshot.getId()), userList);
        List<Queue<ThreatSituation>> queueList = new LinkedList<Queue<ThreatSituation>>();

        for (Questionnaire questionnaire : questionnaireList) {
            final Map<Long, String[]> questMap = questionnaireService.getMapping(questionnaire);
            queueList.add(questionnaireService.getThreatSituationQueueFromMapping(questMap));
        }

        return questionnaireService.getThreatSituationAverageQueueFromQueues(queueList);
    }
}
