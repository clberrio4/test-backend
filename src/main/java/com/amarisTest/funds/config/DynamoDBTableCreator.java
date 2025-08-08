package com.amarisTest.funds.config;

import com.amarisTest.funds.model.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.waiters.WaiterParameters;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DynamoDBTableCreator {

    private final AmazonDynamoDB amazonDynamoDB;
    private final DynamoDBMapper dynamoDBMapper;


    @Value("${app.bootstrap.dynamo:false}")
    private boolean bootstrap;
    @Value("${app.bootstrap.seed:false}")
    private boolean seedDemoData;

    private static final List<Class<?>> DYNAMO_ENTITIES = List.of(
            Client.class,
            Transaction.class,
            Fund.class
    );

    @PostConstruct
    public void createTablesIfNotExist() {
        if (!bootstrap) {
            System.out.println("[Dynamo] bootstrap desactivado (app.bootstrap.dynamo=false). Omitiendo creación de tablas.");
            return;
        }

        Set<String> existingTables = new HashSet<>(amazonDynamoDB.listTables().getTableNames());

        for (Class<?> entityClass : DYNAMO_ENTITIES) {
            String tableName = getTableNameFromAnnotation(entityClass);
            if (!existingTables.contains(tableName)) {
                CreateTableRequest request = dynamoDBMapper.generateCreateTableRequest(entityClass);


                request.withBillingMode(BillingMode.PAY_PER_REQUEST);
                request.setProvisionedThroughput(null);

                amazonDynamoDB.createTable(request);
                System.out.println("[Dynamo] Tabla creada: " + tableName);

                waitForActive(tableName);
            } else {
                waitForActive(tableName);
            }
        }

        if (seedDemoData) {
            loadFunds();
            loadClients();
        } else {
            System.out.println("[Dynamo] seeding desactivado (app.bootstrap.seed=false).");
        }
    }

    private void waitForActive(String tableName) {
        try {
            amazonDynamoDB.waiters()
                    .tableExists()
                    .run(new WaiterParameters<>(new DescribeTableRequest().withTableName(tableName)));
        } catch (Exception e) {
            System.err.println("[Dynamo] Error esperando ACTIVE para " + tableName + ": " + e.getMessage());
        }
    }

    private String getTableNameFromAnnotation(Class<?> clazz) {
        DynamoDBTable annotation = clazz.getAnnotation(DynamoDBTable.class);
        if (annotation != null) return annotation.tableName();
        throw new IllegalArgumentException("La clase " + clazz.getSimpleName() + " no tiene @DynamoDBTable");
    }

    private void loadFunds() {
        try {
            List<Fund> existingFunds = dynamoDBMapper.scan(Fund.class, new DynamoDBScanExpression());
            if (existingFunds.isEmpty()) {
                System.out.println("[Dynamo] Precargando fondos iniciales...");
                List<Fund> funds = List.of(
                        new Fund("FPV_EL CLIENTE_RECAUDADORA", 75000, "FPV"),
                        new Fund("FPV_EL CLIENTE_ECOPETROL", 125000, "FPV"),
                        new Fund("DEUDAPRIVADA", 50000, "FIC"),
                        new Fund("FDO-ACCIONES", 250000, "FIC"),
                        new Fund("FPV_EL CLIENTE_DINAMICA", 100000, "FPV")
                );
                for (Fund fund : funds) dynamoDBMapper.save(fund);
                System.out.println("[Dynamo] Fondos precargados.");
            }
        } catch (Exception e) {
            System.err.println("[Dynamo] Error al precargar fondos: " + e.getMessage());
        }
    }

    private void loadClients() {
        try {
            List<Client> existing = dynamoDBMapper.scan(Client.class, new DynamoDBScanExpression());
            if (existing.isEmpty()) {
                System.out.println("[Dynamo] Precargando clientes iniciales...");
                List<Client> clients = List.of(
                        new Client("", "Cesar", "Berrio", "Medelin", "cesar693berrio@yopmail.com", new BigDecimal(500000)),
                        new Client("", "Carlos", "Rodriguez", "Santiago", "carlos6534@yopmail.com", new BigDecimal(500000)),
                        new Client("", "Miguel", "Lincoln", "Medelin", "lincolnmiguel001@yopmail.com", new BigDecimal(500000)),
                        new Client("", "Pedro", "Flores", "Ciudad de Mexico", "pedroflores4@yopmail.com", new BigDecimal(500000)),
                        new Client("", "Mary", "Gallego", "Paris", "mary6593@yopmail.com", new BigDecimal(500000)),
                        new Client("", "Sara", "Rico", "Gdansk", "clberrio4@yopmail.com", new BigDecimal(500000))
                );
                for (Client c : clients) dynamoDBMapper.save(c);
                System.out.println("[Dynamo] Clientes precargados.");
            }
        } catch (Exception e) {
            System.err.println("[Dynamo] Error al precargar clientes: " + e.getMessage());
        }
    }
}
