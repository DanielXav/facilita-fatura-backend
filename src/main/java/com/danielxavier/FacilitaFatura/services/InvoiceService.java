package com.danielxavier.FacilitaFatura.services;

import com.danielxavier.FacilitaFatura.dto.InvoiceDTO;
import com.danielxavier.FacilitaFatura.entities.Invoice;
import com.danielxavier.FacilitaFatura.exceptions.DatabaseException;
import com.danielxavier.FacilitaFatura.exceptions.ResourceNotFoundException;
import com.danielxavier.FacilitaFatura.repositories.InvoiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Transactional(readOnly = true)
    public Page<InvoiceDTO> findAllPaged(Pageable pageable){
        Page<Invoice> list = invoiceRepository.findAll(pageable);
        return list.map(InvoiceDTO::new);
    }

    @Transactional(readOnly = true)
    public InvoiceDTO findById(Long id){
        Invoice entity = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id não encontrado " + id));
        return new InvoiceDTO(entity);
    }


    @Transactional
    public InvoiceDTO insert(InvoiceDTO dto){
        Invoice entity = new Invoice();
        BeanUtils.copyProperties(dto, entity);
        entity = invoiceRepository.save(entity);
        return new InvoiceDTO(entity);
    }

    @Transactional
    public InvoiceDTO update(Long id, InvoiceDTO dto){
        try {
            Invoice entity = invoiceRepository.getReferenceById(id);
            entity.setDate(dto.getDate());
            entity = invoiceRepository.save(entity);
            return new InvoiceDTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encontrado " + id);
        }
    }

    public void delete(Long id) {
        if (!invoiceRepository.existsById(id)){
            throw new ResourceNotFoundException("Fatura não encontrada!");
        }
        try {
            invoiceRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha na integridade referencial");
        }
    }
}
