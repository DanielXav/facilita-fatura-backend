package com.danielxavier.FacilitaFatura.repositories;

import com.danielxavier.FacilitaFatura.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
