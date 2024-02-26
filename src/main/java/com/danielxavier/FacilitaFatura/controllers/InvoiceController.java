package com.danielxavier.FacilitaFatura.controllers;

import com.danielxavier.FacilitaFatura.dto.InvoiceDTO;
import com.danielxavier.FacilitaFatura.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDTO> insert(@RequestBody InvoiceDTO invoice) {
        invoice = invoiceService.insert(invoice);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(invoice.getId()).toUri();
        return ResponseEntity.created(uri).body(invoice);
    }
}
