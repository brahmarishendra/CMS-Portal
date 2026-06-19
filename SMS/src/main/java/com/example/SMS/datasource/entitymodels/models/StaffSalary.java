package com.example.SMS.datasource.entitymodels.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

// POJO Entity Class
// Location: src/main/java/com/example/SMS/datasource/entitymodels/models/StaffSalary.java
@Entity
@Table(name = "stuff_salary")
public class StaffSalary {

    @Id
    @Column(name = "phone_no", length = 20)
    private String phoneNo;

    @Column(name = "staff_name", length = 100)
    private String staffName;

    @Column(name = "salary_amount")
    private BigDecimal salaryAmount;

    @Column(name = "taxes")
    private BigDecimal taxes;

    @Column(name = "total")
    private BigDecimal total;

    public StaffSalary() {}

    public StaffSalary(String phoneNo, String staffName, BigDecimal salaryAmount, BigDecimal taxes, BigDecimal total) {
        this.phoneNo = phoneNo;
        this.staffName = staffName;
        this.salaryAmount = salaryAmount;
        this.taxes = taxes;
        this.total = total;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public BigDecimal getSalaryAmount() {
        return salaryAmount;
    }

    public void setSalaryAmount(BigDecimal salaryAmount) {
        this.salaryAmount = salaryAmount;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
