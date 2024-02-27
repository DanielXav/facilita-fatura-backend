package com.danielxavier.FacilitaFatura.repositories;

import com.danielxavier.FacilitaFatura.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
