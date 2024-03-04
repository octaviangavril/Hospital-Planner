package com.example.application.data.service;

import com.example.application.data.entity.Doctor;
import com.example.application.data.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }
    public String getDoctorSpeciality(long id)  {
        try {
            return doctorRepository.findById(id).getSpeciality();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String getDoctorFullName(long id)  {
        try {
            return doctorRepository.findById(id).getFirst_name() +
                    " " + doctorRepository.findById(id).getLast_name();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Doctor getDoctorByUserId(long id)  {
        try {
            return doctorRepository.findByUserId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
