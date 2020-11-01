package com.hse.products.converter;

import java.util.HashMap;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConverterDTO {

    private boolean success;

    private String base;

    private HashMap<String, Double> rates;

}
