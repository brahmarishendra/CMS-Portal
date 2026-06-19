package com.example.SMS.controllers.etc;

import com.example.SMS.datasource.entitymodels.models.StaffSalary;
import com.example.SMS.datasource.repository.jpa.StaffSalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

// Controller Class
// Location: src/main/java/com/example/SMS/controllers/etc/StaffSalaryController.java
@RestController
@RequestMapping("/api/salary")
@CrossOrigin
public class StaffSalaryController {

    @Autowired
    private StaffSalaryRepository salaryRepository;

    @GetMapping("/get")
    public List<StaffSalary> getAllSalaries() {
        return salaryRepository.findAll();
    }

    @GetMapping("/get/{phoneNo}")
    public ResponseEntity<StaffSalary> getSalaryByPhoneNo(@PathVariable String phoneNo) {
        Optional<StaffSalary> salaryOpt = salaryRepository.findById(phoneNo);
        if (salaryOpt.isPresent()) {
            return ResponseEntity.ok(salaryOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/add")
    public StaffSalary addSalary(@RequestBody StaffSalary salary) {
        return salaryRepository.save(salary);
    }

    @PutMapping("/update/{phoneNo}")
    public ResponseEntity<StaffSalary> updateSalary(@PathVariable String phoneNo, @RequestBody StaffSalary salaryDetails) {
        Optional<StaffSalary> salaryOpt = salaryRepository.findById(phoneNo);
        if (salaryOpt.isPresent()) {
            StaffSalary salary = salaryOpt.get();
            salary.setStaffName(salaryDetails.getStaffName());
            salary.setSalaryAmount(salaryDetails.getSalaryAmount());
            salary.setTaxes(salaryDetails.getTaxes());
            salary.setTotal(salaryDetails.getTotal());
            return ResponseEntity.ok(salaryRepository.save(salary));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{phoneNo}")
    public ResponseEntity<String> deleteSalary(@PathVariable String phoneNo) {
        try {
            salaryRepository.deleteById(phoneNo);
            return ResponseEntity.ok("Salary record deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
