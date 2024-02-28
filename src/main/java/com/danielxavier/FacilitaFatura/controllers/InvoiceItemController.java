package com.danielxavier.FacilitaFatura.controllers;

import com.danielxavier.FacilitaFatura.dto.InvoiceItemDTO;
import com.danielxavier.FacilitaFatura.services.InvoiceItemService;
import com.danielxavier.FacilitaFatura.services.TextractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.danielxavier.FacilitaFatura.services.ImageService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/invoiceitem")
public class InvoiceItemController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private TextractService textractService;

    @Autowired
    private InvoiceItemService invoiceItemService;

    @GetMapping
    public ResponseEntity<Page<InvoiceItemDTO>> findAll(Pageable pageable){
        Page<InvoiceItemDTO> list = invoiceItemService.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<InvoiceItemDTO> findById(@PathVariable Long id){
        InvoiceItemDTO dto = invoiceItemService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping(value = "/{id}/upload")
    public ResponseEntity<Double> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String base64Image = imageService.convertToBase64(file);
        String textract = textractService.analyzeDocument(base64Image);
        String brand = invoiceItemService.determineBrand(textract);
        List<InvoiceItemDTO> list = invoiceItemService.parseInvoiceItems(id, textract, brand);
        Double total = invoiceItemService.sumInvoiceItemValues(list);
        return ResponseEntity.ok().body(total);
    }

    @PatchMapping("/{invoiceItemId}/client/{clientId}")
    public ResponseEntity<Void> assignClientToInvoiceItem(@PathVariable Long invoiceItemId, @PathVariable Long clientId) {
        invoiceItemService.assignClientToInvoiceItem(invoiceItemId, clientId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<InvoiceItemDTO> update(@PathVariable Long id, @RequestBody InvoiceItemDTO dto){
        dto = invoiceItemService.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        invoiceItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
