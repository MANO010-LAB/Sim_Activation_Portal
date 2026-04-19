package com.example.sim_activation_portal.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.sim_activation_portal.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Existing query methods
    Optional<Customer> findByEmailAndDateOfBirth(String email, LocalDate dateOfBirth);
    Optional<Customer> findByFirstNameAndLastNameAndEmail(String firstName, String lastName, String email);
    
    // New features: pagination and sorting are already provided by JpaRepository
    // But we can add custom finders for search functionality:
    Page<Customer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String firstName, String lastName, String email, Pageable pageable);
    
    boolean existsByEmail(String email);
}