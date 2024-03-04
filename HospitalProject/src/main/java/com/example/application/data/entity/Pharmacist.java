package com.example.application.data.entity;

import java.io.Serializable;

public class Pharmacist extends AbstractEntity implements Serializable {
    private String first_name;
    private String last_name;
    private String pharmacy;
    private long user_id;

    public Pharmacist() {
    }

    public Pharmacist(long id, String first_name, String last_name, String pharmacy, long userId) {
        setId(id);
        this.first_name = first_name;
        this.last_name = last_name;
        this.pharmacy = pharmacy;
        this.user_id = userId;
    }

    public Pharmacist(String first_name, String last_name, String pharmacy, long userId) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.pharmacy = pharmacy;
        this.user_id = userId;
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

    public String getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(String pharmacy) {
        this.pharmacy = pharmacy;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long userId) {
        this.user_id = userId;
    }
}
