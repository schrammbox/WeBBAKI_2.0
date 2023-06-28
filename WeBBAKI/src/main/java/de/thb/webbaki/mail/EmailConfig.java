
package de.thb.webbaki.mail;

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


    @Bean
    public JavaMailSender javaMailSender() throws IOException {

        String filePath = new File("").getAbsolutePath();
        filePath = filePath + "/WeBBAKI/src/main/java/de/thb/webbaki/mail/config.conf";
        filePath = filePath.replace('\\', '/');

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder sb = new StringBuilder(reader.lines().collect(Collectors.joining(System.lineSeparator())));
        List<String> eig = Arrays.asList(sb.toString().split("\\s*;\\s*"));

        reader.close();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(eig.get(0)); // mail.th-brandenburg.de - smtp.gmail.com
        mailSender.setPort(Integer.parseInt(eig.get(1))); // 25
        mailSender.setUsername(eig.get(2)); // noreply@th-brandenburg.de - webbakinoreply@gmail.com
        mailSender.setPassword(eig.get(3)); // mdtikeksobwrseob

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
