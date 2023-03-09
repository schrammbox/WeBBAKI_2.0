package de.thb.webbaki.controller;

import de.thb.webbaki.controller.form.ResetPasswordForm;
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

import javax.validation.Valid;

@Controller
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

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

    @GetMapping("/request_password_reset")
    public String showPasswordReset() {
        return "security/request_passwordReset";
    }

    @PostMapping("/request_password_reset")
    public String requestPasswordReset(@Valid ResetPasswordForm form, Model model) {

        try {
            model.addAttribute("form", form);
            User user = userService.getUserByUsername(form.getUsername());
            passwordResetTokenService.createPasswordResetToken(user);
            model.addAttribute("success", "Eingabe erfolgreich. Sofern Ihre Email einem Nutzer zugeordnet werden kann erhalten Sie demnächst eine Benachrichtigung per Mail.");

        } catch (EmailNotMatchingException e) {
            model.addAttribute("error", "Bei der Eingabe ist ein Fehler aufgetreten. Bitte stellen Sie sicher, dass die eingegebene Email auch korrekt ist.");
            return "security/request_passwordReset";
        }
        return "security/request_passwordReset";

    }

    @GetMapping(path = "/reset_password")
    public String showResetPassword(@RequestParam("token") String token, Model model) {

        if (passwordResetTokenService.getByToken(token) != null) {
            ResetPasswordForm form = new ResetPasswordForm();
            form.setToken(token);
            model.addAttribute("form", form);
            return "security/reset_password";
        } else return "Token existiert nicht";
    }

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

    @GetMapping(path = "/datenschutz")
    public String showDataProtection(){
        return "datenschutz";
    }

}
