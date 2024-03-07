package com.danielxavier.FacilitaFatura.controllers;

import com.danielxavier.FacilitaFatura.dto.InvoiceDTO;
import com.danielxavier.FacilitaFatura.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<Page<InvoiceDTO>> findAll(Pageable pageable){
        Page<InvoiceDTO> list = invoiceService.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<InvoiceDTO> findById(@PathVariable Long id){
        InvoiceDTO dto = invoiceService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<InvoiceDTO> insert(@RequestBody InvoiceDTO invoice) {
        invoice = invoiceService.insert(invoice);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(invoice.getId()).toUri();
        return ResponseEntity.created(uri).body(invoice);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<InvoiceDTO> update(@PathVariable Long id, @RequestBody InvoiceDTO invoice) {
        invoice = invoiceService.update(id, invoice);
        return ResponseEntity.ok().body(invoice);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
