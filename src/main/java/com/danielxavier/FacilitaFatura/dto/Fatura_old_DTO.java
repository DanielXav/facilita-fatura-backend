package com.danielxavier.FacilitaFatura.dto;

import com.danielxavier.FacilitaFatura.entities.Client;
import com.danielxavier.FacilitaFatura.entities.Fatura_OLD;
import com.danielxavier.FacilitaFatura.enums.Brand;
import com.danielxavier.FacilitaFatura.enums.Month;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Fatura_old_DTO {

    private Long id;
    private Month invoice_month;
    private Brand brand;
    private Double totalMonth;
    private Instant date;
    private Double totalFaturaClientes;
    private List<ClientDTO> clientes = new ArrayList<>();

    public Fatura_old_DTO(){
    }

    public Fatura_old_DTO(Long id, Month invoice_month, Brand brand, Double totalMonth, Instant date, Double totalFaturaClientes) {
        this.id = id;
        this.invoice_month = invoice_month;
        this.brand = brand;
        this.totalMonth = totalMonth;
        this.date = date;
        this.totalFaturaClientes = totalFaturaClientes;
    }

    public Fatura_old_DTO(Fatura_OLD entity){
        this.id = entity.getId();
        this.invoice_month = entity.getInvoice_month();
        this.brand = entity.getBrand();
        this.totalMonth = entity.getTotalMonth();
        this.date = entity.getDate();
        this.totalFaturaClientes = entity.calculateTotalFatura();
    }

    public Fatura_old_DTO(Fatura_OLD entity, Set<Client> clients){
        this(entity);
        clients.forEach(cli -> this.clientes.add(new ClientDTO(cli)));
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

    public Double getTotalFaturaClientes() {
        return totalFaturaClientes;
    }

    public void setTotalFaturaClientes(Double totalFaturaClientes) {
        this.totalFaturaClientes = totalFaturaClientes;
    }

    public List<ClientDTO> getClientes() {
        return clientes;
    }

    public void setClientes(List<ClientDTO> clientes) {
        this.clientes = clientes;
    }

    // Necessário escolher o cliente quando for cadastrar
}
