package de.thb.webbaki.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class for reading the hostname from the application.properties.
 */
@Component
@ConfigurationProperties("webbaki.backend")
@Getter
@Setter
public class HostnameReader {
    private String hostname;

    public String getHostnameWithoutEnding(){
        String res = hostname;
        if (res != null && res.charAt(hostname.length() - 1) == '/'){
            res = res.substring(0, hostname.length() - 1);
        }

        return res;
    }
}
