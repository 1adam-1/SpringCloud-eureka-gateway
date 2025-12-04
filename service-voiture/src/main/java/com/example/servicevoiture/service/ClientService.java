package com.example.servicevoiture.service;

import com.example.servicevoiture.entities.Client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="SERVICE-CLIENT")
public interface ClientService{
    @GetMapping(path="/clients/{id}")
    Client getClientById(@PathVariable("id") Long id);
}
