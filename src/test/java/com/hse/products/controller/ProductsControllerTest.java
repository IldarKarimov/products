package com.hse.products.controller;

import com.hse.products.exceptions.ExceptionKeyEnum;
import com.hse.products.exceptions.NotFoundException;
import com.hse.products.model.Error;
import com.hse.products.model.FullProductDTO;
import com.hse.products.model.ProductDTO;
import com.hse.products.service.ProductsService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static com.hse.products.utils.TestDataUtil.getExpectedError;
import static com.hse.products.utils.TestDataUtil.getTestFullProductDTO;
import static com.hse.products.utils.TestDataUtil.getTestProductDTO;
import static com.hse.products.utils.TestDataUtil.getTestProductDTOs;
import static com.hse.products.utils.TestDataUtil.mapToJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductsControllerTest {

    @MockBean
    private ProductsService productsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addProductTest() throws Exception {
        ProductDTO testProductDTO = getTestProductDTO();
        String testProductDTOJson = mapToJson(testProductDTO);
        when(productsService.addProduct(any())).thenReturn(testProductDTO);

        MockHttpServletResponse response = mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(testProductDTOJson)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(testProductDTOJson, response.getContentAsString());
    }

    @Test
    public void deleteProductByIdSuccess() throws Exception {
        doNothing().when(productsService).deleteById(1L);

        mockMvc.perform(delete("/products/{id}", 1)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteProductByNotFound() throws Exception {
        ExceptionKeyEnum exceptionKey = ExceptionKeyEnum.NotFound;
        String message = "Product with ID " + 2 + " is not found";
        List<Error> expectedError = getExpectedError(exceptionKey, message);
        String expectedErrorJson = mapToJson(expectedError);

        doThrow(new NotFoundException(exceptionKey, message))
            .when(productsService).deleteById(1L);

        MockHttpServletResponse response = mockMvc.perform(delete("/products/{id}", 1)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(expectedErrorJson, response.getContentAsString());
    }

    @Test
    public void getProductByIdSuccess() throws Exception {
        ProductDTO testProductDTO = getTestProductDTO();
        String testProductDTOJson = mapToJson(testProductDTO);
        when(productsService.getById(anyLong(), any())).thenReturn(testProductDTO);

        MockHttpServletResponse response = mockMvc.perform(get("/products/{id}", 1)
            .param("currency", "EUR")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(testProductDTOJson, response.getContentAsString());
    }

    @Test
    public void getProductByIdNotFound() throws Exception {
        ExceptionKeyEnum exceptionKey = ExceptionKeyEnum.NotFound;
        String message = "Product with ID " + 1 + " is not found";
        List<Error> expectedError = getExpectedError(exceptionKey, message);
        String expectedErrorJson = mapToJson(expectedError);

        when(productsService.getById(1L, null))
            .thenThrow(new NotFoundException(exceptionKey, message));

        MockHttpServletResponse response = mockMvc.perform(get("/products/{id}", 1)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(expectedErrorJson, response.getContentAsString());
    }

    @Test
    public void updateCategoryByIdSuccess() throws Exception {
        ProductDTO testProductDTO = getTestProductDTO();
        String testProductDTOJson = mapToJson(testProductDTO);
        when(productsService.updateProduct(anyLong(), any())).thenReturn(testProductDTO);

        MockHttpServletResponse response =  mockMvc.perform(put("/products/{id}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(testProductDTOJson)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(testProductDTOJson, response.getContentAsString());
    }

    @Test
    public void updateCategoryByIdNotFound() throws Exception {
        ProductDTO testProductDTO = getTestProductDTO();
        String testProductDTOJson = mapToJson(testProductDTO);

        ExceptionKeyEnum exceptionKey = ExceptionKeyEnum.NotFound;
        String message = "Product with ID " + 2 + " is not found";
        List<Error> expectedError = getExpectedError(exceptionKey, message);
        String expectedErrorJson = mapToJson(expectedError);

        when(productsService.updateProduct(1L, testProductDTO))
            .thenThrow(new NotFoundException(exceptionKey, message));

        MockHttpServletResponse response = mockMvc.perform(put("/products/{id}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(testProductDTOJson)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(expectedErrorJson, response.getContentAsString());
    }

    @Test
    public void getProductsByCategoryIdSuccess() throws Exception {
        List<ProductDTO> testProductDTOs = getTestProductDTOs();
        String testProductDTOsJson = mapToJson(testProductDTOs);
        when(productsService.getProductsByCategoryId(anyLong())).thenReturn(testProductDTOs);

        MockHttpServletResponse response = mockMvc.perform(get("/products")
            .param("categoryId", "2")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(testProductDTOsJson, response.getContentAsString());
    }

    @Test
    public void getProductsByCategoryIdNotFound() throws Exception {
        ExceptionKeyEnum exceptionKey = ExceptionKeyEnum.NotFound;
        String message = "Category with ID " + 2 + " doesn't have products";
        List<Error> expectedError = getExpectedError(exceptionKey, message);
        String expectedErrorJson = mapToJson(expectedError);

        when(productsService.getProductsByCategoryId(2L))
            .thenThrow(new NotFoundException(exceptionKey, message));

        MockHttpServletResponse response = mockMvc.perform(get("/products")
            .param("categoryId", "2")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(expectedErrorJson, response.getContentAsString());
    }

    @Test
    public void getFullProductByIdSuccess() throws Exception {
        FullProductDTO testFullProductDTO = getTestFullProductDTO();
        String testFullProductDTOJson = mapToJson(testFullProductDTO);
        when(productsService.getFullById(anyLong(), any())).thenReturn(testFullProductDTO);

        MockHttpServletResponse response = mockMvc.perform(get("/products/{id}/full", 1)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(testFullProductDTOJson, response.getContentAsString());
    }

    @Test
    public void getFullProductByIdFail() throws Exception {
        ExceptionKeyEnum exceptionKey = ExceptionKeyEnum.NotFound;
        String message = "Product with ID " + 1 + " is not found";
        List<Error> expectedError = getExpectedError(exceptionKey, message);
        String expectedErrorJson = mapToJson(expectedError);

        when(productsService.getFullById(1L, null))
            .thenThrow(new NotFoundException(exceptionKey, message));

        MockHttpServletResponse response = mockMvc.perform(get("/products/{id}/full", 1)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(expectedErrorJson, response.getContentAsString());
    }
}
