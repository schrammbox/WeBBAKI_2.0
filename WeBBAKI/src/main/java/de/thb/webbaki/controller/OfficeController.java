package de.thb.webbaki.controller;

import de.thb.webbaki.controller.form.BranchForm;
import de.thb.webbaki.controller.form.UserForm;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.repository.UserRepository;
import de.thb.webbaki.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
@SessionAttributes("form")
public class OfficeController {

    UserService userService;
    UserRepository userRepository;

    @GetMapping("/office")
    public String showOfficePage(Model model){
        final var users = userService.getAllUsers();

        UserForm form = new UserForm();
        String branche = form.getBranche();

        form.setUsers(users);

        model.addAttribute("form", form);
        model.addAttribute("users", users);
        model.addAttribute("branche", form);


        return "permissions/office";
    }

    @PostMapping("/office")
    public String deactivateUser(@ModelAttribute("form") @Valid UserForm form){
        System.out.println(form.getUsers());

        userService.deactivateUser(form);
        userService.changeBranche(form);

        return "redirect:office";
    }
}
