package com.amarisTest.funds.controller;

import com.amarisTest.funds.dto.generic.RestGenericData;
import com.amarisTest.funds.dto.generic.TransactionDto;
import com.amarisTest.funds.dto.generic.TransactionWithClientDTO;
import com.amarisTest.funds.facade.TransactionFacade;
import com.amarisTest.funds.model.Transaction;
import com.amarisTest.funds.model.enumField.OrderQuery;
import com.amarisTest.funds.model.enumField.TransactionType;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionFacade transactionFacade;

    @PostMapping
    public RestGenericData<Transaction> createTransaction(@RequestBody RestGenericData<TransactionDto> input) {
        return transactionFacade.createTransaction(input);
    }

    @GetMapping
    public RestGenericData<List<Transaction>> getAllTransactions() {
        return transactionFacade.getAll();
    }

    @GetMapping("/{id}")
    public RestGenericData<Transaction> getTransactionById(@PathVariable String id) {
        return transactionFacade.getTransactionById(id);
    }

    @PostMapping("/{id}/cancel")
    public RestGenericData<Transaction> cancelTransactionById(@PathVariable String id) {
        return transactionFacade.cancelTransaction(id);
    }

    @GetMapping("/customer/{customerId}")
    public RestGenericData<List<Transaction>> getByCustomerId(
            @PathVariable String customerId,
            @RequestParam(required = false) TransactionType status
    ) {
        return transactionFacade.getByCustomerId(customerId, status);
    }
    @GetMapping("/filter")
    public RestGenericData<List<TransactionWithClientDTO>> getTransactionsWithClientInfo(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String fundId,
            @RequestParam(required = false) TransactionType status,
            @RequestParam(required = false) OrderQuery order
    ) {
        return transactionFacade.getTransactionsWithClientInfo(status,customerId, fundId,order);
    }
}
