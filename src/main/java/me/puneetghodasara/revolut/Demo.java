package me.puneetghodasara.revolut;

import me.puneetghodasara.revolut.dao.*;
import me.puneetghodasara.revolut.endpoint.TransactionEndpoint;
import me.puneetghodasara.revolut.endpoint.TransactionEndpointImpl;
import me.puneetghodasara.revolut.endpoint.UserAccountEndpoint;
import me.puneetghodasara.revolut.endpoint.UserAccountEndpointImpl;
import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.exception.AccountOperationException;
import me.puneetghodasara.revolut.exception.TransactionException;
import me.puneetghodasara.revolut.exception.UserOperationException;
import me.puneetghodasara.revolut.model.AccountModel;
import me.puneetghodasara.revolut.model.TransactionReponse;
import me.puneetghodasara.revolut.model.UserModel;
import me.puneetghodasara.revolut.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Demo {

    private static final Logger logger = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) throws UserOperationException, AccountOperationException, TransactionException, InterruptedException {

        // Persistence Layer beans
        final AccountRepository accountRepository = new AccountInMemoryDao();
        final UserRepository userRepository = new UserInMemoryDao();
        final TransactionRepository transactionRepository = new TransactionInMemoryDao();

        // Service Layer beans
        final AccountNumberService accountNumberService = new AccountNumberServiceImpl();

        final AccountService accountService = new AccountServiceImpl(accountRepository, accountNumberService);
        final UserService userService = new UserServiceImpl(userRepository, accountService);

        final TransactionBroker broker = new TransactionQueueBroker();

        final SimpleConversionServiceImpl conversionService = new SimpleConversionServiceImpl();

        // Endpoint Layer beans (REST-like API)
        final UserAccountEndpoint userAccountEndpoint = new UserAccountEndpointImpl(userService);
        final TransactionEndpoint transactionEndpoint = new TransactionEndpointImpl(broker, transactionRepository);


        final UserModel user1 = userAccountEndpoint.createUser("Revolut Test User");
        final UserModel user2 = userAccountEndpoint.createUser("Revolut Stage User");

        final AccountModel account1 = userAccountEndpoint.openNewAccount(user1.getUserId(), "EUR");
        final AccountModel account2 = userAccountEndpoint.openNewAccount(user1.getUserId(), "USD");
        final AccountModel account3 = userAccountEndpoint.openNewAccount(user2.getUserId(), "EUR");

        // Can not transact with zero balances, hence putting a hack to provide 30 Euro to first account
        accountRepository.updateEntity(account1.getAccountId(), new AccountEntity(account1.getAccountId(), Currency.getInstance("EUR")).withNewAmount(30d));

        final ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(new TransactionProcessor(broker, accountService, conversionService));
        // (Though executorService will be shutdown when program ends it is good idea to)
        // Add shutdown hook to shutdown executorService
        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));


        /**
         * Scenario 1 : Transferring 10 Euro from account-1 to account-3
         */
        logger.info("All Accounts");
        printAllAmounts(accountRepository);

        logger.info("");
        logger.info("Scenario 1 [Positive]");
        logger.info("Transferring 10 Euro from account-1 to account-3");
        final String transactionId = transactionEndpoint.transact(account1.getAccountId(), account3.getAccountId(), 10D, "My First Transaction");
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        final TransactionReponse transactionReponse1 = transactionEndpoint.getStatus(transactionId);
        printStatus(transactionReponse1);
        printAllAmounts(accountRepository);


        logger.info("");
        logger.info("Scenario 2 [Insufficient Balance]");
        logger.info("Transferring 100 Euro from account-1 to account-3");
        final String transactionId2 = transactionEndpoint.transact(account1.getAccountId(), account3.getAccountId(), 100D, "My First Transaction");
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        final TransactionReponse transactionReponse2 = transactionEndpoint.getStatus(transactionId2);
        printStatus(transactionReponse2);
        printAllAmounts(accountRepository);


        logger.info("");
        logger.info("Scenario 3 [Coversion]");
        logger.info("Transferring 10 Euro from account-1 to account-2");
        final String transactionId3 = transactionEndpoint.transact(account1.getAccountId(), account2.getAccountId(), 10D, "My First Transaction");
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        final TransactionReponse transactionReponse3 = transactionEndpoint.getStatus(transactionId3);
        printStatus(transactionReponse3);
        printAllAmounts(accountRepository);

        logger.info("Press ctrl+c to close");

    }

    private static void printStatus(final TransactionReponse tx) {
        logger.info(String.format("[%7s | %20s]", tx.getTransactionStatus().name(), tx.getTransactionStatusMessage()));
    }

    private static void printAllAmounts(final AccountRepository accountRepository) {
        final String header = String.format("|%10.15s|%10.8s|%10.7s|", "Account", "Currency", "Amount");
        logger.info(header);
        accountRepository.getAll().forEach(accountEntity -> {
            final String aLine = String.format("|%10.15s|%10.5s|%10.5s|", accountEntity.getAccountId(), accountEntity.getCurrency(), accountEntity.getAmount());
            logger.info(aLine);
        });
    }


}
