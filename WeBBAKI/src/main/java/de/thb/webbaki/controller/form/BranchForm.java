package de.thb.webbaki.controller.form;

import de.thb.webbaki.entity.User;
import de.thb.webbaki.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchForm {

    List<User> users;

    private String branche;
}
