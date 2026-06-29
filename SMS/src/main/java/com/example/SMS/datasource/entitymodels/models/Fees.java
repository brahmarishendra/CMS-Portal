package com.example.SMS.datasource.entitymodels.models;

import jakarta.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "student_fee")
public class Fees {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private int id;

    @Column(name = "student_id", nullable = false)
    private long studentId;

    @Column(name = "fee_type", nullable = false, length = 100)
    private String feeType;

    @Column(name = "term_name", nullable = false, length = 50)
    private String termName;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "due_amount", insertable = false, updatable = false)
    private BigDecimal dueAmount;

    @Column(name = "due_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "Unpaid";

    @Column(name = "last_payment_date")
    @Temporal(TemporalType.DATE)
    private Date lastPaymentDate;

    public Fees() {}

    public Fees(long studentId, String feeType, String termName, BigDecimal totalAmount, BigDecimal paidAmount, Date dueDate, String paymentStatus) {
        this.studentId = studentId;
        this.feeType = feeType;
        this.termName = termName;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.dueDate = dueDate;
        this.paymentStatus = paymentStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getDueAmount() {
        if (dueAmount != null) {
            return dueAmount;
        }
        if (totalAmount != null) {
            BigDecimal paid = paidAmount != null ? paidAmount : BigDecimal.ZERO;
            return totalAmount.subtract(paid);
        }
        return BigDecimal.ZERO;
    }

    public void setDueAmount(BigDecimal dueAmount) {
        this.dueAmount = dueAmount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }
}
