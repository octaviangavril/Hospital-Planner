package com.example.application.data.service;

import com.example.application.data.entity.Pharmacist;
import com.example.application.data.repository.PharmacistRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Map;

@Service
public class MedicationStockService {
    private final PharmacistRepository pharmacistRepository;
    public MedicationStockService(PharmacistRepository pharmacistRepository) {
        this.pharmacistRepository = pharmacistRepository;
    }

    public Pharmacist getPharmacistByUserId(long id) throws SQLException {
        return pharmacistRepository.findByUserId(id);
    }
    public boolean checkStockMedicine(String medicine, long user_Id, String med_description) throws SQLException {
        double dosage = Double.parseDouble(med_description.split(" ")[0]);
        int duration = Integer.parseInt(med_description.split(" ")
        [med_description.split(" ").length-2]);
        return pharmacistRepository.checkStock(medicine,
                getPharmacistByUserId(user_Id).getPharmacy(),
                dosage,duration);
    }

    public void updateStock(String medicine, long user_Id, String med_description) throws SQLException {
        double dosage = Double.parseDouble(med_description.split(" ")[0]);
        int duration = Integer.parseInt(med_description.split(" ")
                [med_description.split(" ").length-2]);
        pharmacistRepository.updateStock(medicine,
                getPharmacistByUserId(user_Id).getPharmacy(),
                dosage,duration);
    }

    public Map<String,String> getMedicines(long id) throws SQLException {
        return pharmacistRepository.getMedicines(
          getPharmacistByUserId(id).getPharmacy()
        );
    }

    public void refillMedicine(long user_id,String medicine) throws SQLException {
        pharmacistRepository.refillMedicine(
                getPharmacistByUserId(user_id).getPharmacy(),
                medicine);
    }

    public String getPharmacistFullName(long user_id) {
        try {
            return pharmacistRepository.findByUserId(user_id).getFirst_name() +
                    " " + pharmacistRepository.findByUserId(user_id).getLast_name();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
