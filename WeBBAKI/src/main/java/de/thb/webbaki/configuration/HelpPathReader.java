package de.thb.webbaki.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class for defining path of help.pdf in the application.properties
 */
@Component
@ConfigurationProperties("webbaki.help")
public class HelpPathReader {
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
