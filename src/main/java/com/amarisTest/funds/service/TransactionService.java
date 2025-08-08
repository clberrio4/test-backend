package com.amarisTest.funds.service;

import com.amarisTest.funds.dto.generic.TransactionWithClientDTO;
import com.amarisTest.funds.helpers.errorHandler.NotFoundException;
import com.amarisTest.funds.model.Client;
import com.amarisTest.funds.model.Fund;
import com.amarisTest.funds.model.Transaction;
import com.amarisTest.funds.model.enumField.OrderQuery;
import com.amarisTest.funds.model.enumField.TransactionType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final DynamoDBMapper dynamoDBMapper;

    public Transaction createTransaction(Transaction input) {
        dynamoDBMapper.save(input);
        return input;
    }

    public List<Transaction> getAll() {
        return dynamoDBMapper.scan(Transaction.class, new DynamoDBScanExpression());
    }


    public Transaction getTransactionById(String id) {
        Transaction transaction = dynamoDBMapper.load(Transaction.class, id);
        if (transaction == null) {
            throw new NotFoundException("Transaction with id " + id + " not found");
        }
        return transaction;
    }

    public List<Transaction> getByCustomerIdAndStatus(String customerId, TransactionType status) {
            return dynamoDBMapper.scan(Transaction.class, new DynamoDBScanExpression())
                    .stream()
                    .filter(t -> customerId.equals(t.getCustomerId()) &&
                            (status == null || t.getStatus() == status))
                    .collect(Collectors.toList());
    }

    public List<TransactionWithClientDTO> getTransactionsWithClientInfo(
            TransactionType status,
            String customerId,
            String fundId,
            OrderQuery order,
            List<Transaction> transactions,
            List<Client> clients,
            List<Fund> funds
    ) {
        if (transactions == null || transactions.isEmpty()) {
            return Collections.emptyList();
        }

        List<TransactionWithClientDTO> result = transactions.stream()
                .filter(Objects::nonNull)
                .filter(tx -> status == null || status.equals(tx.getStatus()))
                .filter(tx -> customerId == null || customerId.equals(tx.getCustomerId()))
                .filter(tx -> fundId == null || fundId.equals(tx.getFundId()))
                .map(tx -> {
                    Client client = (clients != null && tx.getCustomerId() != null)
                            ? clients.stream()
                            .filter(Objects::nonNull)
                            .filter(c -> tx.getCustomerId().equals(c.getId()))
                            .findFirst()
                            .orElse(null)
                            : null;

                    Fund fund = (funds != null && tx.getFundId() != null)
                            ? funds.stream()
                            .filter(Objects::nonNull)
                            .filter(f -> tx.getFundId().equals(f.getId()))
                            .findFirst()
                            .orElse(null)
                            : null;

                    return TransactionWithClientDTO.builder()
                            .transactionId(tx.getId())
                            .customerId(tx.getCustomerId())
                            .firstName(client != null ? client.getName() : null)
                            .lastName(client != null ? client.getLastname() : null)
                            .fundId(tx.getFundId())
                            .fundName(fund != null ? fund.getName() : null)
                            .status(tx.getStatus())
                            .amount(tx.getAmount())
                            .createdAt(tx.getCreatedAt())
                            .updatedAt(tx.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        Comparator<TransactionWithClientDTO> comparator = TransactionType.UNSUBSCRIBE.equals(status)
                ? Comparator.comparing(TransactionWithClientDTO::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                : Comparator.comparing(TransactionWithClientDTO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));

        if (OrderQuery.DES.equals(order)) {
            comparator = comparator.reversed();
        }

        result.sort(comparator);
        return result;
    }


    public boolean hasActiveSubscription(String customerId, String fundId) {
        List<Transaction> transactions = dynamoDBMapper.scan(Transaction.class, new DynamoDBScanExpression())
                .stream()
                .filter(tx -> customerId.equals(tx.getCustomerId()) && fundId.equals(tx.getFundId()))
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .toList();

        return transactions.stream()
                .findFirst()
                .map(tx -> tx.getStatus() == TransactionType.SUBSCRIBE)
                .orElse(false);
    }
}