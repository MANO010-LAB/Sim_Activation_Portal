package com.example.sim_activation_portal.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sim_activation_portal.entity.Sim;
import com.example.sim_activation_portal.enums.SimStatus;
import com.example.sim_activation_portal.repository.SimRepository;

@Service
public class SimService {

    @Autowired
    private SimRepository simRepository;

    // ---------- Basic SIM operations ----------
    public String validateSim(String simNumber) {
        Optional<Sim> simOpt = simRepository.findBySimNumber(simNumber);
        if (simOpt.isEmpty()) {
            return "SIM not found";
        }
        Sim sim = simOpt.get();
        if (sim.getStatus() == SimStatus.ACTIVE) {
            return "SIM is already activated";
        }
        return "VALID";
    }

    public boolean activateSim(String simNumber, Long customerId) {
        Optional<Sim> simOpt = simRepository.findBySimNumber(simNumber);
        if (simOpt.isPresent()) {
            Sim sim = simOpt.get();
            if (sim.getStatus() == SimStatus.INACTIVE) {
                sim.setStatus(SimStatus.ACTIVE);
                sim.setActivationDate(LocalDate.now());
                sim.setCustomerId(customerId);
                simRepository.save(sim);
                return true;
            }
        }
        return false;
    }

    public boolean deactivateSim(String simNumber) {
        Optional<Sim> simOpt = simRepository.findBySimNumber(simNumber);
        if (simOpt.isPresent()) {
            Sim sim = simOpt.get();
            if (sim.getStatus() == SimStatus.ACTIVE) {
                sim.setStatus(SimStatus.INACTIVE);
                simRepository.save(sim);
                return true;
            }
        }
        return false;
    }

    public List<Sim> getSimsByCustomerId(Long customerId) {
        return simRepository.findByCustomerId(customerId);
    }

    // ---------- SIM management (assignment, unassignment, status toggle) ----------
    public List<Sim> getUnassignedSims() {
        return simRepository.findByCustomerIdIsNullAndStatus(SimStatus.INACTIVE);
    }

    public boolean assignSimToCustomer(Long simId, Long customerId) {
        Optional<Sim> simOpt = simRepository.findById(simId);
        if (simOpt.isPresent()) {
            Sim sim = simOpt.get();
            if (sim.getCustomerId() == null && sim.getStatus() == SimStatus.INACTIVE) {
                sim.setCustomerId(customerId);
                simRepository.save(sim);
                return true;
            }
        }
        return false;
    }

    public boolean assignSimToCustomerByNumber(String simNumber, Long customerId) {
        Optional<Sim> simOpt = simRepository.findBySimNumber(simNumber);
        if (simOpt.isPresent()) {
            Sim sim = simOpt.get();
            if (sim.getCustomerId() == null && sim.getStatus() == SimStatus.INACTIVE) {
                sim.setCustomerId(customerId);
                simRepository.save(sim);
                return true;
            }
        }
        return false;
    }

    public boolean unassignSimFromCustomer(Long simId) {
        Optional<Sim> simOpt = simRepository.findById(simId);
        if (simOpt.isPresent()) {
            Sim sim = simOpt.get();
            if (sim.getCustomerId() != null) {
                sim.setCustomerId(null);
                simRepository.save(sim);
                return true;
            }
        }
        return false;
    }

    public boolean toggleSimStatus(Long simId) {
        Optional<Sim> simOpt = simRepository.findById(simId);
        if (simOpt.isPresent()) {
            Sim sim = simOpt.get();
            if (sim.getStatus() == SimStatus.ACTIVE) {
                sim.setStatus(SimStatus.INACTIVE);
            } else {
                sim.setStatus(SimStatus.ACTIVE);
                if (sim.getActivationDate() == null) {
                    sim.setActivationDate(LocalDate.now());
                }
            }
            simRepository.save(sim);
            return true;
        }
        return false;
    }
}