package com.example.application.data.service;

import com.example.application.data.entity.Doctor;
import com.example.application.data.entity.Pharmacist;
import com.example.application.data.entity.Receptionist;
import com.example.application.data.repository.DoctorRepository;
import com.example.application.data.repository.PharmacistRepository;
import com.example.application.data.repository.ReceptionistRepository;
import com.example.application.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class GridService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PharmacistRepository pharmacistRepository;
    private final ReceptionistRepository receptionistRepository;

    public GridService(DoctorRepository doctorRepository, PharmacistRepository pharmacistRepository, ReceptionistRepository receptionistRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.receptionistRepository = receptionistRepository;
        this.userRepository = userRepository;
    }

    public List<Doctor> getAllDoctors() throws SQLException {
        return doctorRepository.findAll();
    }

    public void deleteDoctor(long id) {
        long user_id = 0;
        try {
            user_id = doctorRepository.findById(id).getUser_id();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        doctorRepository.deleteById(id);
        userRepository.deleteById(user_id);
    }

    public List<Receptionist> getAllReceptionists() throws SQLException {
        return receptionistRepository.findAll();
    }

    public void deleteReceptionist(long id) {
        long user_id = 0;
        try {
            user_id = receptionistRepository.findById(id).getUser_id();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        receptionistRepository.deleteById(id);
        userRepository.deleteById(user_id);
    }

    public List<Pharmacist> getAllPharmacists() throws SQLException {
        return pharmacistRepository.findAll();
    }

    public void deletePharmacist(long id) {
        long user_id = 0;
        try {
            user_id = pharmacistRepository.findById(id).getUser_id();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        pharmacistRepository.deleteById(id);
        userRepository.deleteById(user_id);
    }
}
