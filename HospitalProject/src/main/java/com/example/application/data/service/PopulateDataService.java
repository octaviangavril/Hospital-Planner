package com.example.application.data.service;

import com.example.application.data.entity.Doctor;
import com.example.application.data.entity.Pharmacist;
import com.example.application.data.entity.Receptionist;
import com.example.application.data.entity.User;
import com.example.application.data.utils.Role;
import com.github.javafaker.Faker;

import com.example.application.data.repository.DoctorRepository;
import com.example.application.data.repository.PharmacistRepository;
import com.example.application.data.repository.ReceptionistRepository;
import com.example.application.data.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Random;

@Service
public class PopulateDataService {

    public PopulateDataService(UserRepository userRepository, DoctorRepository doctorRepository, PharmacistRepository pharmacistRepository, ReceptionistRepository receptionistRepository) {
        Faker faker = new Faker(new Locale("ro"));
        String[] medical_specialities = {"Medicină Generală", "Pediatrie", "Chirurgie Generală", "Medicină Internă", "Obstetrică și Ginecologie", "Cardiologie", "Neurologie", "Oftalmologie"};
        Long[] desk_ids = {1L,2L,3L,4L};
        String[] pharmacies = {"Catena", "Dr.Max", "Ropharma"};

        try {
            userRepository.save(new User("doctor","doctor",Role.doctor));
            doctorRepository.save(new Doctor(faker.name().firstName(),
                    faker.name().lastName(),
                    medical_specialities[0],
                    userRepository.findByEmail("doctor").getId()));
            for (int i = 0; i < 8; i++) {
                String email = faker.internet().emailAddress();
                userRepository.save(new User(email,
                        faker.internet().password(),
                        Role.doctor));
                doctorRepository.save(new Doctor(faker.name().firstName(),
                        faker.name().lastName(),
                        medical_specialities[i],
                        userRepository.findByEmail(email).getId()));
            }
            userRepository.save(new User("recep","recep",Role.receptionist));
            receptionistRepository.save(new Receptionist(faker.name().firstName(),
                    faker.name().lastName(),
                    desk_ids[0],
                    userRepository.findByEmail("recep").getId()));
            for (int i = 0; i < 5; i++) {
                Random random = new Random();
                int index = random.nextInt(desk_ids.length);
                String email = faker.internet().emailAddress();
                userRepository.save(new User(email,
                        faker.internet().password(),
                        Role.receptionist));
                receptionistRepository.save(new Receptionist(faker.name().firstName(),
                        faker.name().lastName(),
                        desk_ids[index],
                        userRepository.findByEmail(email).getId()));
            }
            userRepository.save(new User("pharma","pharma",Role.pharmacist));
            pharmacistRepository.save(new Pharmacist(faker.name().firstName(),
                    faker.name().lastName(),
                    pharmacies[0],
                    userRepository.findByEmail("pharma").getId()));
            userRepository.save(new User("pharma1","pharma1",Role.pharmacist));
            pharmacistRepository.save(new Pharmacist(faker.name().firstName(),
                    faker.name().lastName(),
                    pharmacies[1],
                    userRepository.findByEmail("pharma1").getId()));
            for (int i = 0; i < 9; i++) {
                Random random = new Random();
                int index = random.nextInt(pharmacies.length);
                String email = faker.internet().emailAddress();
                userRepository.save(new User(email,
                        faker.internet().password(),
                        Role.pharmacist));
                pharmacistRepository.save(new Pharmacist(faker.name().firstName(),
                        faker.name().lastName(),
                        pharmacies[index],
                        userRepository.findByEmail(email).getId()));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
