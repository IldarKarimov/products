package com.hse.products.controller;

import com.hse.products.api.ProductsApi;
import com.hse.products.model.CurrencyEnum;
import com.hse.products.model.FullProductDTO;
import com.hse.products.model.ProductDTO;
import com.hse.products.service.ProductsService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductsController implements ProductsApi {

    @Autowired
    private ProductsService productsService;

    @SneakyThrows
    @Override
    public ResponseEntity<ProductDTO> addProduct(@Valid ProductDTO productDTO) {
        return new ResponseEntity(productsService.addProduct(productDTO), HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<ProductDTO> deleteProductById(Long id) {
        productsService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<FullProductDTO> getFullProductById(Long id, @Valid CurrencyEnum currency) {
        return new ResponseEntity(productsService.getFullById(id, currency), HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<ProductDTO> getProductById(Long id, CurrencyEnum currency) {
        return new ResponseEntity(productsService.getById(id, currency), HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<List<ProductDTO>> getProductsByCategoryId(@NotNull @Valid Long categoryId) {
        return new ResponseEntity(productsService.getProductsByCategoryId(categoryId), HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<ProductDTO> updateProductById(Long id, @Valid ProductDTO productDTO) {
        return new ResponseEntity(productsService.updateProduct(id, productDTO), HttpStatus.OK);
    }
}
