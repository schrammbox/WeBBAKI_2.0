package de.thb.webbaki.controller.form;

import de.thb.webbaki.entity.Role;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThreatMatrixFormModel {

    // get ENUM for Frontend
    @NotNull(message = "Probability null")
    private Probability probability;
    // get ENUM for Frontend
    @NotNull(message = "Impact null")
    private Impact impact;
    // get ENUM for Frontend
    @NotNull(message = "Risk null")
    private Risk risk;

    private Collection<Role> roles;

    private String comment;
    private String[] smallComments;

    // get probability from Frontend as array
    @NotNull(message = "prob not null")
    private String[] prob;
    // get impact from Frontend as array
    @NotNull(message = "imp not null")
    private String[] imp;

    private User user;

}
