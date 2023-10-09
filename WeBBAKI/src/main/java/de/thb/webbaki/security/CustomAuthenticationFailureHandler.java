package de.thb.webbaki.security;

import de.thb.webbaki.entity.User;
import de.thb.webbaki.service.Exceptions.UserNotEnabledException;
import de.thb.webbaki.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof DisabledException && isCorrectCredentials(request)) {
            response.sendRedirect("/login?notEnabled");
        } else {
            response.sendRedirect("/login?error");
        }
    }

    private boolean isCorrectCredentials(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userService.getUserByUsername(username);
        if (user != null) {
            boolean result = passwordEncoder.matches(password, user.getPassword());
            return result;
        }

        return false;
    }
}
