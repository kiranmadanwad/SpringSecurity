package com.kiran.madanwad.springsecurityclient.model;

import lombok.Data;

@Data
public class PasswordModel {
    private String email;
    private String oldPassowrd;
    private String newPassword;
}
