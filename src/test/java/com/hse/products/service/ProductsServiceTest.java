package com.hse.products.service;

import com.hse.products.converter.ConverterService;
import com.hse.products.entity.Product;
import com.hse.products.exceptions.ConverterException;
import com.hse.products.exceptions.ExceptionKeyEnum;
import com.hse.products.exceptions.NotFoundException;
import com.hse.products.exceptions.ValidationException;
import com.hse.products.model.CategoryTree;
import com.hse.products.model.CurrencyEnum;
import com.hse.products.model.FullProductDTO;
import com.hse.products.model.ProductDTO;
import com.hse.products.repository.ProductRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import static com.hse.products.utils.TestDataUtil.getTestCategoryTree;
import static com.hse.products.utils.TestDataUtil.getTestFullProductDTO;
import static com.hse.products.utils.TestDataUtil.getTestProduct;
import static com.hse.products.utils.TestDataUtil.getTestProductDTO;
import static com.hse.products.utils.TestDataUtil.getTestProductDTOs;
import static com.hse.products.utils.TestDataUtil.getTestProducts;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProductsServiceTest {

    @InjectMocks
    private ProductsService productsService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ValidationService validationService;

    @Mock
    private ConverterService converterService;

    @Mock
    private CategoriesService categoriesService;

    @Spy
    private ModelMapper modelMapper;

    @Test
    public void getByIdAndCurrencyTest() throws ConverterException, NotFoundException {
        Product testProduct = getTestProduct();
        ProductDTO testProductDTO = getTestProductDTO();
        testProductDTO.setPrice(2222.22);
        testProductDTO.setCurrency(CurrencyEnum.USD);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        when(converterService.getConvertedPrice(anyDouble(), any(), any())).thenReturn(2222.22);

        ProductDTO productDTO = productsService.getById(1L, CurrencyEnum.USD);
        assertThat(productDTO).isNotNull();
        assertEquals(testProductDTO, productDTO);
    }

    @Test
    public void getByIdBaseCurrencyTest() throws NotFoundException, ConverterException {
        Product testProduct = getTestProduct();
        ProductDTO testProductDTO = getTestProductDTO();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

        ProductDTO productDTO = productsService.getById(1L, null);
        assertThat(productDTO).isNotNull();
        assertEquals(testProductDTO, productDTO);
    }

    @Test
    public void getByIdNotFoundTest() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> productsService.getById(1L, null));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.NotFound);
        assertThat(exception.getMessage()).isEqualTo("Product with ID " + 1 + " is not found");
    }

    @Test
    public void deleteByIdSuccessTest() {
        when(productRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> productsService.deleteById(1L));
    }

    @Test
    public void deleteByIdNotFoundTest() {
        when(productRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> productsService.deleteById(1L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.NotFound);
        assertThat(exception.getMessage()).isEqualTo("Product with ID " + 1 + " is not found");
    }

    @Test
    public void updateProductSuccessTest() throws ValidationException, NotFoundException {
        ProductDTO testProductDTO = getTestProductDTO();
        Product testProduct = getTestProduct();
        doNothing().when(validationService).validateIdNotUpdated(anyLong(), anyLong());
        doNothing().when(validationService).validateCategoryIdNotNull(anyLong());
        doNothing().when(validationService).validateParentIdExists(anyLong());
        when(productRepository.existsById(anyLong())).thenReturn(true);
        when(productRepository.save(any())).thenReturn(testProduct);

        ProductDTO productDTO = productsService.updateProduct(1L, testProductDTO);
        assertThat(productDTO).isNotNull();
        assertEquals(testProductDTO, productDTO);
    }

    @Test
    public void updateProductNotFoundTest() throws ValidationException {
        ProductDTO testProductDTO = getTestProductDTO();
        Product testProduct = getTestProduct();
        doNothing().when(validationService).validateIdNotUpdated(anyLong(), anyLong());
        doNothing().when(validationService).validateCategoryIdNotNull(anyLong());
        doNothing().when(validationService).validateParentIdExists(anyLong());
        when(productRepository.existsById(anyLong())).thenReturn(false);
        when(productRepository.save(any())).thenReturn(testProduct);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> productsService.updateProduct(1L, testProductDTO));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.NotFound);
        assertThat(exception.getMessage()).isEqualTo("Product with ID " + 1 + " is not found");
    }

    @Test
    public void addProductTest() throws ValidationException {
        ProductDTO testProductDTO = getTestProductDTO();
        Product testProduct = getTestProduct();
        doNothing().when(validationService).validateIdNotAssigned(anyLong());
        doNothing().when(validationService).validateCategoryIdNotNull(anyLong());
        doNothing().when(validationService).validateParentIdExists(anyLong());
        when(productRepository.save(any())).thenReturn(testProduct);

        ProductDTO productDTO = productsService.addProduct(testProductDTO);
        assertThat(productDTO).isNotNull();
        assertEquals(testProductDTO, productDTO);
    }

    @Test
    public void getProductsByCategoryIdSuccess() throws NotFoundException {
        List<ProductDTO> testProductDTOs = getTestProductDTOs();
        List<Product> testProducts = getTestProducts();
        when(productRepository.findProductsByCategoryId(2L)).thenReturn(testProducts);

        List<ProductDTO> products = productsService.getProductsByCategoryId(2L);
        assertThat(products).isNotNull();
        assertEquals(testProductDTOs, products);
    }

    @Test
    public void getProductsByCategoryIdNotFound() {
        when(productRepository.findProductsByCategoryId(2L)).thenReturn(Collections.emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> productsService.getProductsByCategoryId(2L));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.NotFound);
        assertThat(exception.getMessage()).isEqualTo("Category with ID " + 2 + " doesn't have products");
    }

    @Test
    public void getFullByIdSuccess() throws NotFoundException, ConverterException {
        Product testProduct = getTestProduct();
        ProductDTO testProductDTO = getTestProductDTO();
        CategoryTree testCategoryTree = getTestCategoryTree();
        FullProductDTO testFullProductDTO = getTestFullProductDTO();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        when(categoriesService.getCategoryTree(anyLong())).thenReturn(testCategoryTree);

        FullProductDTO productDTO = productsService.getFullById(1L, null);
        assertThat(productDTO).isNotNull();
        assertEquals(testFullProductDTO, productDTO);
    }

    @Test
    public void getFullByIdNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> productsService.getFullById(1L, null));

        assertThat(exception).isNotNull();
        assertThat(exception.getExceptionKey()).isEqualTo(ExceptionKeyEnum.NotFound);
        assertThat(exception.getMessage()).isEqualTo("Product with ID " + 1 + " is not found");
    }
}

