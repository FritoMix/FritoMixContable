package com.fritomix.erp.modules.products.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.products.application.dto.request.ProductRequest;
import com.fritomix.erp.modules.products.application.dto.response.ProductResponse;
import com.fritomix.erp.modules.products.application.mapper.ProductMapper;
import com.fritomix.erp.modules.products.domain.entity.Category;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.products.domain.repository.CategoryRepository;
import com.fritomix.erp.modules.products.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return mapper.toResponse(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + request.code());
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.categoryId()));

        Product product = Product.builder()
                .category(category)
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .unit(request.unit())
                .presentation(request.presentation() != null ? request.presentation() : 0)
                .weight(request.weight())
                .weightGrams(request.weightGrams() != null ? request.weightGrams() : 0)
                .pesoUnidad(request.pesoUnidad())
                .dimension(request.dimension())
                .pesoTotalCargue(request.pesoTotalCargue())
                .active(request.active() != null ? request.active() : true)
                .build();

        product = productRepository.save(product);
        return mapper.toResponse(product);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        if (request.code() != null && !request.code().equals(product.getCode())) {
            if (productRepository.existsByCode(request.code())) {
                throw new IllegalArgumentException("Ya existe otro producto con el código: " + request.code());
            }
            product.setCode(request.code());
        }

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.categoryId()));
            product.setCategory(category);
        }
        if (request.name() != null) product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.unit() != null) product.setUnit(request.unit());
        if (request.presentation() != null) product.setPresentation(request.presentation());
        if (request.weight() != null) product.setWeight(request.weight());
        if (request.weightGrams() != null) product.setWeightGrams(request.weightGrams());
        if (request.pesoUnidad() != null) product.setPesoUnidad(request.pesoUnidad());
        if (request.dimension() != null) product.setDimension(request.dimension());
        if (request.pesoTotalCargue() != null) product.setPesoTotalCargue(request.pesoTotalCargue());
        if (request.active() != null) product.setActive(request.active());

        productRepository.save(product);
        return mapper.toResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
    }
}
