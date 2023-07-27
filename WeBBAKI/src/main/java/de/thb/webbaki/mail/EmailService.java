package de.thb.webbaki.mail;

import de.thb.webbaki.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("emailService")
@AllArgsConstructor
public class EmailService implements EmailSender{

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    @Value("${webbaki.mail.host}")
    private String mailHost;
    @Value("${webbaki.mail.port}")
    private String mailPort;
    @Value("${webbaki.mail.user}")
    private String mailUser;
    @Value("${webbaki.mail.password}")
    private String mailPassword;

    @Override
    @Async
    public void send(String to, String email) {

        /*String filePath = new File("").getAbsolutePath();
        filePath = filePath + "/WeBBAKI/src/main/java/de/thb/webbaki/mail/config.conf";
        filePath = filePath.replace('\\', '/');

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder(reader.lines().collect(Collectors.joining(System.lineSeparator())));
        List<String> eig = Arrays.asList(sb.toString().split("\\s*;\\s*"));

        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Änderung auf WebBakI");
            helper.setFrom("noreply@th-brandenburg.de");
            javaMailSender.send(mimeMessage);
        }catch (MessagingException e){
            LOGGER.error("Fehler beim Senden der Nachricht", e);
            throw new IllegalStateException("Failed to send mail");
        }
    }
}
