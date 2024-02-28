package com.danielxavier.FacilitaFatura.dto;

import com.danielxavier.FacilitaFatura.entities.InvoiceItem;

import java.time.LocalDate;

public class InvoiceItemDTO {

    private Long id;
    private String brand;
    private LocalDate purchaseDate;
    private String establishment;
    private String installment;
    private Double itemValue;
    private Long invoiceId;

    public InvoiceItemDTO(){}

    public InvoiceItemDTO(Long id, String brand, LocalDate purchaseDate, String establishment, String installment, Double itemValue, Long invoiceId) {
        this.id = id;
        this.brand = brand;
        this.purchaseDate = purchaseDate;
        this.establishment = establishment;
        this.installment = installment;
        this.itemValue = itemValue;
        this.invoiceId = invoiceId;
    }

    public InvoiceItemDTO(InvoiceItem entity) {
        this.id = entity.getId();
        this.brand = entity.getBrand();
        this.purchaseDate = entity.getPurchaseDate();
        this.establishment = entity.getEstablishment();
        this.installment = entity.getInstallment();
        this.itemValue = entity.getItemValue();
        this.invoiceId = entity.getInvoice() != null ? entity.getInvoice().getId() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getEstablishment() {
        return establishment;
    }

    public void setEstablishment(String establishment) {
        this.establishment = establishment;
    }

    public String getInstallment() {
        return installment;
    }

    public void setInstallment(String installment) {
        this.installment = installment;
    }

    public Double getItemValue() {
        return itemValue;
    }

    public void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
}
