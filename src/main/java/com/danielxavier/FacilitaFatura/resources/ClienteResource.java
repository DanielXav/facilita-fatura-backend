package com.danielxavier.FacilitaFatura.resources;

import com.danielxavier.FacilitaFatura.dto.ClienteDTO;
import com.danielxavier.FacilitaFatura.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Adicione esta linha

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/clientes")
public class ClienteResource {

    @Autowired
    private ClienteService service;

    private static final Logger logger = LoggerFactory.getLogger(ClienteResource.class);

    @GetMapping
    public ResponseEntity<Page<ClienteDTO>> findAll(Pageable pageable){
        Page<ClienteDTO> list = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ClienteDTO> findById(@PathVariable Long id){
        ClienteDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> insert(@RequestBody ClienteDTO dto){
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PostMapping(value = "/{id}/adicionar-valor")
    public ResponseEntity<String> insert(@PathVariable Long id, @RequestBody Double total){
        service.adicionarValor(id, total);
        return ResponseEntity.ok("Operação bem-sucedida!");
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ClienteDTO> insert(@PathVariable Long id, @RequestBody ClienteDTO dto){
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
