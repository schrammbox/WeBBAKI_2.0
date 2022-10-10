package de.thb.webbaki.controller;

import de.thb.webbaki.controller.form.UserToRoleFormModel;
import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.Snapshot;
import de.thb.webbaki.service.RoleService;
import de.thb.webbaki.service.SnapshotService;
import de.thb.webbaki.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
public class SuperAdminController implements Comparable {
    private final UserService userService;
    private final RoleService roleService;
    private final SnapshotService snapshotService;

    @RequestMapping(value = "admin", method = RequestMethod.GET)
    public String showAllUsers(Model model) {
        final var users = userService.getAllUsers();
        /* If sorting of usernames needed
        users.sort(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getLastName().compareTo(o2.getLastName());
            }
        });
         */
        model.addAttribute("roleForm", new UserToRoleFormModel());
        final var roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("users", users);

        return "permissions/admin";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    @PostMapping("/admin")
    public String addRoleToUser(@ModelAttribute("roleForm") @Valid UserToRoleFormModel userToRoleFormModel) {
        System.out.println(userToRoleFormModel.toString());
        userService.addRoleToUser(userToRoleFormModel);
        userService.removeRoleFromUser(userToRoleFormModel);
        return "redirect:admin";
    }

    @GetMapping("/snap")
    public String getSnap(Model model) {

        List<Snapshot> snaps = snapshotService.getAllSnapshots();
        model.addAttribute("snaps", snaps);

        Snapshot snapName = new Snapshot();
        model.addAttribute("snapName", snapName);

        return "snap/snapshot";
    }

    @GetMapping("confirmation/userDenied")
    public String userDenied(){
        return "confirmation/userDenied";
    }

    @PostMapping("/snap")
    public String postSnap(@ModelAttribute("snapName") Snapshot snapName) {
        snapshotService.createSnap(snapName);


        return "redirect:snap";

    }

    @GetMapping("/snap/{snapID}")
    public String showSnapByID(@PathVariable("snapID") long snapID, Model model) {
        List<Questionnaire> questionnaires = snapshotService.getAllQuestionnaires(snapID);
        model.addAttribute("questionnaires", questionnaires);

        return "snap/details";
    }


    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
