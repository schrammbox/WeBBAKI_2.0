package de.thb.webbaki.configuration;


import de.thb.webbaki.security.MyUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


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
                .antMatchers("/", "/home", "/register/**", "/success_register", "/confirmation/**").permitAll()
                .antMatchers("/admin").access("hasAuthority('ROLE_SUPERADMIN')")
                .antMatchers("/office").access("hasAuthority('ROLE_GESCHÄFTSSTELLE')")
                .antMatchers("/threatmatrix/**").access("hasAuthority('ROLE_KRITIS_BETREIBER')")
                .antMatchers("/report/**").access("hasAuthority('ROLE_KRITIS_BETREIBER')")
                .antMatchers("/snap").access("hasAuthority('ROLE_SUPERADMIN')")
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .usernameParameter("username")
                .permitAll()
                .defaultSuccessUrl("/account")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").permitAll();
        http.csrf().disable(); //TODO: just for developement
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
