package de.thb.webbaki.service;

import de.thb.webbaki.controller.form.UserRegisterFormModel;
import de.thb.webbaki.controller.form.UserToRoleFormModel;
import de.thb.webbaki.entity.Role;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.mail.EmailSender;
import de.thb.webbaki.mail.confirmation.ConfirmationToken;
import de.thb.webbaki.mail.confirmation.ConfirmationTokenService;
import de.thb.webbaki.repository.RoleRepository;
import de.thb.webbaki.repository.UserRepository;
import de.thb.webbaki.service.Exceptions.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
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

    public Boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
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
    public User registerNewUser(final UserRegisterFormModel form) throws UserAlreadyExistsException {
        if (usernameExists(form.getUsername())) {
            throw new UserAlreadyExistsException("Es existiert bereits ein Account mit folgender Email-Adresse: " + form.getEmail());
        } else {

            final User user = new User();

            user.setLastName(form.getLastname());
            user.setFirstName(form.getFirstname());
            user.setBranche(form.getBranche());
            user.setCompany(form.getCompany());
            user.setPassword(passwordEncoder.encode(form.getPassword()));
            user.setEmail(form.getEmail());
            user.setRoles(Arrays.asList(roleRepository.findByName("DEFAULT_USER")));
            user.setUsername(form.getUsername());
            user.setEnabled(false);

            String token = createToken(user); // To create the token of the user


            String userLink = "https://webbaki.th-brandenburg.de/confirmation/confirmByUser?token=" + token;
            String adminLink = "https://webbaki.th-brandenburg.de/confirmation/confirm?token=" + token;

            //Email to Superadmin
            emailSender.send("schrammbox@proton.me", buildAdminEmail("Christian", adminLink,
                                                                                form.getFirstname(), form.getLastname(),
                                                                                form.getEmail(), form.getBranche(), form.getCompany()));

            //Email to Superadmin
            emailSender.send("schoenbe@th-brandenburg.de", buildAdminEmail("Leon", adminLink,
                    form.getFirstname(), form.getLastname(),
                    form.getEmail(), form.getBranche(), form.getCompany()));

            //Email to new registered user
            emailSender.send(form.getEmail(), buildUserEmail(form.getFirstname(), userLink));

            return userRepository.save(user);
        }
    }

    @Transactional
    public String confirmToken(String token) throws IllegalStateException {
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token);

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed.");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        } else {
            confirmationTokenService.setConfirmedAt(token);
            confirmAdmin(token);
        }
        enableUser(confirmationToken.getUser().getEmail(), token);

        return "/confirmation/confirm";
    }

    /**
     * Using USERDETAILS -> Enabling User in Spring security
     * User is enabled if user_confirmation && admin_confirmation == TRUE
     *
     * @param email to get the user
     * @param token to get the according token
     * @return value TRUE or FALSE based on INTEGER value (0 = false, 1 = true)
     */
    public int enableUser(String email, String token) {

        if (confirmationTokenService.getConfirmationToken(token).accessGranted(token)) {
            return userRepository.enableUser(email);
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
        userConfirmation(token);
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
                Role r = roleRepository.findByName(roleDel[i]);
                user.removeRole(r);


            }
        }
    }

    private String buildAdminEmail(String name, String link, String userFirstname, String userLastname,
                                   String userEmail, String userBranche, String userCompany) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"de\" dir=\"ltr\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title></title>\n" +
                "  </head>\n" +
                "\n" +
                "  <style>\n" +
                "    p{\n" +
                "      font-size:16px;\n" +
                "    }\n" +
                "\n" +
                "    html {\n" +
                "      font-family: sans-serif;\n" +
                "      text-align:center;\n" +
                "      align-content:center;\n" +
                "    }\n" +
                "\n" +
                "    table {\n" +
                "      width:560px;\n" +
                "      border-collapse: collapse;\n" +
                "      border: 2px solid rgb(200,200,200);\n" +
                "      letter-spacing: 1px;\n" +
                "      font-size: 0.9rem;\n" +
                "    }\n" +
                "\n" +
                "    td, th {\n" +
                "      border: 1px solid rgb(190,190,190);\n" +
                "      padding: 10px 20px;\n" +
                "    }\n" +
                "\n" +
                "    th {\n" +
                "      background-color: rgb(235,235,235);\n" +
                "    }\n" +
                "\n" +
                "    td {\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "\n" +
                "    tr:nth-child(even) td {\n" +
                "      background-color: rgb(250,250,250);\n" +
                "    }\n" +
                "\n" +
                "    tr:nth-child(odd) td {\n" +
                "      background-color: rgb(245,245,245);\n" +
                "    }\n" +
                "\n" +
                "    caption {\n" +
                "      padding: 10px;\n" +
                "    }\n" +
                "  </style>\n" +
                "\n" +
                "  <body>\n" +
                "    <h2 style=\"background-color:black; color: white; padding: 20px 0; margin: 0 auto;\">Neue Registrierung auf WebBaKI</h2>\n" +
                "    <p>Hallo "+name+",</p>\n" +
                "    <p>Es hat sich ein neuer WebBaKI-Nutzer registriert. Infos zum Nutzer:</p>\n" +
                "    <div class=\"tabledata\" style=\"display:flex;align-items:center; justify-content:center\">\n" +
                "      <table style=\"\">\n" +
                "          <tr>\n" +
                "            <td>Vorname</td>\n" +
                "            <td>"+userFirstname+"</td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td>Nachname</td>\n" +
                "            <td>"+userLastname+"</td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td>Firma</td>\n" +
                "            <td>"+userCompany+"</td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td>Email</td>\n" +
                "            <td>"+userEmail+"</td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td>Branche</td>\n" +
                "            <td>"+userBranche+"</td>\n" +
                "          </tr>\n" +
                "      </table>\n" +
                "    </div>\n" +
                "    <p>Der Account kann unter folgendem Link aktiviert oder abgelehnt werden:</p>\n" +
                "      <p>\n" +
                "        <a href="+link+">Nutzer verifizieren</a>\n" +
                "        <span></span>\n" +
                "        <a href=\"http://localhost:8080/confirmation/userDenied\">Nutzer ablehnen</a>\n" +
                "      </p>\n" +
                "    <p>Der Link bleibt 3 Tage gültig.</p>\n" +
                "  </body>\n" +
                "</html>\n";
    }

    // ------------------------------MAIL TO USER --------------------------------------------------------
    private String buildUserEmail(String name, String link) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"de\" dir=\"ltr\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title></title>\n" +
                "  </head>\n" +
                "\n" +
                "  <style>\n" +
                "    p{\n" +
                "      font-size:16px;\n" +
                "    }\n" +
                "\n" +
                "    html {\n" +
                "      font-family: sans-serif;\n" +
                "      text-align:center;\n" +
                "      align-content:center;\n" +
                "    }\n" +
                "\n" +
                "    table {\n" +
                "      width:560px;\n" +
                "      border-collapse: collapse;\n" +
                "      border: 2px solid rgb(200,200,200);\n" +
                "      letter-spacing: 1px;\n" +
                "      font-size: 0.9rem;\n" +
                "    }\n" +
                "\n" +
                "    td, th {\n" +
                "      border: 1px solid rgb(190,190,190);\n" +
                "      padding: 10px 20px;\n" +
                "    }\n" +
                "\n" +
                "    th {\n" +
                "      background-color: rgb(235,235,235);\n" +
                "    }\n" +
                "\n" +
                "    td {\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "\n" +
                "    tr:nth-child(even) td {\n" +
                "      background-color: rgb(250,250,250);\n" +
                "    }\n" +
                "\n" +
                "    tr:nth-child(odd) td {\n" +
                "      background-color: rgb(245,245,245);\n" +
                "    }\n" +
                "\n" +
                "    caption {\n" +
                "      padding: 10px;\n" +
                "    }\n" +
                "  </style>\n" +
                "\n" +
                "  <body>\n" +
                "    <h2 style=\"background-color:black; color: white; padding: 20px 0; margin: 0 auto;\">Neue Registrierung auf WebBaKI</h2>\n" +
                "    <p>Hallo "+name+",</p>\n" +
                "    <p>Vielen Dank für die Registrierung. Bitte bestätige deine Email unter folgendem Link:</p>\n" +
                "      <p>\n" +
                "        <a href="+link+">Verifizieren</a>\n" +
                "        <span></span>\n" +
                "      </p>\n" +
                "    <p>Der Link bleibt 3 Tage gültig.</p>\n" +
                "  </body>\n" +
                "</html>\n";
    }

}
