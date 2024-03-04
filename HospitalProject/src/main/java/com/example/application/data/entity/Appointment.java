package com.example.application.data.entity;

import java.io.Serializable;

public class Appointment extends AbstractEntity implements Serializable {
    private long doctor_id;
    private long patient_id;
    private String appointment_date;
    private String appointment_time;

    public Appointment() {
    }

    public Appointment(long id, long doctor_id, long patient_id, String appointment_date, String appointment_time) {
        setId(id);
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.appointment_date = appointment_date;
        this.appointment_time = appointment_time;
    }

    public Appointment(long doctor_id, long patient_id, String appointment_date, String appointment_time) {
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.appointment_date = appointment_date;
        this.appointment_time = appointment_time;
    }

    public long getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(long doctor_id) {
        this.doctor_id = doctor_id;
    }

    public long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(long patient_id) {
        this.patient_id = patient_id;
    }

    public String getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(String appointment_date) {
        this.appointment_date = appointment_date;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public void setAppointment_time(String appointment_time) {
        this.appointment_time = appointment_time;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "doctor_id=" + doctor_id +
                ", patient_id=" + patient_id +
                ", appointment_date='" + appointment_date + '\'' +
                ", appointment_time='" + appointment_time + '\'' +
                "} " + super.toString();
    }
}

