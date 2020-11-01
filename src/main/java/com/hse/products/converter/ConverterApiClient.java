package com.hse.products.converter;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "converterApiClient", url = "${converter.url}")
public interface ConverterApiClient extends ConverterApi {
}

