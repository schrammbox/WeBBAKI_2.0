package de.thb.webbaki.configuration;


import de.thb.webbaki.security.MyUserDetailsService;
import de.thb.webbaki.service.Exceptions.UserNotEnabledException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
//Used for @PreAuthorize in SuperAdminController.java
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private MyUserDetailsService userDetailsService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeRequests()
                .antMatchers("/css/**", "/webjars/**", "/bootstrap/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .antMatchers("/", "/home", "/register/**", "/success_register", "/confirmation/confirmByUser/**", "datenschutz").permitAll()
                .antMatchers("/admin").access("hasAuthority('ROLE_SUPERADMIN')")
                .antMatchers("/office").access("hasAuthority('ROLE_GESCHÄFTSSTELLE')")
                .antMatchers("/threatmatrix/**").access("hasAuthority('ROLE_KRITIS_BETREIBER')")
                .antMatchers("/report/company/**").access("hasAuthority('ROLE_KRITIS_BETREIBER')")
                .antMatchers("/report/branche/**").hasAnyAuthority("ROLE_KRITIS_BETREIBER", "ROLE_BRANCHENADMIN", "ROLE_SEKTORENADMIN", "ROLE_BUNDESADMIN")
                .antMatchers("/report/sector/**").hasAnyAuthority("ROLE_KRITIS_BETREIBER", "ROLE_SEKTORENADMIN", "ROLE_BUNDESADMIN")
                .antMatchers("/report/national/**").hasAnyAuthority("ROLE_KRITIS_BETREIBER", "ROLE_BUNDESADMIN")
                .antMatchers("/snap/**").access("hasAuthority('ROLE_SUPERADMIN')")
                .antMatchers("/scenarios").access("hasAuthority('ROLE_SUPERADMIN')")
                .antMatchers("/adjustHelp").access("hasAuthority('ROLE_SUPERADMIN')")
                .antMatchers("/help").hasAnyAuthority("ROLE_KRITIS_BETREIBER", "ROLE_BRANCHENADMIN", "ROLE_SEKTORENADMIN", "ROLE_BUNDESADMIN", "ROLE_SUPERADMIN", "ROLE_GESCHÄFTSSTELLE")
                .antMatchers("/confirmation/confirm/**").access("hasAuthority('ROLE_GESCHÄFTSSTELLE')")
                .and()
                .formLogin()
                .loginPage("/login")
                .failureHandler((request, response, exception) -> {
                    String redirectURL = "/login?";
                    if (exception.getCause().getCause() instanceof UserNotEnabledException){
                        redirectURL += "notEnabled";
                    }
                    else{
                        redirectURL += "error";
                    }
                    response.sendRedirect(redirectURL);
                })
                .usernameParameter("email")
                .usernameParameter("username")
                .permitAll()
                .defaultSuccessUrl("/account")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").permitAll();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
