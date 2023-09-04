package de.thb.webbaki.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class for defining path of help.pdf in the application.properties
 */
@Getter
@Setter
@Component
@ConfigurationProperties("webbaki.mail")
public class DatabaseCredentialReader {
    private String host;
    private String port;
    private String user;
    private String password;
}
