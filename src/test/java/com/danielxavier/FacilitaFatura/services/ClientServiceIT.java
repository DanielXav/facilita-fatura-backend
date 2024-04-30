package com.danielxavier.FacilitaFatura.services;

import com.danielxavier.FacilitaFatura.entities.Client;
import com.danielxavier.FacilitaFatura.repositories.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ClientServiceIT {

    @Autowired
    private ClientService service;

    @Autowired
    private ClientRepository repository;

    private Long existingId;
    private Long countTotalClients;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        countTotalClients = 3L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {

        service.delete(existingId);

        Assertions.assertEquals(countTotalClients-1, repository.count());
    }
}
