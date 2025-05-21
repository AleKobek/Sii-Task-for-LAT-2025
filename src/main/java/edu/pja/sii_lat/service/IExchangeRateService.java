package edu.pja.sii_lat.service;

import java.util.Set;

public interface IExchangeRateService {

    double getRateFor(String currencyCodeFrom, String currencyCodeTo);

    Set<String> getValidCurrencies();
}
