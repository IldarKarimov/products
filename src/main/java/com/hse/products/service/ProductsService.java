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
import com.sun.istack.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductsService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ConverterService converterService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private CategoriesService categoriesService;

    public ProductDTO getById(Long id, CurrencyEnum currency) throws NotFoundException, ConverterException {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new NotFoundException(ExceptionKeyEnum.NotFound, "Product with ID " + id + " is not found");
        }
        ProductDTO productDTO = convertToDto(product.get());
        covnertPrice(productDTO, currency);
        return productDTO;
    }

    public FullProductDTO getFullById(Long id, CurrencyEnum currency) throws NotFoundException, ConverterException {
        ProductDTO productDTO = getById(id, currency);
        return getFullProductDTO(productDTO);
    }

    public void deleteById(Long id) throws NotFoundException {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException(ExceptionKeyEnum.NotFound, "Product with ID " + id + " is not found");
        }
        productRepository.deleteById(id);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) throws ValidationException, NotFoundException {
        validationService.validateIdNotUpdated(id, productDTO.getId());
        validationService.validateCategoryIdNotNull(productDTO.getCategoryId());
        validationService.validateParentIdExists(productDTO.getCategoryId());
        if (!productRepository.existsById(id)) {
            throw new NotFoundException(ExceptionKeyEnum.NotFound, "Product with ID " + id + " is not found");
        }
        return convertToDto(
            productRepository.save(
                convertToEntity(productDTO)));
    }

    public ProductDTO addProduct(ProductDTO productDTO) throws ValidationException {
        validationService.validateIdNotAssigned(productDTO.getId());
        validationService.validateCategoryIdNotNull(productDTO.getCategoryId());
        validationService.validateParentIdExists(productDTO.getCategoryId());
        return convertToDto(
            productRepository.save(
                convertToEntity(productDTO)));
    }

    public List<ProductDTO> getProductsByCategoryId(Long categoryId) throws NotFoundException {
        List<Product> products = productRepository.findProductsByCategoryId(categoryId);
        if (products.isEmpty()) {
            throw new NotFoundException(ExceptionKeyEnum.NotFound, "Category with ID " + categoryId + " doesn't have products");
        }
        return products.stream()
            .map(p -> convertToDto(p))
            .collect(Collectors.toList());
    }

    private ProductDTO convertToDto(Product product) {
        return product != null ? modelMapper.map(product, ProductDTO.class) : null;
    }

    private Product convertToEntity(@NotNull ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    private void covnertPrice(ProductDTO productDTO, CurrencyEnum currency) throws ConverterException {
        if (productDTO != null && currency != null && !currency.equals(productDTO.getCurrency())) {
            Double convertedPrice = converterService.getConvertedPrice(productDTO.getPrice(), productDTO.getCurrency(), currency);
            productDTO.setPrice(convertedPrice);
            productDTO.setCurrency(currency);
        }
    }

    private FullProductDTO getFullProductDTO(ProductDTO productDTO) throws NotFoundException {
        CategoryTree categoryTree = categoriesService.getCategoryTree(productDTO.getCategoryId());
        FullProductDTO fullProductDTO = new FullProductDTO();
        fullProductDTO.setProductDTO(productDTO);
        fullProductDTO.setCategoryTree(categoryTree);
        return fullProductDTO;
    }
}
