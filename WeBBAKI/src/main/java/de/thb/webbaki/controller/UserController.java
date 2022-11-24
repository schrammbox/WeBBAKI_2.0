package de.thb.webbaki.controller;

import de.thb.webbaki.controller.form.ChangeCredentialsForm;
import de.thb.webbaki.controller.form.UserRegisterFormModel;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.service.Exceptions.EmailNotMatchingException;
import de.thb.webbaki.service.Exceptions.PasswordNotMatchingException;
import de.thb.webbaki.service.Exceptions.UserAlreadyExistsException;
import de.thb.webbaki.service.SectorService;
import de.thb.webbaki.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    @Autowired
    SectorService sectorService;

    @Deprecated
    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> userList = userService.getAllUsers();
        model.addAttribute("userList", userList);
        return "users";
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

    @GetMapping("/data/user/threatmatrices")
    public String showCustomerOrders() {

        return "account/user_threatmatrices";
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

}





