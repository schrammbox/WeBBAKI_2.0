package de.thb.webbaki.controller.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordForm {

    private String email;
    private String username;
    private String newPassword;
    private String confirmPassword;
    private String token;
}
