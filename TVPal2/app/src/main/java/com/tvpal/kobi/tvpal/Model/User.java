package com.tvpal.kobi.tvpal.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tvpal.kobi.tvpal.MyApplication;
/**
 * Created by Kobi on 11/05/2016.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    String email;
    String firstName;
    String lastName;
    String birthDate;
    String password;
    String profilePic;
    String lastUpdateDate;



    public User(String email, String firstName,String lastName, String birthDate, String password , String profilePic,String lastUpdated) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.birthDate = birthDate;
        this.profilePic = profilePic;
        this.lastUpdateDate = lastUpdated;
    }

    public User() {}



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

    @JsonIgnore
    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @JsonIgnore
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


    @JsonIgnore
    public Map<String,Object> getUserMap(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("email",this.email);
        map.put("firstName",this.firstName);
        map.put("lastName",this.lastName);
        map.put("birthDate",this.birthDate);
        map.put("password",this.password);
        map.put("profilePic",this.profilePic);
        map.put("lastUpdateDate",this.lastUpdateDate);
        return map;
    }

    @JsonIgnore
    public String getProfilePic() {
        return profilePic;
    }

    @JsonIgnore
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
    @JsonIgnore
    public String displayName() {return this.firstName+" "+this.lastName;}
}
