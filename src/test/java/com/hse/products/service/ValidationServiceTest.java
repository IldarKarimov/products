package com.hse.products.service;

import com.hse.products.exceptions.ExceptionKeyEnum;
import com.hse.products.exceptions.ValidationException;
import com.hse.products.repository.CategoryRepository;
import com.hse.products.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ValidationServiceTest {

    @InjectMocks
    private ValidationService validationService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    public void validateIdNotAssignedSuccess() {
        assertDoesNotThrow(() -> validationService.validateIdNotAssigned(null));
    }

    @Test
    public void validateIdNotAssignedFail() {
        ValidationException exception = assertThrows(ValidationException.class, () -> validationService.validateIdNotAssigned(1L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.IdAssignmentForbidden);
        assertThat(exception.getMessage()).isEqualTo("ID manual assignment is forbidden");
    }

    @Test
    public void validateIdNotUpdatedSuccess() {
        assertDoesNotThrow(() -> validationService.validateIdNotUpdated(1L, 1L));
    }

    @Test
    public void validateIdNotUpdatedFail() {
        ValidationException exception = assertThrows(ValidationException.class, () -> validationService.validateIdNotUpdated(1L, 2L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.IdUpdateForbidden);
        assertThat(exception.getMessage()).isEqualTo("ID update is forbidden");
    }

    @Test
    public void validateParentIdNotNullSuccess() {
        assertDoesNotThrow(() -> validationService.validateCategoryIdNotNull(1L));
    }

    @Test
    public void validateParentIdNotNullFail() {
        ValidationException exception = assertThrows(ValidationException.class, () -> validationService.validateCategoryIdNotNull(null));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.CategoryIdIsNull);
        assertThat(exception.getMessage()).isEqualTo("Category ID cannot be null");
    }

    @Test
    public void validateParentIdExistsSuccess() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> validationService.validateParentIdExists(1L));
    }

    @Test
    public void validateParentIdExistsFail() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class, () -> validationService.validateParentIdExists(1L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.ParentCategoryDoesNotExist);
        assertThat(exception.getMessage()).isEqualTo("Parent category with ID " + 1 + " doesn't exist");
    }

    @Test
    public void validateCategoryDeletionSuccess() {
        when(categoryRepository.existsCategoriesByParentId(1L)).thenReturn(false);
        when(productRepository.existsProductsByCategoryId(1L)).thenReturn(false);

        assertDoesNotThrow(() -> validationService.validateCategoryDeletion(1L));
    }

    @Test
    public void validateCategoryDeletionFail() {
        when(categoryRepository.existsCategoriesByParentId(1L)).thenReturn(true);
        when(productRepository.existsProductsByCategoryId(1L)).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class, () -> validationService.validateCategoryDeletion(1L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.CategoryAssigned);
        assertThat(exception.getMessage()).isEqualTo("Assigned category with ID " + 1 + " cannot be deleted");

        when(categoryRepository.existsCategoriesByParentId(1L)).thenReturn(true);
        when(productRepository.existsProductsByCategoryId(1L)).thenReturn(false);

        exception = assertThrows(ValidationException.class, () -> validationService.validateCategoryDeletion(1L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.CategoryAssigned);
        assertThat(exception.getMessage()).isEqualTo("Assigned category with ID " + 1 + " cannot be deleted");

        when(categoryRepository.existsCategoriesByParentId(1L)).thenReturn(false);
        when(productRepository.existsProductsByCategoryId(1L)).thenReturn(true);

        exception = assertThrows(ValidationException.class, () -> validationService.validateCategoryDeletion(1L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.CategoryAssigned);
        assertThat(exception.getMessage()).isEqualTo("Assigned category with ID " + 1 + " cannot be deleted");
    }




}
