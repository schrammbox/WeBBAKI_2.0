package de.thb.webbaki.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class for reading token expiry dates from the application.properties.
 */
@Component
@ConfigurationProperties("webbaki.expiry")
@Getter
@Setter
public class ExpiryDaysReader {
    private int user;
    private int admin;
}
