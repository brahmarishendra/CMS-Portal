package com.example.SMS.controllers.etc;

import com.example.SMS.datasource.entitymodels.models.Results;
import com.example.SMS.datasource.repository.jpa.ResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/results")
public class ResultsController {
    
    @Autowired
    private ResultsRepository resultsRepository;

    @PostMapping("/add")
    public Results addResults(@RequestBody Results results) {
        return resultsRepository.save(results);
    }
    
    @GetMapping("/get")
    public List<Results> getResults() {
        return resultsRepository.findAll();
    }

    @GetMapping("/student/{studentId}")
    public List<Results> getResultsByStudent(@PathVariable long studentId) {
        return resultsRepository.findByStudentId(studentId);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Results> getResults(@PathVariable int id) {
        Optional<Results> resultsOpt = resultsRepository.findById(id);
        if (resultsOpt.isPresent()) {
            return ResponseEntity.ok(resultsOpt.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/Results")
    public Results updateResults(@RequestBody Results results) {
        return resultsRepository.save(results);
    }

    @DeleteMapping("/Results")
    public ResponseEntity<String> deleteResults(@RequestBody Results results) {
        try {
            resultsRepository.delete(results);
            return ResponseEntity.ok("Result deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
