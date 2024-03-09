package com.danielxavier.FacilitaFatura.repositories;

import com.danielxavier.FacilitaFatura.entities.Client;
import com.danielxavier.FacilitaFatura.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ClientRepositoryTests {

    private long existingId;
    private long countTotalClients;

    @Autowired
    private ClientRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        countTotalClients = 3L;
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Client client = Factory.createClient();
        client.setId(null);

        client = repository.save(client);

        Assertions.assertNotNull(client.getId());
        Assertions.assertEquals(countTotalClients + 1, client.getId());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existingId);

        Optional<Client> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

}
