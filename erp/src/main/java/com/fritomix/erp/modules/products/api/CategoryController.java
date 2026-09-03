package com.fritomix.erp.modules.products.api;

import com.fritomix.erp.modules.products.application.service.CategoryService;
import com.fritomix.erp.modules.products.application.service.CategoryService.CategoryCreateRequest;
import com.fritomix.erp.modules.products.application.service.CategoryService.CategoryDTO;
import com.fritomix.erp.modules.products.application.service.CategoryService.CategoryGroupDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/groups")
    @PreAuthorize("hasAuthority('PERMISSION_PRODUCTS_VIEW')")
    public ResponseEntity<List<CategoryGroupDTO>> findAllGroups() {
        return ResponseEntity.ok(categoryService.findAllGroups());
    }

    @GetMapping("/groups/{groupId}/categories")
    @PreAuthorize("hasAuthority('PERMISSION_PRODUCTS_VIEW')")
    public ResponseEntity<List<CategoryDTO>> findChildrenByGroupId(@PathVariable Long groupId) {
        return ResponseEntity.ok(categoryService.findChildrenByGroupId(groupId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_PRODUCTS_VIEW')")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping("/groups")
    @PreAuthorize("hasAuthority('PERMISSION_PRODUCTS_CREATE')")
    public ResponseEntity<CategoryDTO> createGroup(@Valid @RequestBody CategoryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createGroup(request));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_PRODUCTS_CREATE')")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_PRODUCTS_EDIT')")
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryCreateRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @PutMapping("/{id}/image")
    @PreAuthorize("hasAuthority('PERMISSION_PRODUCTS_EDIT')")
    public ResponseEntity<CategoryDTO> updateImage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(categoryService.updateImage(id, body.get("image")));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_PRODUCTS_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
