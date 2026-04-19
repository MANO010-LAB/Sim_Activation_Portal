package com.example.sim_activation_portal.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.sim_activation_portal.entity.Customer;
import com.example.sim_activation_portal.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // 1. Validate login using email and date of birth
    public Optional<Customer> validateLogin(String email, LocalDate dob) {
        return customerRepository.findByEmailAndDateOfBirth(email, dob);
    }

    // 2. Validate customer by first name, last name, and email
    public Optional<Customer> validateCustomerDetails(String firstName, String lastName, String email) {
        return customerRepository.findByFirstNameAndLastNameAndEmail(firstName, lastName, email);
    }

    // 3. Update address for a given customer
    public Customer updateAddress(Long customerId, String newAddress) {
        Optional<Customer> opt = customerRepository.findById(customerId);
        if (opt.isPresent()) {
            Customer customer = opt.get();
            customer.setAddress(newAddress);
            return customerRepository.save(customer);
        }
        return null;
    }

    // 4. Validate ID proof (number and type) - now accepts customerId parameter
    public boolean validateIdProof(Long customerId, String idProofNumber, String idProofType) {
        Optional<Customer> opt = customerRepository.findById(customerId);
        return opt.isPresent() &&
                opt.get().getIdProofNumber() != null &&
                opt.get().getIdProofNumber().equals(idProofNumber) &&
                opt.get().getIdProofType() != null &&
                opt.get().getIdProofType().equalsIgnoreCase(idProofType);
    }

    // 5. Get customer by ID
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    // 6. Get all customers with pagination and sorting
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    // 7. Get all customers as a plain list (for dropdown)
    public List<Customer> getAllCustomersList() {
        return customerRepository.findAll();
    }

    // 8. Add a new customer (with email uniqueness check)
    public Customer addCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email already exists: " + customer.getEmail());
        }
        return customerRepository.save(customer);
    }

    // 9. Update all customer details (full update)
    public Customer updateCustomer(Long id, Customer updatedData) {
        Optional<Customer> opt = customerRepository.findById(id);
        if (opt.isPresent()) {
            Customer existing = opt.get();
            existing.setFirstName(updatedData.getFirstName());
            existing.setLastName(updatedData.getLastName());
            existing.setEmail(updatedData.getEmail());
            existing.setDateOfBirth(updatedData.getDateOfBirth());
            existing.setMobileNumber(updatedData.getMobileNumber());
            existing.setIdProofNumber(updatedData.getIdProofNumber());
            existing.setIdProofType(updatedData.getIdProofType());
            existing.setAddress(updatedData.getAddress());
            return customerRepository.save(existing);
        }
        return null;
    }

    // 10. Delete customer by ID
    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 11. Get the first customer (for admin session default)
    public Optional<Customer> getFirstCustomer() {
        return customerRepository.findAll().stream().findFirst();
    }
}