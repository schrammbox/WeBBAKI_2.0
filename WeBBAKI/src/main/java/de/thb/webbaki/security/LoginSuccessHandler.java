package de.thb.webbaki.security;

import de.thb.webbaki.entity.User;
import de.thb.webbaki.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final SessionTimer sessionTimer;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Get the logged-in user's username
        String username = authentication.getName();

        // Get the user from the database
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Long sessionDuration = sessionTimer.getSessionTimeoutInSeconds();
            Long sessionExpiresAt = System.currentTimeMillis() / 1000 + sessionDuration;
            user.setSessionExpiresAt(sessionExpiresAt);
            userRepository.save(user);
        }
        // default success url:
        setDefaultTargetUrl("/account");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
