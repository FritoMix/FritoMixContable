package com.fritomix.erp.modules.products.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.products.application.dto.request.ProductRequest;
import com.fritomix.erp.modules.products.application.dto.response.ProductResponse;
import com.fritomix.erp.modules.products.application.mapper.ProductMapper;
import com.fritomix.erp.modules.products.domain.entity.Category;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.products.domain.repository.CategoryRepository;
import com.fritomix.erp.modules.products.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    private ProductService service;

    private Product product() {
        return Product.builder().id(1L).code("P-1").name("Harina").category(Category.builder().id(1L).build()).build();
    }

    @Test
    void findAllMapeaPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        Product p = product();
        when(productRepository.search(null, pageable)).thenReturn(new PageImpl<>(List.of(p), pageable, 1));
        when(mapper.toResponse(p)).thenReturn(mock(ProductResponse.class));

        assertEquals(1, service.findAll(null, pageable).content().size());
    }

    @Test
    void findAllRecortaTerminoDeBusqueda() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.search("%harina%", pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        service.findAll("  harina  ", pageable);

        verify(productRepository).search("%harina%", pageable);
    }

    @Test
    void findByIdEncontradoYNoEncontrado() {
        Product p = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(mapper.toResponse(p)).thenReturn(mock(ProductResponse.class));

        assertNotNull(service.findById(1L));

        when(productRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }

    @Test
    void createAplicaValoresPorDefecto() {
        ProductRequest request = ProductRequest.builder()
                .categoryId(1L).code("P-1").name("Harina").build();
        when(productRepository.existsByCode("P-1")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(Category.builder().id(1L).build()));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Product.class))).thenReturn(mock(ProductResponse.class));

        service.create(request);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createConCodigoDuplicadoLanzaExcepcion() {
        when(productRepository.existsByCode("P-1")).thenReturn(true);

        ProductRequest request = ProductRequest.builder().categoryId(1L).code("P-1").build();

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    void createConCategoriaInexistenteLanzaExcepcion() {
        when(productRepository.existsByCode("P-1")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ProductRequest request = ProductRequest.builder().categoryId(1L).code("P-1").build();

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void updateCambiaCampos() {
        Product p = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(Category.builder().id(2L).build()));
        when(productRepository.existsByCode("P-2")).thenReturn(false);
        when(mapper.toResponse(p)).thenReturn(mock(ProductResponse.class));

        ProductRequest request = ProductRequest.builder()
                .code("P-2").categoryId(2L).name("Trigo").active(false).build();

        service.update(1L, request);

        assertEquals("P-2", p.getCode());
        assertEquals("Trigo", p.getName());
        assertTrue(!p.getActive());
        verify(productRepository).save(p);
    }

    @Test
    void updateConCodigoDuplicadoLanzaExcepcion() {
        Product p = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(productRepository.existsByCode("P-2")).thenReturn(true);

        ProductRequest request = ProductRequest.builder().code("P-2").build();

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, request));
    }

    @Test
    void updateConCategoriaInexistenteLanzaExcepcion() {
        Product p = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        ProductRequest request = ProductRequest.builder().categoryId(2L).build();

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, request));
    }

    @Test
    void updateNoEncontradoLanzaExcepcion() {
        when(productRepository.findById(9L)).thenReturn(Optional.empty());

        ProductRequest request = ProductRequest.builder().build();

        assertThrows(ResourceNotFoundException.class, () -> service.update(9L, request));
    }

    @Test
    void deleteExitosoYNoEncontrado() {
        when(productRepository.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(productRepository).deleteById(1L);

        when(productRepository.existsById(9L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.delete(9L));
    }
}