package com.danielxavier.FacilitaFatura.services;

import com.danielxavier.FacilitaFatura.dto.ClientDTO;
import com.danielxavier.FacilitaFatura.entities.Client;
import com.danielxavier.FacilitaFatura.exceptions.DatabaseException;
import com.danielxavier.FacilitaFatura.exceptions.ResourceNotFoundException;
import com.danielxavier.FacilitaFatura.repositories.ClientRepository;
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
public class ClientService {

    @Autowired
    private ClientRepository repository;

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAllPaged(Pageable pageable){
        Page<Client> list = repository.findAll(pageable);
        return list.map(ClientDTO::new);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id){
        Optional<Client> obj = repository.findById(id);
        Client entity = obj.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado!"));
        return new ClientDTO(entity);
    }

    @Transactional
    public ClientDTO insert(ClientDTO dto){
        Client entity = new Client();
        entity.setName(dto.getName());
        entity = repository.save(entity);
        return new ClientDTO(entity);
    }

    @Transactional
    public void adicionarValor(Long id, Double total) {
        try {
            Client entity = repository.getReferenceById(id);
            entity.setTotal(entity.getTotal()+total);
            repository.save(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encontrado " + id);
        }
    }

    @Transactional
    public ClientDTO update(Long id, ClientDTO dto) {
        try {
            Client entity = repository.getReferenceById(id);
            entity.setName(dto.getName());
            entity = repository.save(entity);
            return new ClientDTO(entity);
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
