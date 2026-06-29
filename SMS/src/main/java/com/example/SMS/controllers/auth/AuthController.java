package com.example.SMS.controllers.auth;

import com.example.SMS.pojo.Student;
import com.example.SMS.pojo.Staff;
import com.example.SMS.interfaces.StudentService;
import com.example.SMS.interfaces.StaffService;
import com.example.SMS.datasource.entitycustomclasses.customClasses.AuthRequest;
import com.example.SMS.datasource.entitycustomclasses.customClasses.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Unified AuthController for handling generic Login and Signup endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StaffService staffService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        String role = request.getRole() != null ? request.getRole().toUpperCase() : "";

        if (role.equals("STAFF") || role.equals("STUFF")) {
            Optional<Staff> staffOpt = staffService.loginStaff(request.getEmail(), request.getPassword());
            if (staffOpt.isPresent()) {
                return ResponseEntity.ok(staffOpt.get());
            }
        } 
        else if (role.equals("STUDENT")) {
            Optional<Student> studentOpt = studentService.loginStudent(request.getEmail(), request.getPassword());
            if (studentOpt.isPresent()) {
                return ResponseEntity.ok(studentOpt.get());
            }
        } 
        else {
            return ResponseEntity.badRequest().body("Invalid role specified. Must be 'STUDENT' or 'STAFF'/'STUFF'");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        String role = request.getRole() != null ? request.getRole().toUpperCase() : "";

        try {
            if (role.equals("STAFF") || role.equals("STUFF")) {
                Staff staff = new Staff();
                staff.setFirstName(request.getFirstName());
                staff.setLastName(request.getLastName());
                staff.setEmail(request.getEmail());
                staff.setPhoneNumber(request.getPhoneNumber());
                staff.setPassword(request.getPassword());
                staff.setAddress(request.getAddress());
                staff.setAlternativePhoneNumber(request.getAlternativePhoneNumber());
                staff.setCreatedBy(request.getCreatedBy() != null ? request.getCreatedBy() : "System");
                staff.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "System");
                staff.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());
                staff.setUpdatedAt(request.getUpdatedAt() != null ? request.getUpdatedAt() : LocalDateTime.now());

                Staff registeredStaff = staffService.registerStaff(staff);
                return ResponseEntity.status(HttpStatus.CREATED).body(registeredStaff);
            } 
            else if (role.equals("STUDENT")) {
                Student student = new Student();
                student.setFirstName(request.getFirstName());
                student.setLastName(request.getLastName());
                student.setEmail(request.getEmail());
                student.setPassword(request.getPassword());
                student.setClasName(request.getClasName());
                student.setSection(request.getSection());
                student.setGender(request.getGender());
                student.setCreatedBy(request.getCreatedBy() != null ? request.getCreatedBy() : "System");
                student.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "System");
                student.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());
                student.setUpdatedAt(request.getUpdatedAt() != null ? request.getUpdatedAt() : LocalDateTime.now());

                Student registeredStudent = studentService.registerStudent(student);
                return ResponseEntity.status(HttpStatus.CREATED).body(registeredStudent);
            } 
            else {
                return ResponseEntity.badRequest().body("Invalid role specified. Must be 'STUDENT' or 'STAFF'/'STUFF'");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
