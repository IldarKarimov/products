package com.hse.products.service;

import com.hse.products.exceptions.ExceptionKeyEnum;
import com.hse.products.exceptions.ValidationException;
import com.hse.products.repository.CategoryRepository;
import com.hse.products.repository.ProductRepository;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public void validateIdNotAssigned(Long id) throws ValidationException {
        if (id != null) {
            throw new ValidationException(ExceptionKeyEnum.IdAssignmentForbidden, "ID manual assignment is forbidden");
        }
    }

    public void validateIdNotUpdated(@NotNull Long oldId, Long newId) throws ValidationException {
        if (!oldId.equals(newId)) {
            throw new ValidationException(ExceptionKeyEnum.IdUpdateForbidden, "ID update is forbidden");
        }
    }

    public void validateCategoryIdNotNull(Long parentId) throws ValidationException {
        if (parentId == null) {
            throw new ValidationException(ExceptionKeyEnum.CategoryIdIsNull, "Category ID cannot be null");
        }
    }

    public void validateParentIdExists(Long parentId) throws ValidationException {
        if (parentId != null && !categoryRepository.existsById(parentId)) {
            throw new ValidationException(ExceptionKeyEnum.ParentCategoryDoesNotExist, "Parent category with ID " + parentId + " doesn't exist");
        }
    }

    public void validateCategoryDeletion(@NotNull Long id) throws ValidationException {
        if (productRepository.existsProductsByCategoryId(id) || categoryRepository.existsCategoriesByParentId(id)) {
            throw new ValidationException(ExceptionKeyEnum.CategoryAssigned, "Assigned category with ID " + id + " cannot be deleted");
        }
    }
}
