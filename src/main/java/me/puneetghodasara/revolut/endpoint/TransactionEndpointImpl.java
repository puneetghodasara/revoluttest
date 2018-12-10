package me.puneetghodasara.revolut.endpoint;

import me.puneetghodasara.revolut.dao.TransactionRepository;
import me.puneetghodasara.revolut.exception.TransactionException;
import me.puneetghodasara.revolut.entity.Transaction;
import me.puneetghodasara.revolut.model.TransactionReponse;
import me.puneetghodasara.revolut.service.TransactionBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionEndpointImpl implements TransactionEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(TransactionEndpointImpl.class);

    private final TransactionBroker transactionBroker;
    private final TransactionRepository transactionRepository;

    public TransactionEndpointImpl(final TransactionBroker transactionBroker, final TransactionRepository transactionRepository) {
        this.transactionBroker = transactionBroker;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public String transact(final String sourceAccount, final String targetAccount, final Double amount, final String message) throws TransactionException {
        final Transaction transaction = new Transaction.Builder(sourceAccount, targetAccount, amount).message(message).build();
        final boolean txAccepted = transactionBroker.addTransaction(transaction);
        if (!txAccepted) {
            logger.error("Unable to add transaction.");
            throw new TransactionException(TransactionException.TransactionExceptionMessages.FAIL_TO_ACCEPT);
        }
        transactionRepository.update(transaction);
        return transaction.getTransactionId();
    }

    @Override
    public TransactionReponse getStatus(final String transactionId) throws TransactionException {
        return transactionRepository.get(transactionId)
                .map(TransactionReponse::fromEntity)
                .orElseThrow(() -> new TransactionException(TransactionException.TransactionExceptionMessages.UNKNOWN_TRANSACTION_ID));
    }
}
