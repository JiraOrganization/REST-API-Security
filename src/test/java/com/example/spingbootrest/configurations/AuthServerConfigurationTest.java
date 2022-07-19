package com.example.spingbootrest.configurations;

import com.example.spingbootrest.accounts.AccountService;
import com.example.spingbootrest.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AuthServerConfigurationTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test @DisplayName("인증 토큰을 받는 테스트")
    void getAuthToken() throws Exception {
        //Give
        String userName = appProperties.getAdminUsername();
        UserDetails userDetails = this.accountService.loadUserByUsername(userName);

        String clientId = appProperties.getClientId();
        String clientSecret = appProperties.getClientSecret();
        this.mockMvc.perform(post("/oauth2/token")
                .with(httpBasic(clientId, clientSecret))
                .param("grant_type", "client_credentials"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }


    @DisplayName("인증 토큰을 잘못된 클라이언트 ID/SECRET으로 받는 테스트")
    @ParameterizedTest()
    @MethodSource("paramsForTestClientInfo")
    void getAuthToken_Wrong_BaseAuth(String clientId, String clientSecret, int statusCode) throws Exception {
        //Give

        this.mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic(clientId, clientSecret))
                        .param("grant_type", "client_credentials"))
                .andDo(print())
                .andExpect(status().is(statusCode));
    }

    private static Object[] paramsForTestClientInfo(){
        return new Object[]{
                new Object[] {"myApp", "myAppSecret", 200},
                new Object[] {"wrongApp", "myAppSecret", 401},
                new Object[] {"myApp", "wrongAppSecret", 401},
        };
    }




}