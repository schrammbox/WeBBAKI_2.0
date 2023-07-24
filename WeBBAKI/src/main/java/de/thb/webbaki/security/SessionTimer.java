package de.thb.webbaki.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class SessionTimer {

    @Value("${server.servlet.session.timeout}")
    private long sessionTimeoutInSeconds;

    public Long getSessionTimeoutInSeconds() {
        return sessionTimeoutInSeconds;
    }
}
