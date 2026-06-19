package com.example.SMS.controllers.registration;

import com.example.SMS.pojo.Student;
import com.example.SMS.interfaces.StudentService;
import com.example.SMS.datasource.repository.jpa.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Student operations.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        try {
            Student savedStudent = studentService.registerStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@RequestBody Student student) {
        try {
            Student registered = studentService.registerStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(registered);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginStudent(@RequestBody LoginRequest loginRequest) {
        Optional<Student> studentOpt = studentService.loginStudent(loginRequest.getEmail(), loginRequest.getPassword());
        
        if (studentOpt.isPresent()) {
            return ResponseEntity.ok(studentOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @GetMapping("/random-id")
    public long getRandomId() {
        java.time.LocalDate today = java.time.LocalDate.now();
        String monthStr = String.valueOf(today.getMonthValue());
        String dayStr = String.format("%02d", today.getDayOfMonth());
        String yearStr = String.valueOf(today.getYear());
        long baseDate = Long.parseLong(monthStr + dayStr + yearStr);
        
        int sequence = 1000;
        long id = baseDate * 10000 + sequence;
        
        while (studentRepository.existsById(id)) {
            sequence++;
            id = baseDate * 10000 + sequence;
        }
        return id;
    }
}
