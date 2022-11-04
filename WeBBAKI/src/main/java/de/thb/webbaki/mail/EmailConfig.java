
package de.thb.webbaki.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

@Configuration
public class EmailConfig {


    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("mail.th-brandenburg.de");
        mailSender.setPort(25);
        mailSender.setUsername("noreply@th-brandenburg.de");

        Properties mailProperties = mailSender.getJavaMailProperties();
        mailProperties.put("mail.transport.protocol", "smtp");
        mailProperties.put("mail.smtp.auth", "false");
        mailProperties.put("mail.smtp.starttls.enable", "true");
        mailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailProperties.put("mail.debug", "false");
        mailSender.setJavaMailProperties(mailProperties);
        return mailSender;
    }
}
