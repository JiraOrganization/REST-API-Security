package com.example.spingbootrest.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private PasswordEncoder passwordEncoder;
   /* @Autowired
    private AppProperties appProperties;

    @Autowired
    private AppBasicAuthenticationEntryPoint authenticationEntryPoint;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/public").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().mvcMatchers("/docs/index.html")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }*/



///////////////https://www.appsdeveloperblog.com/spring-authorization-server-tutorial/
    @Bean
    SecurityFilterChain configureSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults());

        return http.build();

    }

/*    @Bean
    public UserDetailsService users() {

        //PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        UserDetails user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("1234"))
                //.password("password")
                .roles("Admin")
                .build();

        return new InMemoryUserDetailsManager(user);

    }*/

}

