package de.thb.webbaki.controller.form;

import de.thb.webbaki.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserToRoleFormModel {

    //Get Role for User
    private List<String> role;
    private List<String> roleDel;
    //Get User for Role
    private List<User> users;
}
