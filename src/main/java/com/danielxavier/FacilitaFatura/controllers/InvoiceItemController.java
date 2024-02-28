package com.danielxavier.FacilitaFatura.controllers;

import com.danielxavier.FacilitaFatura.dto.InvoiceItemDTO;
import com.danielxavier.FacilitaFatura.services.InvoiceItemService;
import com.danielxavier.FacilitaFatura.services.TextractService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(value = "/{id}/upload")
    public ResponseEntity<Double> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String base64Image = imageService.convertToBase64(file);
        String textract = textractService.analyzeDocument(base64Image);
        String brand = invoiceItemService.determineBrand(textract);
        List<InvoiceItemDTO> list = invoiceItemService.parseInvoiceItems(id, textract, brand);
        Double total = invoiceItemService.sumInvoiceItemValues(list);
        return ResponseEntity.ok().body(total);
    }
}
