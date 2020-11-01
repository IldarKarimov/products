package com.hse.products.controller;

import com.hse.products.api.CategoriesApi;
import com.hse.products.model.CategoryDTO;
import com.hse.products.model.CategoryTree;
import com.hse.products.service.CategoriesService;
import java.util.List;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoriesController implements CategoriesApi {

    @Autowired
    private CategoriesService categoriesService;

    @SneakyThrows
    @Override
    public ResponseEntity<CategoryDTO> addCategory(@Valid CategoryDTO categoryDTO) {
        return new ResponseEntity(categoriesService.addCategory(categoryDTO), HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<CategoryDTO> deleteCategoryById(Long id) {
        categoriesService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<CategoryDTO> getCategoryById(Long id) {
        return new ResponseEntity(categoriesService.getById(id), HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<List<CategoryDTO>> getChildsByParentId(Long id) {
        return new ResponseEntity(categoriesService.getChildategories(id), HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<List<CategoryDTO>> getParents() {
        return new ResponseEntity(categoriesService.getParentCategories(), HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<CategoryTree> getCategoryTree(Long id) {
        return new ResponseEntity(categoriesService.getCategoryTree(id), HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<CategoryDTO> updateCategoryById(Long id, @Valid CategoryDTO categoryDTO) {
        return new ResponseEntity(categoriesService.updateCategory(id, categoryDTO), HttpStatus.OK);
    }
}
