package com.example.application.data.entity;

import java.io.Serializable;

public class Receptionist extends AbstractEntity implements Serializable {
    private String first_name;
    private String last_name;
    private long desk_id;
    private long user_id;
    public Receptionist() {

    }

    public Receptionist(long id, String first_name, String last_name, long desk_id, long user_id) {
        setId(id);
        this.first_name = first_name;
        this.last_name = last_name;
        this.desk_id = desk_id;
        this.user_id = user_id;
    }

    public Receptionist(String first_name, String last_name, long desk_id, long user_id) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.desk_id = desk_id;
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

    public long getDesk_id() {
        return desk_id;
    }

    public void setDesk_id(long desk_id) {
        this.desk_id = desk_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }
}
