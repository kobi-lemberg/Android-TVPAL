package com.tvpal.kobi.tvpal.Model;

import com.tvpal.kobi.tvpal.MyApplication;

/**
 * Created by Kobi on 11/05/2016.
 */
public class User {
    String email;
    String firstName;
    String lastName;
    String birthDate;
    String password;
    String profilePic;
    String lastUpdateDate;

    public User(String email, String password,String firstName, String lastName,  String birthDate, String profilePic,String lastUpdated) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.birthDate = birthDate;
        this.profilePic = profilePic;
        this.lastUpdateDate = lastUpdated;
    }

    public User() {
        this.email = "test@test.co.il";
        this.firstName = "First Name";
        this.lastName = "Last Name";
        this.password = "Password";
        this.birthDate = "Birth date";
        this.profilePic="";
        lastUpdateDate = MyApplication.getCurrentDate();
    }

    public User(User other) {
        this.email = other.email;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.password = other.password;
        this.birthDate = other.birthDate;
        this.profilePic=other.profilePic;
        lastUpdateDate = other.lastUpdateDate;
    }


    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {

        return "User{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", password='" + password + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", lastUpdateDate='" + lastUpdateDate + '\'' +
                '}';
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getDisplayName()
    {
        return this.firstName+" "+this.lastName;
    }
}
