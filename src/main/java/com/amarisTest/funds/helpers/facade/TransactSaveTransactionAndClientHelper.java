package com.amarisTest.funds.helpers.facade;

import com.amarisTest.funds.helpers.DynamoDBDateConverter;
import com.amarisTest.funds.model.Client;
import com.amarisTest.funds.model.Transaction;
import com.amarisTest.funds.model.enumField.TransactionType;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperTableModel;
import com.amazonaws.services.dynamodbv2.model.*;

import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Component
public class TransactSaveTransactionAndClientHelper {

    private final AmazonDynamoDB amazonDynamoDB;
    private final DynamoDBMapper dynamoDBMapper;

    public void transactSaveTransactionAndClient(Transaction transaction, Client client) {

        DynamoDBMapperTableModel<Transaction> transactionModel = (DynamoDBMapperTableModel<Transaction>) dynamoDBMapper.getTableModel(Transaction.class);
        Map<String, AttributeValue> transactionItem = transactionModel.convert(transaction);

        Map<String, AttributeValue> clientKey = new HashMap<>();
        clientKey.put("id", new AttributeValue(client.getId()));

        Map<String, AttributeValue> clientUpdateValues = new HashMap<>();
        clientUpdateValues.put(":saldo", new AttributeValue().withN(client.getBalance().toPlainString()));

        TransactWriteItemsRequest request = new TransactWriteItemsRequest()
                .withTransactItems(
                        Arrays.asList(
                                new TransactWriteItem().withPut(new Put()
                                        .withTableName("transacciones")
                                        .withItem(transactionItem)
                                ),
                                new TransactWriteItem().withUpdate(new Update()
                                        .withTableName("clientes")
                                        .withKey(clientKey)
                                        .withUpdateExpression("SET saldo = :saldo")
                                        .withExpressionAttributeValues(clientUpdateValues)
                                )
                        )
                );

        try {
            amazonDynamoDB.transactWriteItems(request);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar transacción y actualizar balance: " + e.getMessage(), e);
        }
    }

    public void transactCancelTransactionAndRestoreClient(Transaction cancelTransaction, Client clientWithPreviousBalance) {

        Map<String, AttributeValue> clientKey = new HashMap<>();
        clientKey.put("id", new AttributeValue(clientWithPreviousBalance.getId()));

        Map<String, AttributeValue> trxKey = new HashMap<>();
        trxKey.put("id", new AttributeValue(cancelTransaction.getId()));

        Map<String, AttributeValue> clientUpdateValues = new HashMap<>();
        clientUpdateValues.put(":saldo", new AttributeValue().withN(clientWithPreviousBalance.getBalance().toPlainString()));


        Map<String, AttributeValue> estadoUpdateValues = new HashMap<>();
        estadoUpdateValues.put(":estado", new AttributeValue().withS(TransactionType.UNSUBSCRIBE.name()));
        estadoUpdateValues.put(":fecha_actualizacion", new AttributeValue().withS( new DynamoDBDateConverter().convert(DateTime.now())));

        TransactWriteItemsRequest request = new TransactWriteItemsRequest()
                .withTransactItems(
                        Arrays.asList(
                                new TransactWriteItem().withUpdate(new Update()
                                        .withTableName("transacciones")
                                        .withKey(trxKey)
                                        .withUpdateExpression("SET estado = :estado, fecha_actualizacion = :fecha_actualizacion")
                                        .withExpressionAttributeValues(estadoUpdateValues)
                                ),
                                new TransactWriteItem().withUpdate(new Update()
                                        .withTableName("clientes")
                                        .withKey(clientKey)
                                        .withUpdateExpression("SET saldo = :saldo")
                                        .withExpressionAttributeValues(clientUpdateValues)
                                )
                        )
                );

        try {
            amazonDynamoDB.transactWriteItems(request);
        } catch (Exception e) {
            throw new RuntimeException("Error al cancelar transacción y restaurar saldo: " + e.getMessage(), e);
        }
    }
}


