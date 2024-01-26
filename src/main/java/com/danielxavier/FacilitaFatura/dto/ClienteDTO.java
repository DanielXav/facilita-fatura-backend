package com.danielxavier.FacilitaFatura.dto;

import com.danielxavier.FacilitaFatura.entities.Cliente;

public class ClienteDTO {

    private Long id;
    private String name;
    private Double total;

    public ClienteDTO(){
    }

    public ClienteDTO(Long id, String name, Double total) {
        this.id = id;
        this.name = name;
        this.total = total;
    }

    public ClienteDTO(Cliente entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.total = entity.getTotal();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
