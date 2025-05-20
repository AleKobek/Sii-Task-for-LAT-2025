package edu.pja.sii_lat.service;

import java.math.BigDecimal;
import java.util.Set;

public interface IExchangeRateService {

    BigDecimal getRateFor(String currencyCodeFrom, String currencyCodeTo);

    Set<String> getValidCurrencies();
}
