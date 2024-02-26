package com.danielxavier.FacilitaFatura.services;

import com.danielxavier.FacilitaFatura.dto.InvoiceDTO;
import com.danielxavier.FacilitaFatura.entities.Invoice;
import com.danielxavier.FacilitaFatura.repositories.InvoiceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Transactional
    public InvoiceDTO insert(InvoiceDTO dto){
        Invoice entity = new Invoice();
        BeanUtils.copyProperties(dto, entity);
        entity = invoiceRepository.save(entity);
        return new InvoiceDTO(entity);
    }
}
