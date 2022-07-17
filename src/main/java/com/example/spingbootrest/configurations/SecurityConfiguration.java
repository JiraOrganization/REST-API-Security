package com.example.spingbootrest.configurations;

import com.example.spingbootrest.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private AccountService accountService;


    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /*@Bean
    public UserDetailsService userDetailsService(){
        return accountService;
    }*/

    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    }*/



    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().mvcMatchers("/docs/index.html")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}

