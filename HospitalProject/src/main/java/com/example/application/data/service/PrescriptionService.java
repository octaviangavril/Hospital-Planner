package com.example.application.data.service;

import com.example.application.data.entity.Prescription;
import com.example.application.data.repository.DoctorRepository;
import com.example.application.data.repository.PatientRepository;
import com.example.application.data.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Map;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PrescriptionService(DoctorRepository doctorRepository,PatientRepository patientRepository, PrescriptionRepository prescriptionRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public void createPrescription(String first_name, String last_name, String birthdate, long doctor_id, String prescription_date, String prescription_time, String[] symptoms, String[] intensities) throws SQLException {
        long patient_id = patientRepository.findByEverything(first_name, last_name, birthdate).getId();
        prescriptionRepository.save(new Prescription(doctor_id, patient_id, prescription_date, prescription_time));
        prescriptionRepository.saveMedicines(
                prescriptionRepository.findByEverything(doctor_id, patient_id, prescription_date, prescription_time),
                symptoms, intensities);
    }

    public String getMedicine(String first_name, String last_name, String birthdate, long doctor_id, String date, String time, String symptom) throws SQLException {
        return prescriptionRepository.getMedicine(
                prescriptionRepository.findByEverything(
                        doctor_id,
                        patientRepository.findByEverything(first_name, last_name, birthdate).getId(),
                        date, time), symptom);
    }

    public void finishPrescription(String first_name, String last_name, String birthdate, long doctor_id, String prescription_date, String prescription_time, Map<String,Double> dosages, Map<String,Integer> durations){
        prescriptionRepository.setDosageAndDuration(
                prescriptionRepository.findByEverything(
                        doctor_id,
                        patientRepository.findByEverything(
                                first_name,last_name,birthdate).getId(),
                        prescription_date,prescription_time),
                dosages,durations);
    }

    public String[] getMedicinePharmacy (String medicine, double dosage, int duration) throws SQLException {
        return prescriptionRepository.getMedicinePharmacy(medicine,dosage,duration);
    }

    public long getPrescriptionId(String first_name, String last_name, String birthdate, long doctor_id, String prescription_date, String prescription_time) {
        return prescriptionRepository.findByEverything(doctor_id,
                patientRepository.findByEverything(first_name,
                        last_name,birthdate).getId(),
                prescription_date,prescription_time).getId();
    }

    public String getPatientFirstName(long prescription_id) throws SQLException {
        return patientRepository.findById(
                prescriptionRepository.findById(prescription_id).getId()
        ).getFirst_name();
    }

    public String getPatientLastName(long prescription_id) throws SQLException {
        return patientRepository.findById(
                prescriptionRepository.findById(prescription_id).getId()
        ).getLast_name();
    }

    public String getPatientBirthdate(long prescription_id) throws SQLException {
        return patientRepository.findById(
                prescriptionRepository.findById(prescription_id).getId()
        ).getBirthdate();
    }

    public String getDoctorFirstName(long prescription_id) throws SQLException {
        return doctorRepository.findById(
                prescriptionRepository.findById(prescription_id).getId()
        ).getFirst_name();
    }

    public String getDoctorLastName(long prescription_id) throws SQLException {
        return doctorRepository.findById(
                prescriptionRepository.findById(prescription_id).getId()
        ).getLast_name();
    }

    public Map<String,String> getMedicines(long prescription_id) throws SQLException {
        return prescriptionRepository.getMedicines(prescription_id);
    }

    public void deletePrescription(long prescription_id) throws SQLException {
        prescriptionRepository.removeMedicines(prescription_id);
        prescriptionRepository.deleteById(prescription_id);
    }

    public void updatePrescriptionMeds(long prescription_id, String medicine) throws SQLException {
        prescriptionRepository.removeMedicine(prescription_id,medicine);
    }
}
