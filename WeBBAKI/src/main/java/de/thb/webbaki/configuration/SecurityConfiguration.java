package de.thb.webbaki.configuration;


import de.thb.webbaki.repository.RoleRepository;
import de.thb.webbaki.repository.UserRepository;
import de.thb.webbaki.security.MyUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@Configuration
@AllArgsConstructor
public class SecurityConfiguration {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    @Bean
    public MyUserDetailsService userDetailsService() {
        return new MyUserDetailsService(userRepository, roleRepository);
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = """
                SUPERADMIN > BUNDESADMIN\s
                 BUNDESADMIN > SEKTORENADMIN\s
                 SEKTORENADMIN > BRANCHENADMIN\s
                 BRANCHENADMIN > GESCHÄFTSSTELLE\s
                 GESCHÄFTSSTELLE > KRITIS_BETREIBER\s
                 KRITIS_BETREIBER > DEFAULT_USER""";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
    @Bean
    public DefaultWebSecurityExpressionHandler customSecurityExpressionHandler(){
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
