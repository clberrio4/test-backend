package com.amarisTest.funds.service;

import com.amarisTest.funds.helpers.errorHandler.NotFoundException;
import com.amarisTest.funds.model.Client;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final DynamoDBMapper dynamoDBMapper;

    public Client getById(String clientId) {
        Client client = dynamoDBMapper.load(Client.class, clientId);
        if (client == null) {
            throw new NotFoundException("Client with id " + clientId + " not found");
        }
        return client;
    }

    public List<Client> getAll() {
        return dynamoDBMapper.scan(Client.class, new DynamoDBScanExpression());
    }

    public Client updateSoftData(String clientId, BigDecimal money, String email) {
        Client client = getById(clientId);
        client.setBalance(client.getBalance().add(money));
        client.setEmail(email);
        dynamoDBMapper.save(client);
        return client;
    }
}
