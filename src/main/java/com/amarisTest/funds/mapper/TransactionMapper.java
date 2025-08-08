package com.amarisTest.funds.mapper;

import com.amarisTest.funds.dto.generic.TransactionDto;
import com.amarisTest.funds.model.Transaction;
import org.joda.time.DateTime;

public class TransactionMapper {

    public static Transaction fromDto(TransactionDto dto) {
        Transaction trx = Transaction.builder()
                .customerId(dto.getCustomerId())
                .fundId(dto.getFundId())
                .status(dto.getStatus())
                .createdAt(DateTime.now())
                .build();
        trx.ensureId();
        return  trx;
    }
}