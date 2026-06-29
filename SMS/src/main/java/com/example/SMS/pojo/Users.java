package com.example.SMS.pojo;

// POJO Entity Class
// Location: src/main/java/com/example/SMS/pojo/Users.java
import jakarta.persistence.*;

/**
 * Users Entity mapping to the 'users' table in the database.
 */
@Entity
@Table(name = "users")
public class Users {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "email_id", unique = true, nullable = false)
    private String emailId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Transient
    private String username;

    @Transient
    private String phoneNumber;

    @Transient
    private String role;

    @Transient
    private Long domainId;

    @Transient
    private String profileImage;

    public Users() {}

    public Users(String emailId, String passwordHash) {
        this.emailId = emailId;
        this.passwordHash = passwordHash;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // Getters and Setters for DTO fields (Transient properties)
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Maps standard JSON property 'email' to database column 'email_id'
    public String getEmail() {
        return emailId;
    }

    public void setEmail(String email) {
        this.emailId = email;
    }

    // Maps standard JSON property 'password' to database column 'password_hash'
    public String getPassword() {
        return passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
