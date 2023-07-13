package de.thb.webbaki.controller;

import de.thb.webbaki.configuration.HelpPathReader;
import de.thb.webbaki.controller.form.ChangeCredentialsForm;
import de.thb.webbaki.controller.form.UserRegisterFormModel;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.service.Exceptions.EmailNotMatchingException;
import de.thb.webbaki.service.Exceptions.PasswordNotMatchingException;
import de.thb.webbaki.service.Exceptions.UserAlreadyExistsException;
import de.thb.webbaki.service.SectorService;
import de.thb.webbaki.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    @Autowired
    SectorService sectorService;
    @Autowired
    HelpPathReader helpPathReader;
    @Autowired
    private HttpSession session;

    /**
     * the request object includes the session id
     * @param request
     * @return the formatted remaining time as a String
     */
    @GetMapping("/user/remainingTime")
    public ResponseEntity<String> getTokenRemainingTime(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Retrieve the current session without creating a new one
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session expired");
        }
        String sessionRestDuration = userService.calculateSessionRestDuration(session);
        if (sessionRestDuration == null) {
            sessionRestDuration = "Error while calculating remaining time";
        }
        return ResponseEntity.ok(sessionRestDuration);
    }


    @GetMapping("/register/user")
    public String showRegisterForm(Model model) {
        UserRegisterFormModel formModel = new UserRegisterFormModel();
        model.addAttribute("user", formModel);
        model.addAttribute("sectorList", sectorService.getAllSectors());
        return "register/user_registration";
    }

    @PostMapping("/register/user")
    public String registerUser(
            @ModelAttribute("user") @Valid UserRegisterFormModel formModel, BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("sectorList", sectorService.getAllSectors());
            return "register/user_registration";
        }

        try {
            userService.registerNewUser(formModel);

        } catch (UserAlreadyExistsException uaeEx) {
            model.addAttribute("usernameError", "Es existiert bereits ein Account mit diesem Nutzernamen.");
            model.addAttribute("sectorList", sectorService.getAllSectors());
            return "register/user_registration";
        }

        return "register/success_register";
    }


    @GetMapping("/account/user_details")
    public String showUserData(Authentication authentication, Model model) {

        User user = userService.getUserByUsername(authentication.getName());
        model.addAttribute("user", user);


        return "account/user_details";
    }

    @GetMapping(path = "/confirmation/confirmByUser")
    public String userConfirmation(@RequestParam("token") String token) {
        return userService.confirmUser(token);
    }

    @GetMapping(path = "/confirmation/confirm")
    public String confirm(@RequestParam("token") String token) {
        userService.confirmAdmin(token);
        return userService.confirmToken(token);
    }

    @GetMapping(path = "/account/changeCredentials")
    public String showChangePassword(){
        return "account/changeCredentials";
    }

    @PostMapping(path = "account/changeCredentials")
    public String changeCredentials(@Valid ChangeCredentialsForm form, Principal principal, Model model){

        try {
            String username = principal.getName();
            User user = userService.getUserByUsername(username);

            model.addAttribute("user", user);
            model.addAttribute("form", form);

            userService.changeCredentials(form,user, model);
        } catch (PasswordNotMatchingException passEx) {
            model.addAttribute("passwordError", "Das eingegebene Password stimmt nicht mit Ihrem aktuellen Passwort überein.");
            return "account/changeCredentials";
        } catch (EmailNotMatchingException e){
            model.addAttribute("emailError", "Die eingegebene Email-Adresse stimmt nicht mit Ihrer aktuellen Email überein.");
            return "account/changeCredentials";
        }

        return "account/changeCredentials";
    }

    @GetMapping(value="/help", produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] getHelp() throws IOException {
        File file = new File(helpPathReader.getPath() + "help.pdf");
        return IOUtils.toByteArray(new FileInputStream(file));
    }

}
