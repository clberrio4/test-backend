package com.amarisTest.funds.config;


import com.amarisTest.funds.model.Client;
import com.amarisTest.funds.model.Fund;
import com.amarisTest.funds.model.Transaction;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DynamoDBTableCreatorTest {

    private AmazonDynamoDB amazonDynamoDB;
    private DynamoDBMapper dynamoDBMapper;
    private DynamoDBTableCreator tableCreator;

    @BeforeEach
    void setUp() {
        amazonDynamoDB = mock(AmazonDynamoDB.class);
        dynamoDBMapper = mock(DynamoDBMapper.class);
        tableCreator = new DynamoDBTableCreator(amazonDynamoDB, dynamoDBMapper);
    }

    @Test
    void shouldCreateTablesWhenTheyDoNotExist() {

        when(amazonDynamoDB.listTables())
                .thenReturn(new ListTablesResult().withTableNames(Collections.emptyList()));

        when(dynamoDBMapper.generateCreateTableRequest(any()))
                .thenAnswer(invocation -> {
                    Class<?> clazz = invocation.getArgument(0);
                    return new CreateTableRequest()
                            .withTableName(clazz.getSimpleName())
                            .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
                });

        @SuppressWarnings("unchecked")
        PaginatedScanList<Fund> mockFundList = mock(PaginatedScanList.class);
        when(mockFundList.isEmpty()).thenReturn(true);

        @SuppressWarnings("unchecked")
        PaginatedScanList<Client> mockClientList = mock(PaginatedScanList.class);
        when(mockClientList.isEmpty()).thenReturn(true);

        when(dynamoDBMapper.scan(eq(Fund.class), any(DynamoDBScanExpression.class)))
                .thenReturn(mockFundList);
        when(dynamoDBMapper.scan(eq(Client.class), any(DynamoDBScanExpression.class)))
                .thenReturn(mockClientList);

        tableCreator.createTablesIfNotExist();

        verify(amazonDynamoDB, times(3)).createTable(any(CreateTableRequest.class));

        verify(dynamoDBMapper, atLeast(5)).save(isA(Fund.class));

        verify(dynamoDBMapper, atLeast(6)).save(isA(Client.class));
    }

    @Test
    void shouldNotCreateTablesIfTheyAlreadyExist() {
        String clientTable = Client.class.getAnnotation(DynamoDBTable.class).tableName();
        String transactionTable = Transaction.class.getAnnotation(DynamoDBTable.class).tableName();
        String fundTable = Fund.class.getAnnotation(DynamoDBTable.class).tableName();

        when(amazonDynamoDB.listTables())
                .thenReturn(new ListTablesResult().withTableNames(clientTable, transactionTable, fundTable));

        when(dynamoDBMapper.generateCreateTableRequest(any(Class.class)))
                .thenAnswer(invocation -> {
                    Class<?> clazz = invocation.getArgument(0, Class.class);
                    return new CreateTableRequest().withTableName(clazz.getSimpleName());
                });

        @SuppressWarnings("unchecked")
        PaginatedScanList<Fund> mockFundList = mock(PaginatedScanList.class);
        when(mockFundList.isEmpty()).thenReturn(false);

        @SuppressWarnings("unchecked")
        PaginatedScanList<Client> mockClientList = mock(PaginatedScanList.class);
        when(mockClientList.isEmpty()).thenReturn(false);

        when(dynamoDBMapper.scan(eq(Fund.class), any(DynamoDBScanExpression.class)))
                .thenReturn(mockFundList);
        when(dynamoDBMapper.scan(eq(Client.class), any(DynamoDBScanExpression.class)))
                .thenReturn(mockClientList);

        tableCreator.createTablesIfNotExist();

        verify(amazonDynamoDB, never()).createTable(any(CreateTableRequest.class));
        verify(dynamoDBMapper, never()).save(isA(Fund.class));
        verify(dynamoDBMapper, never()).save(isA(Client.class));
    }


    @Test
    void shouldThrowExceptionIfEntityDoesNotHaveDynamoDBTableAnnotation() throws Exception {
        class FakeEntity {}

        var method = tableCreator.getClass()
                .getDeclaredMethod("getTableNameFromAnnotation", Class.class);
        method.setAccessible(true);

        try {
            method.invoke(tableCreator, FakeEntity.class);
        } catch (Exception e) {
            assertThat(e.getCause())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no tiene la anotación @DynamoDBTable");
        }
    }
}