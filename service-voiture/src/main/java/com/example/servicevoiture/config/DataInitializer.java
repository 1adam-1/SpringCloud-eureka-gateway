package com.example.servicevoiture.config;

import com.example.servicevoiture.entities.Client;
import com.example.servicevoiture.entities.Voiture;
import com.example.servicevoiture.repository.VoitureRepository;
import com.example.servicevoiture.service.ClientService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initialiserBaseH2(VoitureRepository voitureRepository, ClientService clientService){
        return args -> {
            Client c1 = null;
            Client c2 = null;
            try {
                c1 = clientService.getClientById(2L);
            } catch (FeignException e){
                log.warn("SERVICE-CLIENT unavailable when fetching client id=2 - continuing without client ({}).", e.status(), e);
            } catch (Exception e){
                log.warn("Error fetching client id=2 - continuing without client.", e);
            }
            try {
                c2 = clientService.getClientById(1L);
            } catch (FeignException e){
                log.warn("SERVICE-CLIENT unavailable when fetching client id=1 - continuing without client ({}).", e.status(), e);
            } catch (Exception e){
                log.warn("Error fetching client id=1 - continuing without client.", e);
            }

            log.info("**************************");
            if (c2 != null) {
                log.info("Id est :{}", c2.getId());
                log.info("Nom est :{}", c2.getNom());
            } else {
                log.info("Client c2 is null (SERVICE-CLIENT unavailable)");
            }
            log.info("**************************");

            if (c1 != null) {
                log.info("Id est :{}", c1.getId());
                log.info("Nom est :{}", c1.getNom());
                log.info("Age est :{}", c1.getAge());
            } else {
                log.info("Client c1 is null (SERVICE-CLIENT unavailable)");
            }
            log.info("**************************");

            // Save voitures; client may be null if service unavailable.
            voitureRepository.save(new Voiture(null, "Toyota", "A 25 333", "Corolla", c2));
            voitureRepository.save(new Voiture(null, "Renault", "B 6 3456", "Megane", c2));
            voitureRepository.save(new Voiture(null, "Peugeot", "A 55 4444", "301", c1));
        };
    }
}
