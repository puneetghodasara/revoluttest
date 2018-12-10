package me.puneetghodasara.revolut.service;

import java.util.Currency;

/**
 * A service to convert amount between currencies
 */
public interface ConversionService {

    /**
     * It converts the equivalent amount in target currency
     * @param sourceCurrency
     * @param targetCurrency
     * @param amount
     * @return
     */
    Double convert(final Currency sourceCurrency, final Currency targetCurrency, final Double amount);
}
