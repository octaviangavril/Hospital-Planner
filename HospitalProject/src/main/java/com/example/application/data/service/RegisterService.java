package com.example.application.data.service;

import com.example.application.data.entity.*;
import com.example.application.data.repository.DoctorRepository;
import com.example.application.data.repository.PharmacistRepository;
import com.example.application.data.repository.ReceptionistRepository;
import com.example.application.data.repository.UserRepository;
import com.example.application.data.utils.Role;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PharmacistRepository pharmacistRepository;
    private final ReceptionistRepository receptionistRepository;

    public RegisterService(UserRepository userRepository, DoctorRepository doctorRepository, PharmacistRepository pharmacistRepository, ReceptionistRepository receptionistRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.pharmacistRepository = pharmacistRepository;
        this.receptionistRepository = receptionistRepository;
    }

    public void registerDoctor(String first_name,
                               String last_name,
                               String speciality,
                               String email,
                               String password) throws Exception{
        try {
            User user = new User(email, password, Role.doctor);
            userRepository.save(user);
            user = userRepository.findByEmail(email);
            Doctor doctor = new Doctor(first_name, last_name, speciality, user.getId());
            doctorRepository.save(doctor);
        } catch (SQLException e) {
            if(e.getMessage().contains("Doctor already exists")){
                userRepository.deleteById(userRepository.findByEmail(email).getId());
            }
            throw new Exception(e);
        }
    }
    public void registerPharmacist(String first_name,
                               String last_name,
                               String pharmacy,
                               String email,
                               String password) throws Exception{
        try {
            User user = new User(email, password, Role.pharmacist);
            userRepository.save(user);
            user = userRepository.findByEmail(email);
            Pharmacist pharmacist = new Pharmacist(first_name, last_name, pharmacy, user.getId());
            pharmacistRepository.save(pharmacist);
        } catch (SQLException e) {
            if(e.getMessage().contains("Pharmacist already exists")){
                userRepository.deleteById(userRepository.findByEmail(email).getId());
            }
            throw new Exception(e);
        }
    }

    public void registerReceptionist(String first_name,
                                   String last_name,
                                   Long deskId,
                                   String email,
                                   String password) throws Exception{
        try {
            User user = new User(email, password, Role.receptionist);
            userRepository.save(user);
            user = userRepository.findByEmail(email);
            Receptionist receptionist = new Receptionist(first_name, last_name, deskId, user.getId());
            receptionistRepository.save(receptionist);
        } catch (SQLException e) {
            if(e.getMessage().contains("Receptionist already exists")){
                userRepository.deleteById(userRepository.findByEmail(email).getId());
            }
            throw new Exception(e);
        }
    }

    public String getReceptionistFullName(long user_id) {
        try {
            return receptionistRepository.findByUserId(user_id).getFirst_name() +
                    " " + receptionistRepository.findByUserId(user_id).getLast_name();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
