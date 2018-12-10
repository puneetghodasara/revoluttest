package me.puneetghodasara.revolut.service;

import java.util.Currency;

/**
 * It converts amount across few currencies with hard coded values else throw excpetion </br>
 * </br>
 * For more productive use case, we can build cache-based implementation </br>
 * that takes values from third-party </br>
 */
public class SimpleConversionServiceImpl implements ConversionService {

    @Override
    public Double convert(final Currency sourceCurrency, final Currency targetCurrency, final Double amount) {
        if (sourceCurrency.getNumericCode() == targetCurrency.getNumericCode()) {
            return amount;
        }

        if(sourceCurrency.getNumericCode() == Currency.getInstance("USD").getNumericCode()
            && targetCurrency.getNumericCode() == Currency.getInstance("EUR").getNumericCode()){
            return 0.88 * amount;
        }

        if(sourceCurrency.getNumericCode() == Currency.getInstance("EUR").getNumericCode()
                && targetCurrency.getNumericCode() == Currency.getInstance("USD").getNumericCode()){
            return 1.14 * amount;
        }

        throw new IllegalStateException();
    }
}
