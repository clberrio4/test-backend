package com.amarisTest.funds.facade;

import com.amarisTest.funds.dto.generic.RestGenericData;
import com.amarisTest.funds.dto.generic.ClientSoftDataDto;
import com.amarisTest.funds.model.Client;
import com.amarisTest.funds.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientFacade {
    private  final ClientService clientService;


    public RestGenericData<List<Client>> getAllClients() {
        return new RestGenericData<>(clientService.getAll());
    }

    public RestGenericData<Client> getClientById(String id) {
        return new RestGenericData<>(clientService.getById(id));
    }

    public RestGenericData<Client> updateSoftData(String clientId, RestGenericData<ClientSoftDataDto> input) {
        return new RestGenericData<>(clientService.updateSoftData(clientId, input.getData().getBalance(),input.getData().getEmail()));
    }
}