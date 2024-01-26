package com.danielxavier.FacilitaFatura.entities;

import com.danielxavier.FacilitaFatura.enums.Brand;
import com.danielxavier.FacilitaFatura.enums.Month;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_fatura")
public class Fatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Month invoice_month;
    private Brand brand;
    private Double totalMonth;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant date;

    @ManyToMany()
    @JoinTable(
            name = "tb_fatura_cliente",
            joinColumns = @JoinColumn(name = "fatura_id"),
            inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    private Set<Cliente> clientes = new HashSet<>();

    public Fatura(){
    }

    public Fatura(Long id, Month invoice_month, Brand brand, Double totalMonth, Instant date) {
        this.id = id;
        this.invoice_month = invoice_month;
        this.brand = brand;
        this.totalMonth = totalMonth;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Month getInvoice_month() {
        return invoice_month;
    }

    public void setInvoice_month(Month invoice_month) {
        this.invoice_month = invoice_month;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Double getTotalMonth() {
        return totalMonth;
    }

    public void setTotalMonth(Double totalMonth) {
        this.totalMonth = totalMonth;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Set<Cliente> getClientes() {
        return clientes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fatura fatura)) return false;
        return Objects.equals(id, fatura.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Double calculateTotalFatura(){
        double totalFatura = 0.0;
        for (Cliente cliente : clientes){
            totalFatura += cliente.getTotal();
        }
        return totalFatura;
    }
}
