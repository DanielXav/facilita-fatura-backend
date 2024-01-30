package com.danielxavier.FacilitaFatura.controllers;

import com.danielxavier.FacilitaFatura.entities.InvoiceItem;
import com.danielxavier.FacilitaFatura.services.InvoiceItemService;
import com.danielxavier.FacilitaFatura.services.TextractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.danielxavier.FacilitaFatura.services.ImageService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private TextractService textractService;

    @Autowired
    private InvoiceItemService invoiceItemService;

    @PostMapping("/upload")
    public ResponseEntity<List<InvoiceItem>> uploadFile(@RequestParam("file") MultipartFile file) {
        String base64Image = imageService.convertToBase64(file);
        String textract = textractService.analyzeDocument(base64Image);
        String brand = invoiceItemService.determineBrand(textract);
        List<InvoiceItem> list = invoiceItemService.parseInvoiceItems(textract, brand);
        return ResponseEntity.ok().body(list);
    }
}
