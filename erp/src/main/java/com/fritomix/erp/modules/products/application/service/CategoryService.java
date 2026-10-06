package com.fritomix.erp.modules.products.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.products.domain.entity.Category;
import com.fritomix.erp.modules.products.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private static final Pattern IMAGE_DATA_URI = Pattern.compile("^data:image/(png|jpe?g|webp|gif|bmp);base64,.*$", Pattern.CASE_INSENSITIVE);

    public record CategoryDTO(Long id, String name, String description, String image, Long parentId, List<CategoryDTO> children, long itemCount, long subcategoriesCount) {}
    public record CategoryGroupDTO(Long id, String name, String description, String image, List<CategoryDTO> children, long itemCount, long subcategoriesCount) {}
    public record CategoryCreateRequest(String name, String description, Long parentId) {}

    private CategoryDTO mapToDTO(Category c) {
        Long parentId = c.getParent() != null ? c.getParent().getId() : null;
        List<CategoryDTO> children = categoryRepository.findByParentIdOrderByName(c.getId()).stream()
                .map(this::mapToDTO)
                .toList();
        long itemCount = categoryRepository.countProductsByCategoryIdRecursive(c.getId());
        long subcategoriesCount = categoryRepository.countSubcategoriesByParentId(c.getId());
        return new CategoryDTO(c.getId(), c.getName(), c.getDescription(), c.getImage(), parentId, children, itemCount, subcategoriesCount);
    }

    private CategoryGroupDTO mapToGroupDTO(Category g) {
        List<CategoryDTO> children = categoryRepository.findByParentIdOrderByName(g.getId()).stream()
                .map(this::mapToDTO)
                .toList();
        long itemCount = categoryRepository.countProductsByCategoryIdRecursive(g.getId());
        long subcategoriesCount = categoryRepository.countSubcategoriesByParentId(g.getId());
        return new CategoryGroupDTO(g.getId(), g.getName(), g.getDescription(), g.getImage(), children, itemCount, subcategoriesCount);
    }

    @Transactional(readOnly = true)
    public List<CategoryGroupDTO> findAllGroups() {
        List<Category> groups = categoryRepository.findByParentIsNullOrderByName();
        return groups.stream().map(this::mapToGroupDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findChildrenByGroupId(Long groupId) {
        if (!categoryRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Grupo no encontrado con id: " + groupId);
        }
        return categoryRepository.findByParentIdOrderByName(groupId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        return mapToDTO(category);
    }

    @Transactional
    public CategoryDTO updateImage(Long id, String imageDataUri) {
        if (imageDataUri == null || imageDataUri.isBlank()) {
            throw new IllegalArgumentException("La imagen es requerida");
        }
        if (!IMAGE_DATA_URI.matcher(imageDataUri).matches()) {
            throw new IllegalArgumentException("Formato de imagen no válido. Debe ser un data URI (png/jpg/webp/gif/bmp) en base64");
        }
        int maxLen = 1_500_000;
        if (imageDataUri.length() > maxLen) {
            throw new IllegalArgumentException("La imagen es demasiado grande (máximo ~1MB). Usá una imagen más pequeña.");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        category.setImage(imageDataUri);
        category = categoryRepository.save(category);
        return mapToDTO(category);
    }

    @Transactional
    public CategoryDTO createGroup(CategoryCreateRequest request) {
        if (categoryRepository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un grupo con el nombre: " + request.name());
        }
        Category group = Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
        group = categoryRepository.save(group);
        return mapToDTO(group);
    }

    @Transactional
    public CategoryDTO createCategory(CategoryCreateRequest request) {
        if (request.parentId() == null) {
            throw new IllegalArgumentException("Las categorías deben pertenecer a un grupo (parentId requerido)");
        }
        Category parent = categoryRepository.findById(request.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado con id: " + request.parentId()));
        if (categoryRepository.existsByNameAndParentId(request.name(), request.parentId())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre '" + request.name() + "' en este grupo");
        }
        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .parent(parent)
                .build();
        category = categoryRepository.save(category);
        return mapToDTO(category);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryCreateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));

        boolean isGroup = category.getParent() == null;

        if (request.name() != null && !request.name().equals(category.getName())) {
            if (isGroup) {
                if (categoryRepository.findByName(request.name()).isPresent()) {
                    throw new IllegalArgumentException("Ya existe un grupo con el nombre: " + request.name());
                }
            } else {
                Long parentId = category.getParent().getId();
                if (categoryRepository.existsByNameAndParentIdAndIdNot(request.name(), parentId, id)) {
                    throw new IllegalArgumentException("Ya existe una categoría con el nombre '" + request.name() + "' en este grupo");
                }
            }
            category.setName(request.name());
        }

        if (request.description() != null) {
            category.setDescription(request.description());
        }

        category = categoryRepository.save(category);
        return mapToDTO(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        boolean isGroup = category.getParent() == null;
        if (isGroup) {
            List<Category> children = categoryRepository.findByParentIdOrderByName(id);
            if (!children.isEmpty()) {
                throw new IllegalArgumentException("No se puede eliminar el grupo '" + category.getName() + "' porque tiene " + children.size() + " categorías. Eliminá las categorías primero.");
            }
        }
        categoryRepository.deleteById(id);
    }
}
