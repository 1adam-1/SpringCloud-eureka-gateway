package com.example.servicevoiture.repository;

import com.example.servicevoiture.entities.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoitureRepository extends JpaRepository<Voiture, Long> {
    // Find voitures by the client's id (derives property path: voiture.client.id)
    List<Voiture> findByClientId(Long clientId);
}
