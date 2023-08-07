
package de.thb.webbaki.mail;

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

    @Value("${webbaki.mail.host}")
    private String mailHost;
    @Value("${webbaki.mail.port}")
    private String mailPort;
    @Value("${webbaki.mail.user}")
    private String mailUser;
    @Value("${webbaki.mail.password}")
    private String mailPassword;

    @Bean
    public JavaMailSender javaMailSender() throws IOException {

        /*
        String filePath ;//= new File("").getAbsolutePath();
        filePath = "/opt/webbaki-admin/config.conf";
        filePath = filePath.replace('\\', '/');

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder sb = new StringBuilder(reader.lines().collect(Collectors.joining(System.lineSeparator())));
        List<String> eig = Arrays.asList(sb.toString().split("\\s*;\\s*"));

        reader.close();*/

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost); // mail.th-brandenburg.de - smtp.gmail.com
        mailSender.setPort(Integer.parseInt(mailPort)); // 25
        mailSender.setUsername(mailUser); // noreply@th-brandenburg.de - webbakinoreply@gmail.com
        if(!mailPassword.equals("noPw")){
            mailSender.setPassword(mailPassword); // mdtikeksobwrseob
        }


        //maybe to do for later
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
