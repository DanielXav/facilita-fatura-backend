package com.danielxavier.FacilitaFatura.tests;

import com.danielxavier.FacilitaFatura.dto.ClientDTO;
import com.danielxavier.FacilitaFatura.entities.Client;
import com.danielxavier.FacilitaFatura.entities.InvoiceItem;

import java.time.LocalDate;

public class Factory {

    public static Client createClient() {
        Client client = new Client(4L, "Fulano", 1200.00);
        return client;
    }

}
