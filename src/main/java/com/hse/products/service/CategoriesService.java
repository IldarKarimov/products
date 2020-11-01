package com.hse.products.service;

import com.hse.products.entity.Category;
import com.hse.products.exceptions.ApiException;
import com.hse.products.exceptions.ExceptionKeyEnum;
import com.hse.products.exceptions.NotFoundException;
import com.hse.products.exceptions.ValidationException;
import com.hse.products.model.CategoryDTO;
import com.hse.products.model.CategoryTree;
import com.hse.products.repository.CategoryRepository;
import com.sun.istack.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriesService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ValidationService validationService;

    public CategoryDTO getById(@NotNull Long id) throws NotFoundException {
        Optional<Category> category = categoryRepository.findById(id);
        if (!category.isPresent()) {
            throw new NotFoundException(ExceptionKeyEnum.NotFound, "Category with ID " + id + " is not found");
        }
        return convertToDto(category.get());
    }

    public void deleteById(@NotNull Long id) throws ApiException {
        validationService.validateCategoryDeletion(id);
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(ExceptionKeyEnum.NotFound, "Category with ID " + id + " is not found");
        }
        categoryRepository.deleteById(id);
    }

    public CategoryDTO updateCategory(@NotNull Long id, @NotNull CategoryDTO categoryDTO) throws ApiException {
        validationService.validateIdNotUpdated(id, categoryDTO.getId());
        validationService.validateParentIdExists(categoryDTO.getParentId());
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(ExceptionKeyEnum.NotFound, "Category with ID " + id + " is not found");
        }
        return convertToDto(
            categoryRepository.save(
                convertToEntity(categoryDTO)));
    }

    public CategoryDTO addCategory(@NotNull CategoryDTO categoryDTO) throws ValidationException {
        validationService.validateIdNotAssigned(categoryDTO.getId());
        validationService.validateParentIdExists(categoryDTO.getParentId());
        return convertToDto(
            categoryRepository.save(
                convertToEntity(categoryDTO)));
    }

    public List<CategoryDTO> getParentCategories() throws NotFoundException {
        List<Category> categories = categoryRepository.findAllByParentIdIsNull();
        if (categories.isEmpty()) {
            throw new NotFoundException(ExceptionKeyEnum.NotFound, "Parent categories don't exist");
        }
        return categories.stream()
            .map(c -> convertToDto(c))
            .collect(Collectors.toList());
    }

    public List<CategoryDTO> getChildategories(Long parentId) throws NotFoundException {
        List<Category> categories = categoryRepository.findCategoriesByParentId(parentId);
        if (categories.isEmpty()) {
            throw new NotFoundException(ExceptionKeyEnum.NotFound, "Category with ID " + parentId + " doesn't have child categories");
        }
        return categories.stream()
            .map(c -> convertToDto(c))
            .collect(Collectors.toList());
    }

    public CategoryTree getCategoryTree(Long childId) throws NotFoundException {
        List<Category> categoriesList = categoryRepository.getCategoryTree(childId);
        if (categoriesList.isEmpty()) {
            throw new NotFoundException(ExceptionKeyEnum.NotFound, "Category with ID " + childId + " is not found");
        }
        Map<Long, CategoryDTO> categories = categoriesList.stream()
            .map(c -> convertToDto(c))
            .collect(Collectors.toMap(CategoryDTO::getParentId, dto -> dto));
        return getFullCategoryTree(categories, null);
    }

    private CategoryTree getFullCategoryTree(Map<Long, CategoryDTO> categories, Long parentId) {
        CategoryDTO categoryDTO = categories.get(parentId);
        if (categoryDTO != null) {
            CategoryTree categoryTree = new CategoryTree();
            categoryTree.setCategoryDTO(categoryDTO);
            categoryTree.setChildren(getFullCategoryTree(categories, categoryDTO.getId()));
            return categoryTree;
        }
        return null;
    }

    private CategoryDTO convertToDto(Category category) {
        return category != null ? modelMapper.map(category, CategoryDTO.class) : null;
    }

    private Category convertToEntity(@NotNull CategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO, Category.class);
    }
}
