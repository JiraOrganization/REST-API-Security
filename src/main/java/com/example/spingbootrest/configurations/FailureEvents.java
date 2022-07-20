package com.example.spingbootrest.configurations;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;


@Component
public class FailureEvents {
    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent badCredentials) {
        if (badCredentials.getAuthentication() instanceof BearerTokenAuthenticationToken) {
            // ... handle
        }
            System.out.println("AuthenticationFailureBadCredentialsEvent: "+badCredentials.toString());


    }

}
