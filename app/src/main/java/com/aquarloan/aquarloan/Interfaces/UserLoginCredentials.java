package com.aquarloan.aquarloan.Interfaces;

/**
 * Created by Darren Gegantino on 2/26/2018.
 */

public class UserLoginCredentials {
    public String password;
    public String phoneNumber;

    public UserLoginCredentials() {
    }

    public UserLoginCredentials(String password, String phoneNumber) {
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
