package com.fritomix.erp.modules.products.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.modules.products.application.dto.response.ProductResponse;
import com.fritomix.erp.modules.products.application.mapper.ProductMapper;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.products.domain.repository.CategoryRepository;
import com.fritomix.erp.modules.products.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void findAll_shouldReturnPagedResponse() {
        Product product = Product.builder().id(1L).code("PROD-001").name("Test").build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 25);
        when(productRepository.search(eq(null), eq(pageable))).thenReturn(page);
        when(mapper.toResponse(any(Product.class))).thenReturn(
                ProductResponse.builder().id(1L).code("PROD-001").name("Test").build());

        PageResponse<ProductResponse> result = productService.findAll(null, pageable);

        assertEquals(1, result.content().size());
        assertEquals("PROD-001", result.content().get(0).code());
        assertEquals(25, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertFalse(result.last());
        verify(productRepository).search(eq(null), eq(pageable));
    }

    @Test
    void findAll_shouldTrimSearchTermAndBuildPattern() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(), pageable, 0);
        when(productRepository.search(eq("%papa%"), eq(pageable))).thenReturn(page);

        PageResponse<ProductResponse> result = productService.findAll("  papa  ", pageable);

        assertTrue(result.content().isEmpty());
        verify(productRepository).search(eq("%papa%"), eq(pageable));
    }
}