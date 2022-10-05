package de.thb.webbaki.controller;

import de.thb.webbaki.entity.User;
import de.thb.webbaki.repository.UserRepository;
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
    UserRepository userRepository;

    @GetMapping("/office")
    public String showOfficePage(Model model){
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "permissions/office";
    }

    @RequestMapping(value = "office", method = RequestMethod.POST)
    @PostMapping("/office")
    public String deactivateUser(Model model){
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        String user = String.valueOf(new User());

        for (int i = 0; i < users.size(); i++){
            user = users.get(i).getUsername();
        }

        userService.deactivateUser(user);
        userRepository.saveAll(users);

        return "redirect:office";
    }
}
