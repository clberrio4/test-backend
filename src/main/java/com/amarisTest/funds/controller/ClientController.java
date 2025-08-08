package com.amarisTest.funds.controller;

import com.amarisTest.funds.dto.generic.RestGenericData;
import com.amarisTest.funds.dto.generic.ClientSoftDataDto;
import com.amarisTest.funds.facade.ClientFacade;
import com.amarisTest.funds.model.Client;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    @Autowired
    private ClientFacade clientFacade;

    @GetMapping
    public RestGenericData<List<Client>> getAllClients() {
        return clientFacade.getAllClients();
    }

    @GetMapping("/{id}")
    public RestGenericData<Client> getClientById(@PathVariable String id) {
        return clientFacade.getClientById(id);
    }

    @PatchMapping("/{id}/pocket")
    public RestGenericData<Client> updateSoftData(@PathVariable String id, @Valid @RequestBody RestGenericData<ClientSoftDataDto> input ) {
        return clientFacade.updateSoftData(id, input);
    }
}