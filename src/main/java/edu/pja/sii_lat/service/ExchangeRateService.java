package edu.pja.sii_lat.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExchangeRateService implements IExchangeRateService{

    private final WebClient client;

    @Getter
    private final Set<String> validCurrencies = new HashSet<>();



    public BigDecimal getRateFor(String currencyCodeFrom, String currencyCodeTo) {
        // checking parameters
        if(currencyCodeFrom == null || currencyCodeFrom.isBlank()){
            throw new RuntimeException("Cannot fetch rates for an empty currency");
        }
        if(currencyCodeTo == null || currencyCodeTo.isBlank()){
            throw new RuntimeException("Cannot exchange "+ currencyCodeFrom +" to an empty currency");
        }
        if(!validCurrencies.isEmpty()){
            if(!validCurrencies.contains(currencyCodeFrom)){
                throw new RuntimeException("Currency "+currencyCodeFrom + " is not supported");
            }
            if(!validCurrencies.contains(currencyCodeTo)){
                throw new RuntimeException("Currency "+currencyCodeTo + " is not supported");
            }
        }
        if(currencyCodeFrom.equals(currencyCodeTo)){
            return BigDecimal.ONE;
        }
        // done checking parameters, sending request
        GetExchangeRateResponse res = client.get().uri("/" + currencyCodeFrom).retrieve().bodyToMono(GetExchangeRateResponse.class).block();
        if(res == null){
            throw new RuntimeException("Empty response - failed to fetch rates for "+currencyCodeFrom);
        }
        // saving for later
        if(validCurrencies.isEmpty()){
            validCurrencies.addAll(res.rates.keySet());
        }
        if(!res.rates.containsKey(currencyCodeTo)){
            throw new RuntimeException("Exchange from " + currencyCodeFrom + " to " + currencyCodeTo + " is not supported");
        }
        return res.rates.get(currencyCodeTo);
    }

    @Data
    private static class GetExchangeRateResponse {
        @JsonProperty("conversion_rates")
        private Map<String, BigDecimal> rates;
    }

}
