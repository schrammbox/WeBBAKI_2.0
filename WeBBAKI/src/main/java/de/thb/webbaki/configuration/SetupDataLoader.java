package de.thb.webbaki.configuration;

import de.thb.webbaki.entity.Privilege;
import de.thb.webbaki.entity.Role;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.repository.PrivilegeRepository;
import de.thb.webbaki.repository.RoleRepository;
import de.thb.webbaki.repository.UserRepository;
import de.thb.webbaki.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {

        if (alreadySetup) {
            return;
        }
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        final List<Privilege> adminPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));

        final Role adminRole = createRoleIfNotFound("ROLE_SUPERADMIN", adminPrivileges);

        createUserIfNotFound("Schramm", "Christian", "Telekommunikation", "UnterBranche Telekom",
        "Meta", "Passwort1234", Collections.singletonList(adminRole), "schrammbox@gmail.com", true, "schrammbox");

        createUserIfNotFound("Schönberg", "Leon", "Telekommunikation", "UnterBranche Telekom",
                "Meta", "Passwort1234", Collections.singletonList(adminRole), "schoenbe@th-brandenburg.de", true, "schoenbe");

        alreadySetup = true;

    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {

        Role role = roleService.getRoleByName(name);
        if (role == null) {
            role = new Role();
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }

    @Transactional
    User createUserIfNotFound(final String lastName, final String firstName, final String sector, final String branche,
                              final String company, final String password, final Collection<Role> roles, final String email,
                              final boolean enabled, final String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setLastName(lastName);
            user.setFirstName(firstName);
            user.setSector(sector);
            user.setBranche(branche);
            user.setCompany(company);
            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(enabled);
            user.setUsername(username);
            user.setEmail(email);
        }

        user = userRepository.save(user);

        return user;
    }
}