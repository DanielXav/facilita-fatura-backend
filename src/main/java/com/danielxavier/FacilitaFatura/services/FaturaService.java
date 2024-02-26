package com.danielxavier.FacilitaFatura.services;

import com.danielxavier.FacilitaFatura.dto.ClientDTO;
import com.danielxavier.FacilitaFatura.dto.Fatura_old_DTO;
import com.danielxavier.FacilitaFatura.entities.Client;
import com.danielxavier.FacilitaFatura.entities.Fatura_OLD;
import com.danielxavier.FacilitaFatura.exceptions.DatabaseException;
import com.danielxavier.FacilitaFatura.exceptions.ResourceNotFoundException;
import com.danielxavier.FacilitaFatura.repositories.ClientRepository;
import com.danielxavier.FacilitaFatura.repositories.FaturaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FaturaService {

    @Autowired
    private FaturaRepository repository;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public Page<Fatura_old_DTO> findAllPaged(Pageable pageable){
        Page<Fatura_OLD> list = repository.findAll(pageable);
        return list.map(Fatura_old_DTO::new);
    }

    @Transactional(readOnly = true)
    public Fatura_old_DTO findById(Long id){
        Optional<Fatura_OLD> obj = repository.findById(id);
        Fatura_OLD entity = obj.orElseThrow(() -> new EntityNotFoundException("Fatura não encontrado!"));
        return new Fatura_old_DTO(entity, entity.getClientes());
    }

    @Transactional
    public Fatura_old_DTO insert(Fatura_old_DTO dto){
        Fatura_OLD entity = new Fatura_OLD();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new Fatura_old_DTO(entity);
    }

    @Transactional
    public Fatura_old_DTO update(Long id, Fatura_old_DTO dto) {
        try {
            Fatura_OLD entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new Fatura_old_DTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encontrado " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)){
            throw new ResourceNotFoundException("Fatura não encontrado!");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha na integridade referencial");
        }
    }

    private void copyDtoToEntity(Fatura_old_DTO dto, Fatura_OLD entity) {
        entity.setInvoice_month(dto.getInvoice_month());
        entity.setBrand(dto.getBrand());
        entity.setTotalMonth(dto.getTotalMonth());
        entity.setDate(dto.getDate());

        entity.getClientes().clear();
        for (ClientDTO cliDTO : dto.getClientes()){
            Client client = clientRepository.getOne(cliDTO.getId());
            entity.getClientes().add(client);
        }
    }
}
