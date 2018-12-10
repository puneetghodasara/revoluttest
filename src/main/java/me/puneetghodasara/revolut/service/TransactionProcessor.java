package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.entity.Transaction;
import me.puneetghodasara.revolut.entity.TransactionStatus;
import me.puneetghodasara.revolut.exception.AccountOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is very simplified transactionProcessor that </br>
 * takes transaction from {@link TransactionBroker} </br>
 * and processes it </br>
 * </br>
 * For more productive scenario, this can be scaled by putting </br>
 * instances of this class to any ThreadPool </br>
 * </br>
 * </br>
 * This Follows a following automata of {@link TransactionStatus} </br>
 * NEW -> DEBIT_SUCCESS -> SUCCESS  </br>
 * |                            </br>
 * -> ERROR                     </br>
 * </br>
 * </br>
 */
public class TransactionProcessor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);

    private final TransactionBroker transactionBroker;

    private final AccountService accountService;

    private final ConversionService conversionService;
    private final boolean shutdownIfNoTx;

    static final int MAX_TRY = 100;

    // For JUnit only
    TransactionProcessor(final TransactionBroker transactionBroker,
                                final AccountService accountService,
                                final ConversionService conversionService,
                                boolean shutdownIfNoTx) {
        this.transactionBroker = transactionBroker;
        this.accountService = accountService;
        this.conversionService = conversionService;
        this.shutdownIfNoTx = shutdownIfNoTx;
    }

    public TransactionProcessor(final TransactionBroker transactionBroker,
                                final AccountService accountService,
                                final ConversionService conversionService) {
        this(transactionBroker, accountService, conversionService, false);
    }

    @Override
    public void run() {
        try {
            while(true) {
                if(!transactionBroker.hasNext() && shutdownIfNoTx){
                    break;
                }
                transactionBroker.getNextTransaction()
                        .ifPresent(this::processTransaction);
            }
        } catch (InterruptedException e) {
            logger.error("Error getting next transaction.");
        }
    }

    private void processTransaction(final Transaction transaction) {
        final String sourceAccount = transaction.getSourceAccount();
        final String targetAccount = transaction.getTargetAccount();
        final AccountEntity sourceAccountEntity;
        final AccountEntity targetAccountEntity;
        try {
            sourceAccountEntity = accountService.getAccount(sourceAccount)
                    .orElseThrow(() -> new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.UNKNOWN_ACCOUNT));
            targetAccountEntity = accountService.getAccount(targetAccount)
                    .orElseThrow(() -> new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.UNKNOWN_ACCOUNT));
        } catch (AccountOperationException e) {
            logger.error("Source or Target Account are invalid.");
            return;
        }
        final Double amount = transaction.getAmount();

        if (transaction.getTransactionStatus() == TransactionStatus.NEW) {
            logger.debug("Source Account {} will be debited with amount {}", sourceAccount, amount);
            try {
                if (!accountService.debit(sourceAccount, amount)) {
                    throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.DEBIT_FAILED);
                }
            } catch (AccountOperationException e) {
                // Could not debit, FAIL the transaction
                logger.info("Failed to debit Source Account {} with amount {}", targetAccount, amount);
                transaction.updateTransactionStatus(TransactionStatus.ERROR, e.getMessage());
                return;
            }
            transaction.updateTransactionStatus(TransactionStatus.DEBIT_SUCCESS, "");
            logger.info("Source Account {} debited with amount {}", sourceAccount, amount);

        } else if (transaction.getTransactionStatus() == TransactionStatus.DEBIT_SUCCESS && transaction.getAttempted() == MAX_TRY) {
            // Last Try hence try to refund
            logger.info("Source Account {} will be refunded with amount {}", sourceAccount, amount);
            try {
                if(!accountService.credit(sourceAccount, amount)){
                    throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.CREDIT_FAILED);
                }
            } catch (AccountOperationException e) {
                // It was best effort.
                logger.info("Source Account {} NOT refunded with amount {}", sourceAccount, amount);
                transaction.updateTransactionStatus(TransactionStatus.ERROR, "Maximum attempt reached");
                return;
            }
            transaction.updateTransactionStatus(TransactionStatus.SUCCESS, "");
            logger.info("Source Account {} refunded with amount {}", sourceAccount, amount);
            return;
        }

        // Here, convert the money
        Double targetAmount = conversionService.convert(sourceAccountEntity.getCurrency(), targetAccountEntity.getCurrency(), amount);

        try {
            if (!accountService.credit(targetAccount, targetAmount)) {
                throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.CREDIT_FAILED);
            }
        } catch (AccountOperationException e) {
            // Could not credit, keep transaction in DEBIT_SUCCESS status and put it to retry
            logger.info("Failed to credit Target Account {} with amount {}", targetAccount, amount);
            transaction.updateAttempted();

//            if(transaction.getAttempted() < MAX_TRY){
                // Add only if we are not run out of attempts
                transactionBroker.addTransaction(transaction);
//            } else {
////                transaction.updateTransactionStatus(TransactionStatus.ERROR, "Tried Maximum Attempts");
//                logger.info("Target Account {} NOT credited with amount {}", targetAccount, amount);
//            }
            return;
        }
        transaction.updateTransactionStatus(TransactionStatus.SUCCESS, "");
        logger.info("Target Account {} credited with amount {}", targetAccount, amount);
    }
}
