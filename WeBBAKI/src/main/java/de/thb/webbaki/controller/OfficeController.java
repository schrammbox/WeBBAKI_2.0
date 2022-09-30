package de.thb.webbaki.controller;

import de.thb.webbaki.entity.User;
import de.thb.webbaki.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class OfficeController {

    UserService userService;

    @GetMapping("/office")
    public String showOfficePage(Model model){
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "permissions/office";
    }

    @RequestMapping(value = "office", method = RequestMethod.POST)
    @PostMapping("/office")
    public String deactivateUser(@ModelAttribute("users") Model model){
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        return "permissions/office";
    }
}
