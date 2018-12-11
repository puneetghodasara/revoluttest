# revoluttest

A sample code to transact an amount from a source account to a destination account.

## Overview
There are two REST-like endpoints.
- UserAccountEndpoint. It has following APIs,
    - GET /user
    - GET /user/{user-id}
    - POST /user
    - DEL /user/{user-id}
    - GET /user/{user-id}/account 
    - POST /user/{user-id}/account
    - GET /user/{user-id}/account/{account-id}
    - DEL /user/{user-id}/account/{account-id}
- TransactionEndpoint. It has following APIs
    - PUT /transaction
    - GET /transaction/{transaction-id}

A transaction request takes three parameters,
1) Source Account ID
2) Target Account ID
3) Amount to transfer

It creates a transaction request to a broker and returns the transaction ID to the caller. The caller needs to poll transaction ID to check the status of the transaction.

A simple BlockingQueue based implementation has been provided for this demonstration.
A Runnable TransactionProcessor is used for demonstration that watches the broker and process the transaction.

- Transaction would be marked as DEBIT_SUCCESS from NEW if debit operation is performed.
- if subsequent credit operation,
    - Fails, Transaction would be put back to broker 
    - Succeed, Transaction would be marked as SUCCESS
- Transaction would be tried for MAX_ATTEMPT times,
    - At attempt == MAX_ATTEMPT, source account would be refunded.
    - if refund was failed, a hard error would be pritned.

A simple hardcoded currency conversion service is used if accounts are maintained in different currencies.
###### Notes
- Transaction Processor is not crash-proof and consistency is not guaranteed in case of server restart.
- Records of transaction states must be maintain in a separate persistent system (e.g. File, DB etc) to solve above issue.

### Functionality Tests
TransactionProcessorTest unit tests the following scenarios.
- A simple transaction
- Recurring transactions
- A intra currency transaction
- Attempt to transact when amount is not sufficient to debit
- A refund case (by mocking where credit operation fails for n-1 attempt)
- An aborted transaction (by mocking where credit operation fails for all attempts)

### Demo File
Demo.java would demonstrate three of above functionality to showcase the simple working of code.
