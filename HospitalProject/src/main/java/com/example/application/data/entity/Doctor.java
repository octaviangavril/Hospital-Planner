package com.example.application.data.entity;

import java.io.Serializable;

public class Doctor extends AbstractEntity implements Serializable {
    private String first_name;
    private String last_name;
    private String speciality;
    private long user_id;

    public Doctor(){

    }

    public Doctor(long id, String first_name, String last_name, String speciality, long user_id) {
        setId(id);
        this.first_name = first_name;
        this.last_name = last_name;
        this.speciality = speciality;
        this.user_id = user_id;
    }

    public Doctor(String first_name, String last_name, String speciality, long user_id) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.speciality = speciality;
        this.user_id = user_id;
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
    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + getId() + '\'' +
                ", firstName='" + first_name + '\'' +
                ", lastName='" + last_name + '\'' +
                ", speciality='" + speciality + '\'' +
                "} " + super.toString();
    }
}
