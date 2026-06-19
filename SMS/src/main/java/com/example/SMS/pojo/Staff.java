package com.example.SMS.pojo;

// POJO Entity Class
// Location: src/main/java/com/example/SMS/pojo/Staff.java
import jakarta.persistence.*;

@Entity
@Table(name = "stuff_signup")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;


    // No argument constructor of the class
    public Staff() {}

    // Argument constructor of the class
    public Staff(Long id, String firstName, String lastName, String email, String phoneNumber, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void Simpledata(){
        this.firstName = "Sri";
        this.lastName = "Rama";
        this.email = "[EMAIL_ADDRESS]";
        this.phoneNumber = "9876543210";
        this.password = "2025";
    }
}
