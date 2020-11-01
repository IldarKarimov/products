package com.hse.products.converter;

import com.hse.products.model.CurrencyEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ConverterApi {

    @GetMapping(value = "/api/latest?access_key={accessKey}&base={baseCurrency}&symbols={desiredCurrency}")
    ConverterDTO getRates(@PathVariable(value = "accessKey") String accessKey,
                          @PathVariable(value = "baseCurrency") CurrencyEnum baseCurrency,
                          @PathVariable(value = "desiredCurrency") CurrencyEnum desiredCurrency);
}
