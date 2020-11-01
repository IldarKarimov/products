package com.hse.products.service;

import com.hse.products.entity.Category;
import com.hse.products.exceptions.ApiException;
import com.hse.products.exceptions.ExceptionKeyEnum;
import com.hse.products.exceptions.NotFoundException;
import com.hse.products.exceptions.ValidationException;
import com.hse.products.model.CategoryDTO;
import com.hse.products.model.CategoryTree;
import com.hse.products.repository.CategoryRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import static com.hse.products.utils.TestDataUtil.getTestCategories;
import static com.hse.products.utils.TestDataUtil.getTestCategory;
import static com.hse.products.utils.TestDataUtil.getTestCategoryDTO;
import static com.hse.products.utils.TestDataUtil.getTestCategoryDTOs;
import static com.hse.products.utils.TestDataUtil.getTestCategoryTree;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CategoriesServiceTest {

    @InjectMocks
    private CategoriesService categoriesService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ValidationService validationService;

    @Spy
    private ModelMapper modelMapper;

    @Test
    public void getByIdSuccess() throws NotFoundException {
        Category testCategory = getTestCategory();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        CategoryDTO categoryDTO = categoriesService.getById(1L);
        assertThat(categoryDTO).isNotNull();
        assertEquals(getTestCategoryDTO(), categoryDTO);
    }

    @Test
    public void getByIdNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> categoriesService.getById(1L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.NotFound);
        assertThat(exception.getMessage()).isEqualTo("Category with ID " + 1 + " is not found");
    }

    @Test
    public void deleteByIdSuccess() throws ValidationException {
        doNothing().when(validationService).validateCategoryDeletion(1L);
        when(categoryRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> categoriesService.deleteById(1L));
    }

    @Test
    public void deleteByIdNotFound() throws ValidationException {
        doNothing().when(validationService).validateCategoryDeletion(1L);
        when(categoryRepository.existsById(1L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> categoriesService.deleteById(1L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.NotFound);
        assertThat(exception.getMessage()).isEqualTo("Category with ID " + 1 + " is not found");
    }

    @Test
    public void updateCategorySuccess() throws ApiException {
        CategoryDTO testCategoryDTO = getTestCategoryDTO();
        Category testCategory = getTestCategory();

        doNothing().when(validationService).validateIdNotUpdated(anyLong(), anyLong());
        doNothing().when(validationService).validateParentIdExists(anyLong());

        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.save(any())).thenReturn(testCategory);

        CategoryDTO categoryDTO = categoriesService.updateCategory(1L, testCategoryDTO);
        assertThat(categoryDTO).isNotNull();
        assertEquals(testCategoryDTO, categoryDTO);
    }

    @Test
    public void updateCategoryNotFound() throws ValidationException {
        CategoryDTO testCategoryDTO = getTestCategoryDTO();

        doNothing().when(validationService).validateIdNotUpdated(anyLong(), anyLong());
        doNothing().when(validationService).validateParentIdExists(anyLong());
        when(categoryRepository.existsById(1L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> categoriesService.updateCategory(1L, testCategoryDTO));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.NotFound);
        assertThat(exception.getMessage()).isEqualTo("Category with ID " + 1 + " is not found");
    }

    @Test
    public void addCategory() throws ValidationException {
        CategoryDTO testCategoryDTO = getTestCategoryDTO();
        Category testCategory = getTestCategory();

        doNothing().when(validationService).validateIdNotAssigned(anyLong());
        doNothing().when(validationService).validateParentIdExists(anyLong());

        when(categoryRepository.save(any())).thenReturn(testCategory);

        CategoryDTO categoryDTO = categoriesService.addCategory(testCategoryDTO);
        assertThat(categoryDTO).isNotNull();
        assertEquals(testCategoryDTO, categoryDTO);
    }

    @Test
    public void getParentCategoriesSuccess() throws NotFoundException {
        List<Category> testCategories = getTestCategories();
        List<CategoryDTO> testCategoryDTOs = getTestCategoryDTOs();
        when(categoryRepository.findAllByParentIdIsNull()).thenReturn(testCategories);

        List<CategoryDTO> categories = categoriesService.getParentCategories();

        assertThat(categories).isNotNull();
        assertThat(categories.size()).isEqualTo(2);
        assertThat(categories).isEqualTo(testCategoryDTOs);
    }


    @Test
    public void getParentCategoriesNotFound() {
        when(categoryRepository.findAllByParentIdIsNull()).thenReturn(Collections.emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> categoriesService.getParentCategories());

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.NotFound);
        assertThat(exception.getMessage()).isEqualTo("Parent categories don't exist");
    }

    @Test
    public void getChildategoriesSuccess() throws NotFoundException {
        List<Category> testCategories = getTestCategories();
        List<CategoryDTO> testCategoryDTOs = getTestCategoryDTOs();
        when(categoryRepository.findCategoriesByParentId(2L)).thenReturn(testCategories);

        List<CategoryDTO> categories = categoriesService.getChildategories(2L);

        assertThat(categories).isNotNull();
        assertThat(categories.size()).isEqualTo(2);
        assertThat(categories).isEqualTo(testCategoryDTOs);
    }

    @Test
    public void getChildategoriesNotFound() {
        when(categoryRepository.findCategoriesByParentId(2L)).thenReturn(Collections.emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> categoriesService.getChildategories(2L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.NotFound);
        assertThat(exception.getMessage()).isEqualTo("Category with ID " + 2 + " doesn't have child categories");
    }

    @Test
    public void getCategoryTreeSuccess() throws NotFoundException {
        List<Category> testCategories = getTestCategories();
        CategoryTree testCategoryTree = getTestCategoryTree();
        when(categoryRepository.getCategoryTree(2L)).thenReturn(testCategories);

        CategoryTree categoryTree = categoriesService.getCategoryTree(2L);

        assertThat(categoryTree).isNotNull();
        assertThat(categoryTree).isEqualTo(testCategoryTree);
    }

    @Test
    public void getCategoryTreeNotFound() {
        when(categoryRepository.getCategoryTree(2L)).thenReturn(Collections.emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> categoriesService.getCategoryTree(2L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.NotFound);
        assertThat(exception.getMessage()).isEqualTo("Category with ID " + 2 + " is not found");
    }
}
