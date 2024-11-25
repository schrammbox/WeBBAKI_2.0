package de.thb.webbaki.controller;

import de.thb.webbaki.controller.form.ResetPasswordForm;
import de.thb.webbaki.entity.Branch;
import de.thb.webbaki.entity.PasswordResetToken;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.security.authority.UserAuthority;
import de.thb.webbaki.service.Exceptions.EmailNotMatchingException;
import de.thb.webbaki.service.Exceptions.PasswordResetTokenExpired;
import de.thb.webbaki.service.PasswordResetTokenService;
import de.thb.webbaki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;

@Controller
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    /**
     * Returning the home site as html.
     * @param request
     * @return
     */
    @GetMapping("/")
    public String home(HttpServletRequest request) {

        return "home";
    }

    /**
     * Returning the account information sites.
     * @return
     */
    @GetMapping("account")
    public String securedAccountPage() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().toString();

        if (role.contains(UserAuthority.USER)) {
            return "account/account_user";
        } else return "home";
    }

    @GetMapping("/setLogout")
    public void logintime(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        userService.setCurrentLogin(user);
    }

    /**
     * Returning the site for the request of the password reset.
     * @return
     */
    @GetMapping("/request_password_reset")
    public String showPasswordReset() {
        return "security/request_passwordReset";
    }

    /**
     * The endpoint for the requested password post request.
     * @param form
     * @param model
     * @return
     */
    @PostMapping("/request_password_reset")
    public String requestPasswordReset(@Valid ResetPasswordForm form, Model model) {

        try {
            model.addAttribute("form", form);
            User user = userService.getUserByUsername(form.getUsername());
            if(user != null) {
                passwordResetTokenService.createPasswordResetToken(user);
                passwordResetTokenService.createPasswordResetToken(user);
            }
            model.addAttribute("success", "Eingabe erfolgreich. Sofern Ihre Email einem Nutzer zugeordnet werden kann erhalten Sie demnächst eine Benachrichtigung per Mail.");

        } catch (EmailNotMatchingException e) {
            model.addAttribute("error", "Bei der Eingabe ist ein Fehler aufgetreten. Bitte stellen Sie sicher, dass die eingegebene Email auch korrekt ist.");
            return "security/request_passwordReset";
        }
        return "security/request_passwordReset";

    }

    /**
     * Returning the site for resetting of the password.
     * @param token
     * @param model
     * @return
     */
    @GetMapping(path = "/reset_password")
    public String showResetPassword(@RequestParam("token") String token, Model model) {
        PasswordResetToken passwordResetToken = passwordResetTokenService.getByToken(token);
        if (passwordResetToken != null) {
            ResetPasswordForm form = new ResetPasswordForm();
            form.setToken(token);
            model.addAttribute("form", form);
            return "security/reset_password";
        } else return "Token existiert nicht";
    }

    /**
     * The endpoint for the password reset post request.
     * @param form
     * @param model
     * @return
     * @throws PasswordResetTokenExpired
     */
    @PostMapping(path = "/reset_password")
    public String resetUserPassword(@Valid ResetPasswordForm form, Model model) throws PasswordResetTokenExpired {

        try {
            model.addAttribute("form", form);
            if (passwordResetTokenService.resetUserPassword(form.getToken(), form)) {
                model.addAttribute("success", "Ihr Passwort wurde erfolgreich geändert.");
            } else {
                model.addAttribute("error", "Beim Zurücksetzen Ihres Passworts ist ein Fehler aufgetreten.");
            }
        } catch (PasswordResetTokenExpired passEx) {
            model.addAttribute("error", "Ihr Token zum Zurücksetzen des Passworts ist abgelaufen. Bitte beantragen Sie ihn erneut unter \" Passwort vergessen\".");
        }

        return "security/reset_password";
    }

    /**
     * Returning the site with the "datenschutz".
     * @return
     */
    @GetMapping(path = "/datenschutz")
    public String showDataProtection(){
        return "datenschutz";
    }

}
