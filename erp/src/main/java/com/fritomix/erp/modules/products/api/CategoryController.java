package com.fritomix.erp.modules.products.api;

import com.fritomix.erp.modules.products.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    record CategoryDTO(Long id, String name) {}

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_PRODUCTS_VIEW')")
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> list = categoryRepository.findAll().stream()
                .map(c -> new CategoryDTO(c.getId(), c.getName()))
                .toList();
        return ResponseEntity.ok(list);
    }
}
