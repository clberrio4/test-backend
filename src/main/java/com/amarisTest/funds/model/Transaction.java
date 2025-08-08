package com.amarisTest.funds.model;

import com.amarisTest.funds.helpers.DynamoDBDateConverter;
import com.amarisTest.funds.model.enumField.TransactionType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@DynamoDBTable(tableName = "transacciones")
public class Transaction extends BaseModel {

    @Override
    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return super.getId();
    }

    @Override
    public void setId(String id) {
        super.setId(id);
    }

    @DynamoDBAttribute(attributeName = "cliente_id")
    private String customerId;

    @DynamoDBAttribute(attributeName = "fondo_id")
    private String fundId;

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "estado")
    private TransactionType status;

    @DynamoDBAttribute(attributeName = "usuario")
    private String performedBy;

    @DynamoDBAttribute(attributeName = "monto")
    private BigDecimal amount;

    @DynamoDBTypeConverted(converter = DynamoDBDateConverter.class)
    @DynamoDBAttribute(attributeName = "fecha_creacion")
    private DateTime createdAt;

    @DynamoDBTypeConverted(converter = DynamoDBDateConverter.class)
    @DynamoDBAttribute(attributeName = "fecha_actualizacion")
    private DateTime updatedAt;


}