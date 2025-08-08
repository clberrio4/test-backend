package com.amarisTest.funds.service;

import com.amarisTest.funds.helpers.errorHandler.NotFoundException;
import com.amarisTest.funds.model.Fund;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FundService {
    private final DynamoDBMapper dynamoDBMapper;

    public Fund save(Fund fund) {
        dynamoDBMapper.save(fund);
        return fund;
    }

    public Fund getById(String fundId) {
        Fund fund = dynamoDBMapper.load(Fund.class, fundId);
        if (fund == null) {
            throw new NotFoundException("Fund with id " + fundId + " not found");
        }
        return fund;
    }

    public List<Fund> getAll() {
        return dynamoDBMapper.scan(Fund.class, new DynamoDBScanExpression());
    }

    public void delete(String fundId) {
        Fund fund = dynamoDBMapper.load(Fund.class, fundId);
        if (fund == null) {
            throw new NotFoundException("Fund with id " + fundId + " not found");
        }
        dynamoDBMapper.delete(fund);
    }
}
