package com.danielxavier.FacilitaFatura.services;

import com.danielxavier.FacilitaFatura.dto.ClienteDTO;
import com.danielxavier.FacilitaFatura.entities.Cliente;
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

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    @Transactional(readOnly = true)
    public Page<ClienteDTO> findAllPaged(Pageable pageable){
        Page<Cliente> list = repository.findAll(pageable);
        return list.map(ClienteDTO::new);
    }

    @Transactional(readOnly = true)
    public ClienteDTO findById(Long id){
        Optional<Cliente> obj = repository.findById(id);
        Cliente entity = obj.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado!"));
        return new ClienteDTO(entity);
    }

    @Transactional
    public ClienteDTO insert(ClienteDTO dto){
        Cliente entity = new Cliente();
        entity.setName(dto.getName());
        entity.setTotal((dto.getTotal()));
        entity = repository.save(entity);
        return new ClienteDTO(entity);
    }

    @Transactional
    public void adicionarValor(Long id, Double total) {
        try {
            Cliente entity = repository.getReferenceById(id);
            entity.setTotal(entity.getTotal()+total);
            repository.save(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encontrado " + id);
        }
    }

    @Transactional
    public ClienteDTO update(Long id, ClienteDTO dto) {
        try {
            Cliente entity = repository.getReferenceById(id);
            entity.setName(dto.getName());
            entity.setTotal((dto.getTotal()));
            entity = repository.save(entity);
            return new ClienteDTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encontrado " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)){
            throw new ResourceNotFoundException("Cliente não encontrado!");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha na integridade referencial");
        }
    }

}
