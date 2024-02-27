package com.danielxavier.FacilitaFatura.dto;

import com.danielxavier.FacilitaFatura.entities.Client;

public class ClientDTO {

    private Long id;
    private String name;
    private Double total;

    public ClientDTO(){
    }

    public ClientDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ClientDTO(Client entity){
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
