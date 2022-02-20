package com.kiran.madanwad.springsecurityclient.controller;

import com.kiran.madanwad.springsecurityclient.entity.User;
import com.kiran.madanwad.springsecurityclient.entity.VerificationToken;
import com.kiran.madanwad.springsecurityclient.event.RegistartionCompleteEvent;
import com.kiran.madanwad.springsecurityclient.model.PasswordModel;
import com.kiran.madanwad.springsecurityclient.model.UserModel;
import com.kiran.madanwad.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest httpServletRequest) {
        User user = userService.registerUser(userModel);
        applicationEventPublisher.publishEvent(new RegistartionCompleteEvent(user, applicationUrl(httpServletRequest)));
        return "Success!";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "user Verified successfully!";
        } else {
            return "user Verification failed!";
        }

    }

    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerificaitonTokenEmail(user, applicationUrl(request), verificationToken);
        return "Verification Link sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if (user != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user, applicationUrl(request), token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid token";
        }
        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password Reset successful";
        } else {
            return "Invalid Token";
        }

    }

    @PostMapping("/changePassword")
    public String changePassword(
            @RequestBody PasswordModel passwordModel) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        if (user != null) {
            if (!userService.checkIfValidOldPassword(user, passwordModel.getOldPassowrd())) {
                return "Invalid old password";
            }
            /* Save new password */
            userService.changePassword(user, passwordModel.getNewPassword());
            return "Password change successfully";
        }
        return "Invalid email";

    }


    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl + "/savePassword?token=" + token;
        //sendPasswordResetEmail
        log.info("click the link to reset your password == {} ", url);
        return url;
    }

    private void resendVerificaitonTokenEmail(User user, String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl + "/verifyRegistration?token=" + verificationToken.getToken();
        //sendVerificationEmail
        log.info("click the link to verify your account == {} ", url);
    }

    public String applicationUrl(HttpServletRequest httpServletRequest) {
        return "http://" +
                httpServletRequest.getServerName() +
                ":" +
                httpServletRequest.getServerPort() +
                httpServletRequest.getContextPath();
    }
}
