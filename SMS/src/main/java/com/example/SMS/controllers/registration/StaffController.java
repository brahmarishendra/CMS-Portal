package com.example.SMS.controllers.registration;

import com.example.SMS.pojo.Staff;
import com.example.SMS.interfaces.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Staff ("stuff") operations.
 */
@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping
    public List<Staff> getAllStaff() {
        return staffService.getAllStaff();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Staff> staffOpt = staffService.loginStaff(loginRequest.getEmail(), loginRequest.getPassword());
        
        if (staffOpt.isPresent()) {
            List<Staff> allStaff = staffService.getAllStaff();
            return ResponseEntity.ok(allStaff);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Staff staff) {
        try {
            Staff registered = staffService.registerStaff(staff);
            return ResponseEntity.status(HttpStatus.CREATED).body(registered);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
}
