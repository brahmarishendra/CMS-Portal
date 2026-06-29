package com.example.SMS.datasource.entitycustomclasses.customClasses;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Data Transfer Object (DTO) for unified signup request body.
 * Combines all possible fields for both Student and Staff.
 */
public class SignupRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber; // Used primarily by Staff
    private String password;
    private String role; // Expecting "STUDENT", "STAFF", or "STUFF"
    private String clasName;
    private String section;
    private String gender;
    private String address;
    private String alternativePhoneNumber;
    private String createdBy;
    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getClasName() { return clasName; }
    public void setClasName(String clasName) { this.clasName = clasName; }
    
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAlternativePhoneNumber() { return alternativePhoneNumber; }
    public void setAlternativePhoneNumber(String alternativePhoneNumber) { this.alternativePhoneNumber = alternativePhoneNumber; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
