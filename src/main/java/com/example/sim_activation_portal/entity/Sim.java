package com.example.sim_activation_portal.entity;

import com.example.sim_activation_portal.enums.SimStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sim")
public class Sim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String simNumber;
    private String simId;
    private String planName;
    @Enumerated(EnumType.ORDINAL)
    private SimStatus status;
    private LocalDate activationDate;
    private Long customerId;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSimNumber() { return simNumber; }
    public void setSimNumber(String simNumber) { this.simNumber = simNumber; }
    public String getSimId() { return simId; }
    public void setSimId(String simId) { this.simId = simId; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public SimStatus getStatus() { return status; }
    public void setStatus(SimStatus status) { this.status = status; }
    public LocalDate getActivationDate() { return activationDate; }
    public void setActivationDate(LocalDate activationDate) { this.activationDate = activationDate; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}