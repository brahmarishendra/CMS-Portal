package com.example.SMS.controllers.etc;

import com.example.SMS.datasource.entitymodels.models.Fees;
import com.example.SMS.datasource.repository.jpa.FeesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fees")
public class FeesController {

    @Autowired
    private FeesRepository feesRepository;

    @PostMapping("/add")
    public Fees addFee(@RequestBody Fees fees) {
        return feesRepository.save(fees);
    }

    @GetMapping("/get")
    public List<Fees> getAllFees() {
        return feesRepository.findAll();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Fees> getFeeById(@PathVariable int id) {
        Optional<Fees> feeOpt = feesRepository.findById(id);
        if (feeOpt.isPresent()) {
            return ResponseEntity.ok(feeOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/student/{studentId}")
    public List<Fees> getFeesByStudentId(@PathVariable long studentId) {
        return feesRepository.findAll().stream()
                .filter(fee -> fee.getStudentId() == studentId)
                .toList();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Fees> updateFee(@PathVariable int id, @RequestBody Fees feesDetails) {
        Optional<Fees> feeOpt = feesRepository.findById(id);
        if (feeOpt.isPresent()) {
            Fees fee = feeOpt.get();
            fee.setStudentId(feesDetails.getStudentId());
            fee.setFeeType(feesDetails.getFeeType());
            fee.setTermName(feesDetails.getTermName());
            fee.setTotalAmount(feesDetails.getTotalAmount());
            fee.setPaidAmount(feesDetails.getPaidAmount());
            fee.setDueDate(feesDetails.getDueDate());
            fee.setPaymentStatus(feesDetails.getPaymentStatus());
            fee.setLastPaymentDate(feesDetails.getLastPaymentDate());
            return ResponseEntity.ok(feesRepository.save(fee));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFee(@PathVariable int id) {
        try {
            feesRepository.deleteById(id);
            return ResponseEntity.ok("Fee record deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
