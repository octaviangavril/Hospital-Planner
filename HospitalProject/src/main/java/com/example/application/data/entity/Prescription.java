package com.example.application.data.entity;

import java.io.Serializable;

public class Prescription extends AbstractEntity implements Serializable {
    private long doctor_id;
    private long patient_id;
    private String prescription_date;
    private String prescription_time;

    public Prescription(){

    }

    public Prescription(long id,long doctor_id, long patient_id, String prescription_date, String prescription_time) {
        setId(id);
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.prescription_date = prescription_date;
        this.prescription_time = prescription_time;
    }

    public Prescription(long doctor_id, long patient_id, String prescription_date, String prescription_time) {
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.prescription_date = prescription_date;
        this.prescription_time = prescription_time;
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

    public String getPrescription_date() {
        return prescription_date;
    }

    public void setPrescription_date(String prescription_date) {
        this.prescription_date = prescription_date;
    }

    public String getPrescription_time() {
        return prescription_time;
    }

    public void setPrescription_time(String prescription_time) {
        this.prescription_time = prescription_time;
    }
}
