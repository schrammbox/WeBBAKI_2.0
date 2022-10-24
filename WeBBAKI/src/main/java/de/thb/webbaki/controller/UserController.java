package de.thb.webbaki.controller;

import de.thb.webbaki.controller.form.UserRegisterFormModel;
import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.mail.confirmation.ConfirmationTokenService;
import de.thb.webbaki.repository.QuestionnaireRepository;
import de.thb.webbaki.service.Exceptions.UserAlreadyExistsException;
import de.thb.webbaki.service.ScenarioService;
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
import java.time.LocalDateTime;
import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private QuestionnaireRepository questionnaireRepository;
    private ConfirmationTokenService confirmationTokenService;
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
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setUser(userService.getUserByUsername(formModel.getUsername()));
            questionnaire.setDate(LocalDateTime.now());
            questionnaire.setSmallComment("");
            questionnaire.setMapping("{1=none;none, 2=none;none, 3=none;none, 4=none;none, 5=none;none, 6=none;none, 7=none;none, 8=none;none, 9=none;none, 10=none;none, 11=none;none, 12=none;none, 13=none;none, 14=none;none, 15=none;none, 16=none;none, 17=none;none, 18=none;none, 19=none;none, 20=none;none, 21=none;none, 22=none;none, 23=none;none, 24=none;none, 25=none;none, 26=none;none, 27=none;none}");
            questionnaireRepository.save(questionnaire);

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

}





