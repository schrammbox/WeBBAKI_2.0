package de.thb.webbaki.controller.form;

import de.thb.webbaki.security.passwordValidation.PasswordMatches;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCredentialsForm {

    private String oldPassword;
    private String newPassword;
    private String oldEmail;
    private String newEmail;
    private String newFirstname;
    private String newLastname;
}
