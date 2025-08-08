package com.amarisTest.funds.facade;

import com.amarisTest.funds.dto.generic.RestGenericData;
import com.amarisTest.funds.dto.generic.TransactionDto;
import com.amarisTest.funds.dto.generic.TransactionWithClientDTO;
import com.amarisTest.funds.helpers.errorHandler.BusinessException;
import com.amarisTest.funds.helpers.facade.TransactSaveTransactionAndClientHelper;
import com.amarisTest.funds.model.Client;
import com.amarisTest.funds.model.Fund;
import com.amarisTest.funds.model.Transaction;
import com.amarisTest.funds.model.enumField.OrderQuery;
import com.amarisTest.funds.model.enumField.TransactionType;
import com.amarisTest.funds.service.ClientService;
import com.amarisTest.funds.service.EmailService;
import com.amarisTest.funds.service.FundService;
import com.amarisTest.funds.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.amarisTest.funds.mapper.TransactionMapper.fromDto;

@Service
@RequiredArgsConstructor
public class TransactionFacade {
    private  final EmailService emailService;
    private final TransactionService transactionService;
    private final FundService fundService;
    private final ClientService clientService;
    private final TransactSaveTransactionAndClientHelper helperTrx;

    public RestGenericData<Transaction> createTransaction(RestGenericData<TransactionDto> input) {
        Transaction transaction = fromDto(input.getData());

        Client client = clientService.getById(transaction.getCustomerId());
        Fund fund = fundService.getById(transaction.getFundId());

        if (transaction.getStatus() == TransactionType.SUBSCRIBE) {
            boolean subscribed = transactionService.hasActiveSubscription(
                    transaction.getCustomerId(),
                    transaction.getFundId()
            );



            if (subscribed) {
                emailService.sendFormattedEmailToClient(client,
                        "Estuviste intentando subscribirte a %s pero ha ocurrido un error",
                        fund.getName());

                throw new BusinessException("Ya existe una suscripción activa para el fondo: " + fund.getName(), 400);
            }
            if (client.getBalance().compareTo(fund.getMinAmount()) < 0) {
                emailService.sendFormattedEmailToClient(client,
                        "Estuviste intentando subscribirte a %s pero ha ocurrido un error",
                        fund.getName());
                throw new BusinessException("No tiene saldo disponible para vincularse al fondo " + fund.getName(),400);
            }

            transaction.setAmount(fund.getMinAmount());
            client.setBalance(client.getBalance().subtract(fund.getMinAmount()));
        }

        transaction.ensureId();
        transaction.setUpdatedAt(DateTime.now());

        helperTrx.transactSaveTransactionAndClient(transaction, client);
        emailService.sendFormattedEmailToClient(
                client,
                "Te has suscrito al fondo %s por %.2f, por ende se ha descontado de tu capital",
                fund.getName(),
                fund.getMinAmount().doubleValue()
        );
        return new RestGenericData<>(transaction);
    }

    public RestGenericData<Transaction> cancelTransaction(String trxId ) {

        Transaction originalTransaction = transactionService.getTransactionById(trxId);
        Client client = clientService.getById(originalTransaction.getCustomerId());
        Fund fund = fundService.getById(originalTransaction.getFundId());

        if (originalTransaction.getStatus() != TransactionType.SUBSCRIBE) {
            emailService.sendFormattedEmailToClient(client,
                    "Has intentado cancelar la suscripción al fondo %s pero ha ocurrido un problema",
                    fund.getName());
            throw new BusinessException("Solo se pueden cancelar transacciones activas",400);
        }

        Transaction cancelTransaction = new Transaction();
        cancelTransaction.setId(originalTransaction.getId());
        cancelTransaction.setCustomerId(originalTransaction.getCustomerId());
        cancelTransaction.setFundId(originalTransaction.getFundId());
        cancelTransaction.setCreatedAt(DateTime.now());
        cancelTransaction.setStatus(TransactionType.UNSUBSCRIBE);
        cancelTransaction.setAmount(originalTransaction.getAmount());

        client.setBalance(client.getBalance().add(originalTransaction.getAmount()));

        helperTrx.transactCancelTransactionAndRestoreClient(cancelTransaction, client);
        emailService.sendFormattedEmailToClient(client,
                "Has cancelado la suscripción al fondo %s, por ende se te ha hecho una reversión en tu wallet",
                fund.getName());

        return new RestGenericData<>(cancelTransaction);
    }

    public RestGenericData<List<Transaction>> getAll() {
        return new RestGenericData<>(transactionService.getAll());
    }

    public RestGenericData<Transaction> getTransactionById(String id) {
        return new RestGenericData<>(transactionService.getTransactionById(id));
    }

    public RestGenericData<List<Transaction>> getByCustomerId(String customerId, TransactionType status) {
        return new RestGenericData<>(transactionService.getByCustomerIdAndStatus(customerId, status));
    }

    public RestGenericData<List<TransactionWithClientDTO>> getTransactionsWithClientInfo(TransactionType type, String customerId, String fundId, OrderQuery order){
        return new RestGenericData<>(transactionService.getTransactionsWithClientInfo(type,customerId,fundId,order,transactionService.getAll(),clientService.getAll(),fundService.getAll()));
    }
}