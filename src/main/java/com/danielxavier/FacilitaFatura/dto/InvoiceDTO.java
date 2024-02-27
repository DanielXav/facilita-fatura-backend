package com.danielxavier.FacilitaFatura.dto;

import com.danielxavier.FacilitaFatura.entities.Invoice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceDTO {

    private Long id;
    private LocalDate date;

    private List<InvoiceItemDTO> items = new ArrayList<>();

    public InvoiceDTO() {
    }

    public InvoiceDTO(Long id, LocalDate date, List<InvoiceItemDTO> items) {
        this.id = id;
        this.date = date;
        this.items = items;
    }

    public InvoiceDTO(Invoice entity) {
        this.id = entity.getId();
        this.date = entity.getDate();
        // Converte cada InvoiceItem para InvoiceItemDTO e coleta em uma lista
        this.items = entity.getItems().stream()
                .map(InvoiceItemDTO::new)
                .collect(Collectors.toList());
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<InvoiceItemDTO> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemDTO> items) {
        this.items = items;
    }
}
