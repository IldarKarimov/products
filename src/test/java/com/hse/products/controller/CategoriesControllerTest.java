package com.hse.products.controller;

import com.hse.products.exceptions.ExceptionKeyEnum;
import com.hse.products.exceptions.NotFoundException;
import com.hse.products.model.CategoryDTO;
import com.hse.products.model.CategoryTree;
import com.hse.products.model.Error;
import com.hse.products.service.CategoriesService;
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
import static com.hse.products.utils.TestDataUtil.getTestCategoryDTO;
import static com.hse.products.utils.TestDataUtil.getTestCategoryDTOs;
import static com.hse.products.utils.TestDataUtil.getTestCategoryTree;
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
public class CategoriesControllerTest {

    @MockBean
    private CategoriesService categoriesService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addCategoryTest() throws Exception {
        CategoryDTO testCategoryDTO = getTestCategoryDTO();
        String testCategoryDTOJson = mapToJson(testCategoryDTO);
        when(categoriesService.addCategory(any())).thenReturn(testCategoryDTO);

        MockHttpServletResponse response = mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(testCategoryDTOJson)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(testCategoryDTOJson, response.getContentAsString());
    }

    @Test
    public void deleteCategoryByIdSuccess() throws Exception {
        doNothing().when(categoriesService).deleteById(1L);

        mockMvc.perform(delete("/categories/{id}", 1)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteCategoryNotFound() throws Exception {
        ExceptionKeyEnum exceptionKey = ExceptionKeyEnum.NotFound;
        String message = "Category with ID " + 1 + " is not found";
        List<Error> expectedError = getExpectedError(exceptionKey, message);
        String expectedErrorJson = mapToJson(expectedError);

        doThrow(new NotFoundException(exceptionKey, message))
            .when(categoriesService).deleteById(1L);

        MockHttpServletResponse response = mockMvc.perform(delete("/categories/{id}", 1)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(expectedErrorJson, response.getContentAsString());
    }

    @Test
    public void getCategoryByIdSuccess() throws Exception {
        CategoryDTO testCategoryDTO = getTestCategoryDTO();
        String testCategoryDTOJson = mapToJson(testCategoryDTO);
        when(categoriesService.getById(anyLong())).thenReturn(testCategoryDTO);

        MockHttpServletResponse response = mockMvc.perform(get("/categories/{id}", 1)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(testCategoryDTOJson, response.getContentAsString());
    }

    @Test
    public void getCategoryByIdNotFound() throws Exception {
        ExceptionKeyEnum exceptionKey = ExceptionKeyEnum.NotFound;
        String message = "Category with ID " + 1 + " is not found";
        List<Error> expectedError = getExpectedError(exceptionKey, message);
        String expectedErrorJson = mapToJson(expectedError);

        when(categoriesService.getById(1L))
            .thenThrow(new NotFoundException(exceptionKey, message));

        MockHttpServletResponse response = mockMvc.perform(get("/categories/{id}", 1)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(expectedErrorJson, response.getContentAsString());
    }

    @Test
    public void updateCategoryByIdSuccess() throws Exception {
        CategoryDTO testCategoryDTO = getTestCategoryDTO();
        String testCategoryDTOJson = mapToJson(testCategoryDTO);
        when(categoriesService.updateCategory(anyLong(), any())).thenReturn(testCategoryDTO);

        MockHttpServletResponse response = mockMvc.perform(put("/categories/{id}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(testCategoryDTOJson)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(testCategoryDTOJson, response.getContentAsString());
    }

    @Test
    public void updateCategoryByIdNotFound() throws Exception {
        CategoryDTO testCategoryDTO = getTestCategoryDTO();
        String testCategoryDTOJson = mapToJson(testCategoryDTO);

        ExceptionKeyEnum exceptionKey = ExceptionKeyEnum.NotFound;
        String message = "Category with ID " + 1 + " is not found";
        List<Error> expectedError = getExpectedError(exceptionKey, message);
        String expectedErrorJson = mapToJson(expectedError);

        when(categoriesService.updateCategory(1L, testCategoryDTO))
            .thenThrow(new NotFoundException(exceptionKey, message));

        MockHttpServletResponse response = mockMvc.perform(put("/categories/{id}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(testCategoryDTOJson)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(expectedErrorJson, response.getContentAsString());
    }

    @Test
    public void getChildsByParentIdSuccess() throws Exception {
        List<CategoryDTO> testCategoryDTOs = getTestCategoryDTOs();
        String testCategoryDTOsJson = mapToJson(testCategoryDTOs);
        when(categoriesService.getChildategories(2L)).thenReturn(testCategoryDTOs);

        MockHttpServletResponse response = mockMvc.perform(get("/categories/{id}/childs", 2)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(testCategoryDTOsJson, response.getContentAsString());
    }

    @Test
    public void getChildsByParentIdNotFound() throws Exception {
        ExceptionKeyEnum exceptionKey = ExceptionKeyEnum.NotFound;
        String message = "Category with ID " + 2 + " doesn't have child categories";
        List<Error> expectedError = getExpectedError(exceptionKey, message);
        String expectedErrorJson = mapToJson(expectedError);

        when(categoriesService.getChildategories(2L))
            .thenThrow(new NotFoundException(exceptionKey, message));

        MockHttpServletResponse response = mockMvc.perform(get("/categories/{id}/childs", 2)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(expectedErrorJson, response.getContentAsString());
    }

    @Test
    public void getParentsSuccess() throws Exception {
        List<CategoryDTO> testCategoryDTOs = getTestCategoryDTOs();
        String testCategoryDTOsJson = mapToJson(testCategoryDTOs);
        when(categoriesService.getParentCategories()).thenReturn(testCategoryDTOs);

        MockHttpServletResponse response = mockMvc.perform(get("/categories/parents")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(testCategoryDTOsJson, response.getContentAsString());
    }

    @Test
    public void getParentsNotFound() throws Exception {
        ExceptionKeyEnum exceptionKey = ExceptionKeyEnum.NotFound;
        String message = "Parent categories don't exist";
        List<Error> expectedError = getExpectedError(exceptionKey, message);
        String expectedErrorJson = mapToJson(expectedError);

        when(categoriesService.getParentCategories())
            .thenThrow(new NotFoundException(exceptionKey, message));

        MockHttpServletResponse response = mockMvc.perform(get("/categories/parents")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(expectedErrorJson, response.getContentAsString());
    }

    @Test
    public void getCategoryTreeSuccess() throws Exception {
        CategoryTree testCategoryTree = getTestCategoryTree();
        String testCategoryTreeJson = mapToJson(testCategoryTree);
        when(categoriesService.getCategoryTree(anyLong())).thenReturn(testCategoryTree);

        MockHttpServletResponse response = mockMvc.perform(get("/categories/{id}/tree", 2)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(testCategoryTreeJson, response.getContentAsString());
    }

    @Test
    public void getCategoryTreeNotFound() throws Exception {
        ExceptionKeyEnum exceptionKey = ExceptionKeyEnum.NotFound;
        String message = "Category with ID " + 2 + " is not found";
        List<Error> expectedError = getExpectedError(exceptionKey, message);
        String expectedErrorJson = mapToJson(expectedError);

        when(categoriesService.getCategoryTree(2L))
            .thenThrow(new NotFoundException(exceptionKey, message));

        MockHttpServletResponse response = mockMvc.perform(get("/categories/{id}/tree", 2)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

        assertThat(response).isNotNull();
        assertEquals(expectedErrorJson, response.getContentAsString());
    }
}
