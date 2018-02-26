package com.aquarloan.aquarloan.Interfaces;

/**
 * Created by Darren Gegantino on 2/17/2018.
 */

public class UserInformation {
    public String firstName;
    public String lastName;

    public UserInformation() {
    }

    public UserInformation(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
