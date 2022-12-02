package de.thb.webbaki.entity;

import de.thb.webbaki.entity.questionnaire.Questionnaire;
import de.thb.webbaki.entity.snapshot.Report;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String lastName;
    private String firstName;
    private String company;

    // authentication
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private boolean tokenExpired;
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<Questionnaire> questionnaire;

    @ManyToOne
    private Branch branch;

    @ManyToMany(fetch = FetchType.EAGER) //Fetching roles at the same time users get loaded
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public void removeRole(Role role) {
        roles.remove(role);
    }
    public void addRole(Role role) {
        roles.add(role);
    }

    //Roles Getter
    public Collection<Role> getRoles(){
        return roles;
    }
    public void setRoles(Collection<Role>roles){
        this.roles = roles;
    }

    @OneToMany(mappedBy = "user")
    private List<Report> reports;
}
