package ru.solon4ak.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import ru.solon4ak.service.UserDetailServiceImpl;

import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
@ComponentScan({"ru.solon4ak"})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private Logger log = Logger.getLogger(SecurityConfiguration.class.getName());

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new UrlAuthenticationSuccessHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailServiceImpl();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        try {
            auth
                    .userDetailsService(userDetailsService())
                    .passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            log.log(Level.WARNING, "Authentication exception.", e);
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").access("hasRole('ADMIN') or hasRole('USER')")
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .successHandler(myAuthenticationSuccessHandler())

                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)

                .and()
                .csrf().disable();
    }
}
