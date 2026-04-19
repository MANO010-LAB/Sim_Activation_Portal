package com.example.sim_activation_portal.service;

import com.example.sim_activation_portal.entity.Offer;
import com.example.sim_activation_portal.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OfferService {
    @Autowired
    private OfferRepository offerRepository;

    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }
}