package de.thb.webbaki.service;

import de.thb.webbaki.controller.form.UserForm;
import de.thb.webbaki.controller.form.UserRegisterFormModel;
import de.thb.webbaki.controller.form.UserToRoleFormModel;
import de.thb.webbaki.entity.Role;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.mail.EmailSender;
import de.thb.webbaki.mail.Templates.AdminNotifications.AdminChangeBrancheSubmit;
import de.thb.webbaki.mail.Templates.AdminNotifications.AdminDeactivateUserSubmit;
import de.thb.webbaki.mail.Templates.AdminNotifications.AdminRegisterNotification;
import de.thb.webbaki.mail.Templates.AdminNotifications.AdminRemoveRoleNotification;
import de.thb.webbaki.mail.Templates.UserNotifications.*;
import de.thb.webbaki.mail.confirmation.ConfirmationToken;
import de.thb.webbaki.mail.confirmation.ConfirmationTokenService;
import de.thb.webbaki.repository.RoleRepository;
import de.thb.webbaki.repository.UserRepository;
import de.thb.webbaki.service.Exceptions.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class UserService {
    private UserRepository userRepository; ////initialize repository Object
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private ConfirmationTokenService confirmationTokenService;
    private EmailSender emailSender;

    @Autowired
    private SectorService sectorService;

    //Repo Methods --------------------------
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getUsersByBranche(String branche) {
        return userRepository.findAllByBranche(branche);
    }

    public List<User> getUsersByCompany(String company) {
        return userRepository.findAllByCompany(company);
    }

    public List<User> getUsersBySector(String sector) {
        return userRepository.findAllBySector(sector);
    }

    public Boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public List<User> getUserByAdminrole() {
        return userRepository.findByRoles_Name("ROLE_SUPERADMIN");
    }

    public List<User> getUserByOfficeRole() {
        return userRepository.findByRoles_Name("ROLE_GESCHÄFTSSTELLE");
    }

    /**
     * @param user is used to create new user -> forwarded to registerNewUser
     * @return newly created token
     */
    public String createToken(User user) {

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token, LocalDateTime.now(), LocalDateTime.now().plusDays(3), user);

        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    /**
     * Registering new User with all parameters from User.java
     * Using emailExists() to check whether user already exists
     */
    public void registerNewUser(final UserRegisterFormModel form) throws UserAlreadyExistsException {
        if (usernameExists(form.getUsername())) {
            throw new UserAlreadyExistsException("Es existiert bereits ein Account mit folgender Email-Adresse: " + form.getEmail());
        } else {

            final User user = new User();

            user.setLastName(form.getLastname());
            user.setFirstName(form.getFirstname());
            user.setBranche(form.getBranche());
            user.setSector(sectorService.getSectorByBrancheName(form.getBranche()).getName());
            user.setCompany(form.getCompany());
            user.setPassword(passwordEncoder.encode(form.getPassword()));
            user.setEmail(form.getEmail());
            user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_DEFAULT_USER")));
            user.setUsername(form.getUsername());
            user.setEnabled(false);

            String token = createToken(user); // To create the token of the user


            String userLink = "https://webbaki.th-brandenburg.de/confirmation/confirmByUser?token=" + token;

            userRepository.save(user);


            //Email to new registered user
            emailSender.send(form.getEmail(), UserRegisterNotification.buildUserEmail(form.getFirstname(), userLink));
        }
    }

    @Transactional
    public String confirmToken(String token) throws IllegalStateException {
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token);

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        } else {
            confirmationTokenService.setConfirmedAt(token);
            confirmAdmin(token);
        }
        enableUser(confirmationToken.getUser().getUsername(), token);

        return "confirmation/confirm";
    }

    /**
     * Using USERDETAILS -> Enabling User in Spring security
     * User is enabled if user_confirmation && admin_confirmation == TRUE
     *
     * @param username to get the user
     * @param token    to get the according token
     * @return value TRUE or FALSE based on INTEGER value (0 = false, 1 = true)
     */
    public int enableUser(String username, String token) {

        if (confirmationTokenService.getConfirmationToken(token).accessGranted(token)) {
            return userRepository.enableUser(username);
        } else return -1;
    }

    /**
     * Setting user_confirmation TRUE or False
     *
     * @param token to get matching ConfirmationToken
     * @return value TRUE or FALSE based on bit value (0 = false, 1 = true)
     */
    public int userConfirmation(String token) {
        return confirmationTokenService.setConfirmedByUser(token);
    }

    /**
     * Setting admin_confirmation TRUE or False
     *
     * @param token to get matching ConfirmationToken
     * @return value TRUE or FALSE based on bit value (0 = false, 1 = true)
     */
    public int adminConfirmation(String token) {
        return confirmationTokenService.setConfirmedByAdmin(token);
    }

    /**
     * Setting user_confirmation TRUE or False and getting HTML Page with confirmation Details
     * using method public int userConfirmation(String token)
     *
     * @param token to get matching ConfirmationToken
     * @return value TRUE or FALSE based on bit value (0 = false, 1 = true)
     */
    public String confirmUser(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token);
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }else{
            //The confirmation only should be done if its not already done
            if(!confirmationToken.getUserConfirmation()) {
                userConfirmation(token);

                //send link to admin
                String adminLink = "https://webbaki.th-brandenburg.de/confirmation/confirm?token=" + token;
                User user = confirmationToken.getUser();
                for (User superAdmin : getUserByAdminrole()) {
                    emailSender.send(superAdmin.getEmail(), AdminRegisterNotification.buildAdminEmail(superAdmin.getFirstName(), adminLink,
                            user.getFirstName(), user.getLastName(),
                            user.getEmail(), user.getBranche(), user.getCompany()));
                }
            }
        }
        return "confirmation/confirmedByUser";
    }

    /**
     * Setting admin_confirmation TRUE or False and getting HTML Page with confirmation Details
     * using method public int adminConfirmation(String token)
     *
     * @param token to get matching ConfirmationToken
     * @return value TRUE or FALSE based on bit value (0 = false, 1 = true)
     */
    public String confirmAdmin(String token) {
        adminConfirmation(token);
        return "confirmation/confirm";
    }

    public void setCurrentLogin(User u) {
        u.setLastLogin(LocalDateTime.now());
        userRepository.save(u);
    }

    /**
     * USED IN SUPERADMIN DASHBOARD
     * Superadmin can add Roles to specific Users
     *
     * @param formModel to get Userdata, especially roles from user
     */
    public void addRoleToUser(final UserToRoleFormModel formModel) {


        String[] roles = formModel.getRole();

        for (int i = 1; i < roles.length; i++) {
            if (!Objects.equals(roles[i], "none") && !Objects.equals(roles[i], null)) {
                User user = userRepository.findById(i).get();
                if (!user.getRoles().contains(roleRepository.findByName(roles[i]))) {
                    user.addRole((roleRepository.findByName((roles[i]))));

                    emailSender.send(user.getEmail(), AddRoleNotification.changeRoleMail(user.getFirstName(),
                            user.getLastName(),
                            roleRepository.findByName(roles[i])));

                    for (User superAdmin : getUserByAdminrole()) {
                        emailSender.send(superAdmin.getEmail(), AddRoleNotification.changeRoleMail(superAdmin.getFirstName(),
                                superAdmin.getLastName(),
                                roleRepository.findByName(roles[i])));
                    }
                }
            }
        }
    }

    /**
     * USED IN SUPERADMIN DASHBOARD
     * Superadmin can delete Roles to specific Users
     *
     * @param formModel to get Userdata, especially roles from user
     */
    public void removeRoleFromUser(final UserToRoleFormModel formModel) {
        String[] roleDel = formModel.getRoleDel();
        for (int i = 1; i < roleDel.length; i++) {
            if (!Objects.equals(roleDel[i], "none") && !Objects.equals(roleDel[i], null)) {
                User user = userRepository.findById(i).get();
                Role role = roleRepository.findByName(roleDel[i]);
                user.removeRole(role);

                emailSender.send(user.getEmail(), RemoveRoleNotification.removeRoleMail(user.getFirstName(),
                        user.getLastName(),
                        roleRepository.findByName(roleDel[i])));

                for (User superAdmin : getUserByOfficeRole()) {
                    emailSender.send(superAdmin.getEmail(), AdminRemoveRoleNotification.removeRole(superAdmin.getFirstName(),
                            superAdmin.getLastName(),
                            roleRepository.findByName(roleDel[i]),
                            user.getUsername()));
                }
            }
        }
    }

    /**
     * Enable/Disable user to give access to WebBakI
     *
     * @param form to get userlist
     */
    public void changeEnabledStatus(UserForm form) {
        ChangeEnabledStatusNotification enabledStatusNotification = new ChangeEnabledStatusNotification();
        AdminDeactivateUserSubmit deactivateUserSubmit = new AdminDeactivateUserSubmit();

        List<User> users = getAllUsers();

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).isEnabled() != (form.getUsers().get(i).isEnabled())) {
                users.get(i).setEnabled(form.getUsers().get(i).isEnabled());

                emailSender.send(users.get(i).getEmail(), enabledStatusNotification.changeBrancheMail(users.get(i).getFirstName(), users.get(i).getLastName()));

                for (User officeAdmin : getUserByOfficeRole()) {
                    emailSender.send(officeAdmin.getEmail(), deactivateUserSubmit.changeEnabledStatus(officeAdmin.getFirstName(),
                            officeAdmin.getLastName(),
                            users.get(i).isEnabled(),
                            users.get(i).getUsername()));
                }
            }
        }
    }

    public void changeBranche(UserForm form) {
        ChangeBrancheNotification brancheNotification = new ChangeBrancheNotification(); // To send mail
        AdminChangeBrancheSubmit changeBrancheSubmit = new AdminChangeBrancheSubmit(); // Send Mail to admin

        List<User> users = getAllUsers();

        for (int i = 0; i < users.size(); i++) {
            if (!Objects.equals(users.get(i).getBranche(), form.getUsers().get(i).getBranche())) {
                users.get(i).setBranche(form.getUsers().get(i).getBranche());

                emailSender.send(users.get(i).getEmail(), brancheNotification.changeBrancheMail(users.get(i).getFirstName(),
                        users.get(i).getLastName(),
                        users.get(i).getBranche()));

                for (User officeAdmin : getUserByOfficeRole()) {
                    emailSender.send(officeAdmin.getEmail(), changeBrancheSubmit.changeBrancheMail(officeAdmin.getFirstName(),
                            officeAdmin.getLastName(),
                            users.get(i).getBranche(),
                            users.get(i).getUsername()));
                }
            }
        }
    }
}
