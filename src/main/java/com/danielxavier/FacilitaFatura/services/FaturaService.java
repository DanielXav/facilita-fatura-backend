package com.danielxavier.FacilitaFatura.services;

import com.danielxavier.FacilitaFatura.dto.ClienteDTO;
import com.danielxavier.FacilitaFatura.dto.FaturaDTO;
import com.danielxavier.FacilitaFatura.entities.Cliente;
import com.danielxavier.FacilitaFatura.entities.Fatura;
import com.danielxavier.FacilitaFatura.enums.Brand;
import com.danielxavier.FacilitaFatura.enums.Month;
import com.danielxavier.FacilitaFatura.exceptions.DatabaseException;
import com.danielxavier.FacilitaFatura.exceptions.ResourceNotFoundException;
import com.danielxavier.FacilitaFatura.repositories.ClienteRepository;
import com.danielxavier.FacilitaFatura.repositories.FaturaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class FaturaService {

    @Autowired
    private FaturaRepository repository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public Page<FaturaDTO> findAllPaged(Pageable pageable){
        Page<Fatura> list = repository.findAll(pageable);
        return list.map(FaturaDTO::new);
    }

    @Transactional(readOnly = true)
    public FaturaDTO findById(Long id){
        Optional<Fatura> obj = repository.findById(id);
        Fatura entity = obj.orElseThrow(() -> new EntityNotFoundException("Fatura não encontrado!"));
        return new FaturaDTO(entity, entity.getClientes());
    }

    @Transactional
    public FaturaDTO insert(FaturaDTO dto){
        Fatura entity = new Fatura();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new FaturaDTO(entity);
    }

    @Transactional
    public FaturaDTO update(Long id, FaturaDTO dto) {
        try {
            Fatura entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new FaturaDTO(entity);
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

    private void copyDtoToEntity(FaturaDTO dto, Fatura entity) {
        entity.setInvoice_month(dto.getInvoice_month());
        entity.setBrand(dto.getBrand());
        entity.setTotalMonth(dto.getTotalMonth());
        entity.setDate(dto.getDate());

        entity.getClientes().clear();
        for (ClienteDTO cliDTO : dto.getClientes()){
            Cliente cliente = clienteRepository.getOne(cliDTO.getId());
            entity.getClientes().add(cliente);
        }
    }
}
