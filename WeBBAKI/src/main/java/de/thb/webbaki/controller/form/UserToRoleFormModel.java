package de.thb.webbaki.controller.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserToRoleFormModel {

    //Get Role for User
    private String[] role;
    private String[] roleDel;
    //Get User for Role
    private String[] user;
}
