package com.danielxavier.FacilitaFatura.repositories;

import com.danielxavier.FacilitaFatura.entities.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaturaRepository extends JpaRepository<Fatura, Long> {
}
