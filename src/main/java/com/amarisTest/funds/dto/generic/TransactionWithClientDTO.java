package com.amarisTest.funds.dto.generic;

import com.amarisTest.funds.model.enumField.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionWithClientDTO {
    private String transactionId;
    private String customerId;
    private String firstName;
    private String lastName;
    private String fundId;
    private String fundName;
    private TransactionType status;
    private BigDecimal amount;
    private DateTime createdAt;
    private DateTime updatedAt;
}