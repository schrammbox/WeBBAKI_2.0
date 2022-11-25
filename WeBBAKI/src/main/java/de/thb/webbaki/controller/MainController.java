package de.thb.webbaki.controller;

import de.thb.webbaki.entity.User;
import de.thb.webbaki.security.authority.UserAuthority;
import de.thb.webbaki.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {
    private UserService userService;

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

    @PostMapping("/resetPassword")
    public String resetPassword()

}
