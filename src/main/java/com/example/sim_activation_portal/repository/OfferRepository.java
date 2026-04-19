package com.example.sim_activation_portal.repository;

import com.example.sim_activation_portal.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {
}