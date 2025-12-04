package com.example.servicevoiture.controller;

import com.example.servicevoiture.entities.Client;
import com.example.servicevoiture.entities.Voiture;
import com.example.servicevoiture.repository.VoitureRepository;
import com.example.servicevoiture.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/voitures")
public class VoitureController {

    private final VoitureRepository voitureRepository;
    private final ClientService clientService;

    @PersistenceContext
    private EntityManager entityManager;

    public VoitureController(VoitureRepository voitureRepository, ClientService clientService) {
        this.voitureRepository = voitureRepository;
        this.clientService = clientService;
    }

    @GetMapping(produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Voiture>> findAll() {
        List<Voiture> voitures = voitureRepository.findAll();
        return ResponseEntity.ok(voitures);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<Voiture> opt = voitureRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voiture not found with ID: " + id);
        }
        Voiture v = opt.get();
        // Optionally fetch client details from the client service if needed
        try {
            if (v.getClient() != null && v.getClient().getId() != null) {
                Client client = clientService.getClientById(v.getClient().getId());
                v.setClient(client);
            }
        } catch (Exception ignored) {
            // ignore remote client errors; return voiture without enriched client
        }
        return ResponseEntity.ok(v);
    }

    @GetMapping(value = "/client/{clientId}", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Voiture>> findByClient(@PathVariable Long clientId) {
        List<Voiture> voitures = voitureRepository.findByClientId(clientId);
        return ResponseEntity.ok(voitures);
    }

    @PostMapping(value = "/{clientId}", produces = "application/json")
    public ResponseEntity<?> save(@PathVariable Long clientId, @RequestBody Voiture voiture) {
        // Use a JPA reference to avoid persisting a transient Client; this creates a proxy without fetching the client
        try {
            Client clientRef = entityManager.getReference(Client.class, clientId);
            voiture.setClient(clientRef);
            Voiture saved = voitureRepository.save(voiture);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            // Fallback: persist with minimal client object if getReference fails for some reason
            Client client = new Client();
            client.setId(clientId);
            voiture.setClient(client);
            Voiture saved = voitureRepository.save(voiture);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Voiture updatedVoiture) {
        try {
            Voiture existingVoiture = voitureRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Voiture not found with ID: " + id));

            if (updatedVoiture.getMatricule() != null && !updatedVoiture.getMatricule().isEmpty()) {
                existingVoiture.setMatricule(updatedVoiture.getMatricule());
            }
            if (updatedVoiture.getMarque() != null && !updatedVoiture.getMarque().isEmpty()) {
                existingVoiture.setMarque(updatedVoiture.getMarque());
            }
            if (updatedVoiture.getModel() != null && !updatedVoiture.getModel().isEmpty()) {
                existingVoiture.setModel(updatedVoiture.getModel());
            }

            Voiture savedVoiture = voitureRepository.save(existingVoiture);
            return ResponseEntity.ok(savedVoiture);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating voiture: " + e.getMessage());
        }
    }
}