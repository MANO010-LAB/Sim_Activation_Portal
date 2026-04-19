package com.example.sim_activation_portal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.sim_activation_portal.entity.Sim;
import com.example.sim_activation_portal.enums.SimStatus;

public interface SimRepository extends JpaRepository<Sim, Long> {
    
    // ==================== BASIC FINDERS ====================
    Optional<Sim> findBySimNumber(String simNumber);
    Optional<Sim> findBySimId(String simId);
    boolean existsBySimNumberAndStatus(String simNumber, SimStatus status);
    
    // ==================== CUSTOMER ASSIGNMENT ====================
    List<Sim> findByCustomerId(Long customerId);
    List<Sim> findByCustomerIdIsNullAndStatus(SimStatus status);
    List<Sim> findByCustomerIdIsNull();
    List<Sim> findByCustomerIdAndStatus(Long customerId, SimStatus status);
    
    // ==================== STATUS FILTERS ====================
    List<Sim> findByStatus(SimStatus status);
    
    // ==================== VALIDATION & UNIQUENESS ====================
    boolean existsBySimNumberAndCustomerIdIsNotNull(String simNumber);
    Optional<Sim> findBySimNumberAndCustomerId(String simNumber, Long customerId);
    
    // ==================== BULK OPERATIONS ====================
    @Modifying
    @Transactional
    @Query("UPDATE Sim s SET s.customerId = NULL WHERE s.customerId = :customerId")
    void unassignAllSimsFromCustomer(@Param("customerId") Long customerId);
    
    long countByCustomerId(Long customerId);
    
    // ==================== SEARCH / FILTER ====================
    List<Sim> findByPlanNameContainingIgnoreCase(String planName);
}