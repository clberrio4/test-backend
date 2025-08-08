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
@DynamoDBTable(tableName = "clientes")
public class Client extends  BaseModel {
    public Client(String _gen, String name, String lastname, String city, String mail, BigDecimal money) {
        super();
        this.ensureId();
        this.name=name;
        this.lastname=lastname;
        this.city=city;
        this.email=mail;
        this.balance=money;
    }

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

    @DynamoDBAttribute(attributeName = "apellido")
    private String lastname;

    @DynamoDBAttribute(attributeName = "ciudad")
    private String city;

    @DynamoDBAttribute(attributeName = "correo")
    private String email;

    @DynamoDBAttribute(attributeName = "saldo")
    private BigDecimal balance;
}
