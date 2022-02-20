package com.kiran.madanwad.springsecurityclient.event.listener;

import com.kiran.madanwad.springsecurityclient.entity.User;
import com.kiran.madanwad.springsecurityclient.event.RegistartionCompleteEvent;
import com.kiran.madanwad.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistartionCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistartionCompleteEvent event) {
        //Create the verification token for user with Link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);
        //Send the email to user
        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;
        //sendVerificationEmail
        log.info("click the link to verify your account == {} ", url);
    }
}
