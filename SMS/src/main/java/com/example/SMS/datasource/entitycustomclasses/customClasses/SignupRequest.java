package com.example.SMS.datasource.entitycustomclasses.customClasses;

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
}
