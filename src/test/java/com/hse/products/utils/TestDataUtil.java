package com.hse.products.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hse.products.entity.Category;
import com.hse.products.entity.Product;
import com.hse.products.exceptions.ExceptionKeyEnum;
import com.hse.products.model.CategoryDTO;
import com.hse.products.model.CategoryTree;
import com.hse.products.model.CurrencyEnum;
import com.hse.products.model.Error;
import com.hse.products.model.FullProductDTO;
import com.hse.products.model.ProductDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestDataUtil {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String mapToJson(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public static Category getTestCategory() {
        return Category.builder()
            .id(1L)
            .name("Electronics")
            .parentId(null)
            .build();
    }

    public static Category getTestCategory2() {
        return Category.builder()
            .id(2L)
            .name("Mobile phones")
            .parentId(1L)
            .build();
    }

    public static CategoryDTO getTestCategoryDTO() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Electronics");
        categoryDTO.setParentId(null);
        return categoryDTO;
    }

    public static CategoryDTO getTestCategoryDTO2() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(2L);
        categoryDTO.setName("Mobile phones");
        categoryDTO.setParentId(1L);
        return categoryDTO;
    }

    public static List<Category> getTestCategories() {
        Category testCategory1 = getTestCategory();
        Category testCategory2 = getTestCategory2();
        return Arrays.asList(testCategory1, testCategory2);
    }

    public static List<CategoryDTO> getTestCategoryDTOs() {
        CategoryDTO testCategoryDTO1 = getTestCategoryDTO();
        CategoryDTO testCategoryDTO2 = getTestCategoryDTO2();
        return Arrays.asList(testCategoryDTO1, testCategoryDTO2);
    }

    public static Product getTestProduct() {
        return Product.builder()
            .id(1L)
            .name("Iphone 12")
            .categoryId(2L)
            .currency("EUR")
            .price(1111.11)
            .build();
    }

    public static ProductDTO getTestProductDTO() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Iphone 12");
        productDTO.setCategoryId(2L);
        productDTO.setCurrency(CurrencyEnum.EUR);
        productDTO.setPrice(1111.11);
        return productDTO;
    }

    public static List<Product> getTestProducts() {
        Product testProduct1 = getTestProduct();
        Product testProduct2 = getTestProduct();
        return Arrays.asList(testProduct1, testProduct2);
    }

    public static List<ProductDTO> getTestProductDTOs() {
        ProductDTO testProductDTO1 = getTestProductDTO();
        ProductDTO testProductDTO2 = getTestProductDTO();
        return Arrays.asList(testProductDTO1, testProductDTO2);
    }

    public static CategoryTree getTestCategoryTree() {
        CategoryDTO testCategoryDTO1 = getTestCategoryDTO();
        CategoryDTO testCategoryDTO2 = getTestCategoryDTO2();

        CategoryTree categoryTree2 = new CategoryTree();
        categoryTree2.setCategoryDTO(testCategoryDTO2);

        CategoryTree categoryTree1 = new CategoryTree();
        categoryTree1.setCategoryDTO(testCategoryDTO1);
        categoryTree1.setChildren(categoryTree2);
        return categoryTree1;
    }

    public static FullProductDTO getTestFullProductDTO() {
        FullProductDTO fullProductDTO = new FullProductDTO();
        fullProductDTO.setCategoryTree(getTestCategoryTree());
        fullProductDTO.setProductDTO(getTestProductDTO());
        return fullProductDTO;
    }

    public static List<Error> getExpectedError(ExceptionKeyEnum exceptionKey, String message) {
        Error error = new Error();
        error.setKey(exceptionKey.toString());
        error.setDetail(message);
        return Collections.singletonList(error);
    }
}
