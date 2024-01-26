package com.danielxavier.FacilitaFatura.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class InvoiceItem {

    private Long id;

    // Criar classe Marca
    private String brand;
    private LocalDate purchaseDate;
    private String establishment;
    private String installment;
    private BigDecimal value;

    public InvoiceItem() {
    }

    public InvoiceItem(Long id, String brand, LocalDate purchaseDate, String establishment, String installment, BigDecimal value) {
        this.id = id;
        this.brand = brand;
        this.purchaseDate = purchaseDate;
        this.establishment = establishment;
        this.installment = installment;
        this.value = value;
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

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvoiceItem that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
