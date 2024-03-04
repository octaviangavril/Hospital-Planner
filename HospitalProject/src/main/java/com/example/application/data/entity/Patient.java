package com.example.application.data.entity;

import java.io.Serializable;

public class Patient extends AbstractEntity implements Serializable {
    private String first_name;
    private String last_name;
    private String birthdate;

    public Patient() {
    }

    public Patient(long id, String first_name, String last_name, String birthdate) {
        setId(id);
        this.first_name = first_name;
        this.last_name = last_name;
        this.birthdate = birthdate;
    }

    public Patient(String first_name, String last_name, String birthdate) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.birthdate = birthdate;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
