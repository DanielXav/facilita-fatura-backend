package com.danielxavier.FacilitaFatura.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
