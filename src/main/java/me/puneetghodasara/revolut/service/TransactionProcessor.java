package me.puneetghodasara.revolut.service;

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
 *     |                            </br>
 *     -> ERROR                     </br>
 *                                  </br>
 * </br>
 */
public class TransactionProcessor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);

    private final TransactionBroker transactionBroker;

    private final AccountService accountService;


    public TransactionProcessor(final TransactionBroker transactionBroker,
                                final AccountService accountService) {
        this.transactionBroker = transactionBroker;
        this.accountService = accountService;
    }

    @Override
    public void run() {
        transactionBroker.getNextTransaction()
                .ifPresent(transaction -> {
                    final String sourceAccount = transaction.getSourceAccount();
                    final String targetAccount = transaction.getTargetAccount();
                    final Double amount = transaction.getAmount();

                    if(transaction.getTransactionStatus() == TransactionStatus.NEW) {
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

                    } else if (transaction.getTransactionStatus() == TransactionStatus.DEBIT_SUCCESS){
                        logger.debug("Source Account {} was already debited with amount {}", sourceAccount, amount);
                    }

                    try {
                        if(!accountService.credit(targetAccount, amount)){
                            throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.CREDIT_FAILED);
                        }
                    } catch (AccountOperationException e) {
                        // Could not credit, keep transaction in DEBIT_SUCCESS status and put it to retry
                        logger.info("Failed to credit Target Account {} with amount {}", targetAccount, amount);
                        transactionBroker.addTransaction(transaction);
                        return;
                    }
                    transaction.updateTransactionStatus(TransactionStatus.SUCCESS, "");
                    logger.info("Target Account {} credited with amount {}", targetAccount, amount);
                });
    }
}
