package com.example.sim_activation_portal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sim_activation_portal.entity.Customer;
import com.example.sim_activation_portal.entity.Offer;
import com.example.sim_activation_portal.entity.Sim;
import com.example.sim_activation_portal.service.CustomerService;
import com.example.sim_activation_portal.service.OfferService;
import com.example.sim_activation_portal.service.SimService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class RestApiController {

    @Autowired
    private SimService simService;
    @Autowired
    private OfferService offerService;
    @Autowired
    private CustomerService customerService;

    // ========================= PUBLIC / DASHBOARD ENDPOINTS =========================
    @GetMapping("/offers")
    public ResponseEntity<List<Offer>> getOffers() {
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @PostMapping("/sim/validate")
    public ResponseEntity<?> validateSim(@RequestParam String simNumber) {
        String result = simService.validateSim(simNumber);
        Map<String, String> response = new HashMap<>();
        response.put("status", result);
        if ("VALID".equals(result)) {
            response.put("message", "SIM is valid and ready for activation");
        } else {
            response.put("message", result);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customer/validate")
    public ResponseEntity<?> validateCustomerDetails(@RequestParam String firstName,
                                                     @RequestParam String lastName,
                                                     @RequestParam String email) {
        Optional<Customer> customer = customerService.validateCustomerDetails(firstName, lastName, email);
        if (customer.isPresent()) {
            return ResponseEntity.ok(Map.of("valid", true, "message", "Customer details validated successfully"));
        } else {
            return ResponseEntity.ok(Map.of("valid", false, "message", "No customer found with these details"));
        }
    }

    @PutMapping("/customer/address")
    public ResponseEntity<?> updateAddress(@RequestParam String newAddress, HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Not logged in"));
        }
        Customer updated = customerService.updateAddress(customerId, newAddress);
        if (updated != null) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Address updated successfully", "newAddress", updated.getAddress()));
        } else {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "Failed to update address"));
        }
    }

    // Updated ID proof validation – now accepts customerId as parameter
    @PostMapping("/customer/idproof/validate")
    public ResponseEntity<?> validateIdProof(@RequestParam Long customerId,
                                             @RequestParam String idProofNumber,
                                             @RequestParam String idProofType) {
        boolean isValid = customerService.validateIdProof(customerId, idProofNumber, idProofType);
        if (isValid) {
            return ResponseEntity.ok(Map.of("valid", true, "message", "ID proof is valid"));
        } else {
            return ResponseEntity.ok(Map.of("valid", false, "message", "ID proof does not match our records"));
        }
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateSim(@RequestParam String simNumber, HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Not logged in"));
        }
        String validation = simService.validateSim(simNumber);
        if (!"VALID".equals(validation)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", validation));
        }
        boolean activated = simService.activateSim(simNumber, customerId);
        if (activated) {
            return ResponseEntity.ok(Map.of("success", true, "message", "SIM activated successfully!"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Activation failed"));
        }
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateSim(@RequestParam String simNumber) {
        boolean deactivated = simService.deactivateSim(simNumber);
        if (deactivated) {
            return ResponseEntity.ok(Map.of("success", true, "message", "SIM deactivated successfully!"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Deactivation failed. SIM not found or not active."));
        }
    }

    @GetMapping("/customers/list")
    public ResponseEntity<List<Customer>> getAllCustomersList() {
        return ResponseEntity.ok(customerService.getAllCustomersList());
    }

    @GetMapping("/auth/current-customer")
    public ResponseEntity<?> getCurrentCustomer(HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        String customerName = (String) session.getAttribute("customerName");
        if (customerId == null) {
            return ResponseEntity.ok(Map.of("customerId", null, "customerName", "None"));
        }
        return ResponseEntity.ok(Map.of("customerId", customerId, "customerName", customerName));
    }

    // ===================== ADMIN: CUSTOMER MANAGEMENT =====================
    @GetMapping("/admin/customers")
    public ResponseEntity<Page<Customer>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Customer> customers = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/admin/customer/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Customer not found"));
        }
    }

    @GetMapping("/admin/customer/{id}/sims")
    public ResponseEntity<?> getSimsByCustomerId(@PathVariable Long id) {
        List<Sim> sims = simService.getSimsByCustomerId(id);
        return ResponseEntity.ok(sims);
    }

    @PostMapping("/admin/customer")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        try {
            Customer saved = customerService.addCustomer(customer);
            return ResponseEntity.ok(Map.of("success", true, "customer", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/admin/customer/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Customer updated = customerService.updateCustomer(id, customer);
        if (updated != null) {
            return ResponseEntity.ok(Map.of("success", true, "customer", updated));
        } else {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Customer not found"));
        }
    }

    @DeleteMapping("/admin/customer/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Customer deleted"));
        } else {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Customer not found"));
        }
    }

    // ===================== ADMIN: SIM ASSIGNMENT & STATUS =====================
    @GetMapping("/admin/sims/unassigned")
    public ResponseEntity<?> getUnassignedSims() {
        List<Sim> sims = simService.getUnassignedSims();
        return ResponseEntity.ok(sims);
    }

    @PostMapping("/admin/customer/{customerId}/assign-sim/{simId}")
    public ResponseEntity<?> assignSimToCustomer(@PathVariable Long customerId, @PathVariable Long simId) {
        boolean assigned = simService.assignSimToCustomer(simId, customerId);
        if (assigned) {
            return ResponseEntity.ok(Map.of("success", true, "message", "SIM assigned successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Assignment failed. SIM may already be assigned or not exist."));
        }
    }

    @PostMapping("/admin/customer/{customerId}/assign-sim-by-number")
    public ResponseEntity<?> assignSimByNumber(@PathVariable Long customerId, @RequestBody Map<String, String> request) {
        String simNumber = request.get("simNumber");
        if (simNumber == null || simNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "SIM number is required"));
        }
        boolean assigned = simService.assignSimToCustomerByNumber(simNumber, customerId);
        if (assigned) {
            return ResponseEntity.ok(Map.of("success", true, "message", "SIM assigned successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "SIM not found, already assigned, or not inactive"));
        }
    }

    @PostMapping("/admin/customer/{customerId}/unassign-sim/{simId}")
    public ResponseEntity<?> unassignSimFromCustomer(@PathVariable Long customerId, @PathVariable Long simId) {
        List<Sim> customerSims = simService.getSimsByCustomerId(customerId);
        boolean belongsToCustomer = customerSims.stream().anyMatch(s -> s.getId().equals(simId));
        if (!belongsToCustomer) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "This SIM is not assigned to this customer."));
        }
        boolean unassigned = simService.unassignSimFromCustomer(simId);
        if (unassigned) {
            return ResponseEntity.ok(Map.of("success", true, "message", "SIM unassigned successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Unassignment failed"));
        }
    }

    @PostMapping("/admin/sim/{simId}/toggle-status")
    public ResponseEntity<?> toggleSimStatus(@PathVariable Long simId) {
        boolean toggled = simService.toggleSimStatus(simId);
        if (toggled) {
            return ResponseEntity.ok(Map.of("success", true, "message", "SIM status toggled successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Failed to toggle SIM status"));
        }
    }
}