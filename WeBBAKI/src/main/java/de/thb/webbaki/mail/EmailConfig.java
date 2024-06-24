
package de.thb.webbaki.mail;

import de.thb.webbaki.configuration.DatabaseCredentialReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
public class EmailConfig {

    @Autowired
    private DatabaseCredentialReader databaseCredentialReader;

    @Bean
    public JavaMailSender javaMailSender() throws IOException {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(databaseCredentialReader.getHost());
        mailSender.setPort(Integer.parseInt(databaseCredentialReader.getPort()));
        mailSender.setUsername(databaseCredentialReader.getUser());
        String password = databaseCredentialReader.getPassword();
        if(!password.equals("noPw")){
            mailSender.setPassword(password);
        }


        //maybe to do for later
        Properties mailProperties = mailSender.getJavaMailProperties();
        mailProperties.put("mail.transport.protocol", "smtp");
        //mailProperties.put("mail.smtp.auth", "false");
        mailProperties.put("mail.smtp.starttls.enable", "true");
        mailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailProperties.put("mail.debug", "false");
        mailSender.setJavaMailProperties(mailProperties);
        return mailSender;
    }
}
