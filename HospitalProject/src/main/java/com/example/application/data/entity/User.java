package com.example.application.data.entity;

import com.example.application.data.utils.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;

public class User extends AbstractEntity implements Serializable {
    private String email;
    private String password_salt;
    private String password_hash;
    private Role role;
    public User(){

    }

    public User(long id, String email, String password, Role role) {
        setId(id);
        this.email = email;
        this.role = role;
        this.password_salt = RandomStringUtils.random(32);
        this.password_hash = DigestUtils.sha1Hex(password + password_salt);
    }

    public User(String email, String password, Role role) {
        this.email = email;
        this.role = role;
        this.password_salt = RandomStringUtils.random(32);
        this.password_hash = DigestUtils.sha1Hex(password + password_salt);
    }

    public boolean checkPassword(String password) {
        return DigestUtils.sha1Hex(password + password_salt).equals(password_hash);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_salt() {
        return password_salt;
    }

    public void setPassword_salt(String password_salt) {
        this.password_salt = password_salt;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String passwordHash) {
        this.password_hash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password_salt='" + password_salt + '\'' +
                ", password_hash='" + password_hash + '\'' +
                ", role=" + role +
                "} " + super.toString();
    }
}
