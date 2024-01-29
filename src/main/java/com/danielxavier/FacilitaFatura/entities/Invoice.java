package com.danielxavier.FacilitaFatura.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Invoice {

    @Id
    private Long id;
    private LocalDate date;

    @OneToMany(mappedBy = "invoice")
    private List<InvoiceItem> items;

    public Invoice(){
    }

    public Invoice(Long id, LocalDate date, List<InvoiceItem> items) {
        this.id = id;
        this.date = date;
        this.items = items;
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

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }
}
