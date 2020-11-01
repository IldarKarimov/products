package com.hse.products.converter;

import com.hse.products.exceptions.ConverterException;
import com.hse.products.exceptions.ExceptionKeyEnum;
import com.hse.products.model.CurrencyEnum;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ConverterServiceTest {

    @InjectMocks
    private ConverterService converterService;

    @Mock
    private ConverterApi converterApi;

    @Test
    public void getConvertedPriceSuccess() throws ConverterException {
        when(converterApi.getRates(any(), any(), any())).thenReturn(getMockedDTO());
        Double convertedPrice = converterService.getConvertedPrice(1.00, CurrencyEnum.EUR, CurrencyEnum.USD);
        assertThat(convertedPrice).isNotNull();
        assertEquals(1.17, convertedPrice);
    }

    @Test
    public void getConvertedPriceFail() {
        when(converterApi.getRates(anyString(), any(), any())).thenReturn(null);

        ConverterException exception = assertThrows(ConverterException.class,
            () -> converterService.getConvertedPrice(1.00, CurrencyEnum.EUR, CurrencyEnum.USD));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.ConvertionFailed);
        assertThat(exception.getMessage()).isEqualTo("Price cannot be converted");
    }

    private ConverterDTO getMockedDTO() {
        HashMap<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.17);

        return ConverterDTO.builder()
            .success(true)
            .base("EUR")
            .rates(rates)
            .build();
    }
}
