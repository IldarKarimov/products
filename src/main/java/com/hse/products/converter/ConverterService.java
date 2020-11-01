package com.hse.products.converter;

import com.hse.products.exceptions.ConverterException;
import com.hse.products.exceptions.ExceptionKeyEnum;
import com.hse.products.model.CurrencyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConverterService {

    @Value("${converter.accessKey}")
    private String accessKey;

    @Autowired
    private ConverterApi converterApi;

    public Double getConvertedPrice(Double basePrice, CurrencyEnum baseCurrency, CurrencyEnum desirecCurrency) throws ConverterException {
        try {
            ConverterDTO converterDTO = converterApi.getRates(accessKey, baseCurrency, desirecCurrency);
            Double rate = converterDTO.getRates().get(desirecCurrency.getValue());
            return Math.floor(100 * rate * basePrice) / 100;
        } catch (Exception e) {
            throw new ConverterException(ExceptionKeyEnum.ConvertionFailed, "Price cannot be converted");
        }
    }
}
