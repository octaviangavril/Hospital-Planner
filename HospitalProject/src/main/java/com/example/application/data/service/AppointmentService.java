package com.example.application.data.service;

import com.example.application.data.entity.Appointment;
import com.example.application.data.entity.Doctor;
import com.example.application.data.entity.Patient;
import com.example.application.data.repository.*;
import com.vaadin.open.App;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class AppointmentService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(PatientRepository patientRepository, AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }
    public List<Appointment> getAvailableAppointments(String[] symptoms,String birthdate) throws SQLException {
        return appointmentRepository.getAvailableAppointments(symptoms,birthdate);
    }

    public void saveAppointment(Appointment appointment, String first_name, String last_name, String birthdate, String[] symptoms) throws SQLException {
        patientRepository.save(new Patient(first_name,last_name,birthdate));
        appointmentRepository.save(new Appointment(appointment.getDoctor_id(),
                patientRepository.findByEverything(first_name,last_name,birthdate).getId(),
                appointment.getAppointment_date(),
                appointment.getAppointment_time()));
        appointmentRepository.saveSymptoms(appointmentRepository
                .findByEverything(appointment.getDoctor_id(),
                        appointment.getAppointment_date(),
                        appointment.getAppointment_time()),symptoms);
    }

    public List<Appointment> getAppointmentsByDoctor(long id) throws SQLException {
        return appointmentRepository.getAppointmentsByDoctorId(id);
    }

    public String getPatientFullName(Appointment appointment)  {
        try {
            return patientRepository.findById(appointment.getPatient_id()).getFirst_name() +
                    " " + patientRepository.findById(appointment.getPatient_id()).getLast_name();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPatientBirthdate(Appointment appointment) {
        try {
            return patientRepository.findById(appointment.getPatient_id()).getBirthdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAppointment(Appointment appointment) {
        appointmentRepository.deleteSymptoms(appointment.getId());
        appointmentRepository.deleteById(appointment.getId());
    }

    public String[] getSymptoms(Appointment appointment) throws SQLException {
        return appointmentRepository.getSymptoms(appointment);
    }
}
