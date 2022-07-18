package com.example.spingbootrest.accounts;

import com.example.spingbootrest.common.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountServiceTest extends BaseControllerTest {
    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;


    @Test
    void fildByUsername(){
        //Given
        String userName = appProperties.getAdminUsername();
        String password = appProperties.getUserPassword();

        /*Account account =  Account.builder()
                .email(userName)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        //this.accountRepository.save(account);
        Account saveAccount = this.accountService.loadUserByUsername(account);*/

        //When
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        //Then
        assertThat(userDetails.getUsername()).isEqualTo(userName);
        assertThat(passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
    }

    @Test
    void findByUsernameFail(){
        //Given & When
        String username = "unUserName_asdfasdf";
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername(username));

        //Then
        assertEquals(username, exception.getMessage());
    }

}