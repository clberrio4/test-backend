package com.amarisTest.funds.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@DynamoDBTable(tableName = "fondos")
public class Fund extends  BaseModel {
    @Override
    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return super.getId();
    }

    @Override
    public void setId(String id) {
        super.setId(id);
    }

    @DynamoDBAttribute(attributeName = "nombre")
    private String name;

    @DynamoDBAttribute(attributeName = "monto_minimo")
    private BigDecimal minAmount;

    @DynamoDBAttribute(attributeName = "categoria")
    private String category;

    public Fund(String name, int minAmount, String category) {
        this.ensureId();
        this.name = name;
        this.minAmount = BigDecimal.valueOf(minAmount);
        this.category = category;
    }
}