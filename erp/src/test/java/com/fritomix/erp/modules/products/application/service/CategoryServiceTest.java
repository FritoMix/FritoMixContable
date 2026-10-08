package com.fritomix.erp.modules.products.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.products.application.service.CategoryService.CategoryCreateRequest;
import com.fritomix.erp.modules.products.application.service.CategoryService.CategoryDTO;
import com.fritomix.erp.modules.products.application.service.CategoryService.CategoryGroupDTO;
import com.fritomix.erp.modules.products.domain.entity.Category;
import com.fritomix.erp.modules.products.domain.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService service;

    private Category category(long id, String name, Category parent) {
        return Category.builder().id(id).name(name).description("d").parent(parent).build();
    }

    private void stubLeaf(long id, long items, long subcats) {
        when(categoryRepository.findByParentIdOrderByName(id)).thenReturn(List.of());
        when(categoryRepository.countProductsByCategoryIdRecursive(id)).thenReturn(items);
        when(categoryRepository.countSubcategoriesByParentId(id)).thenReturn(subcats);
    }

    @Test
    void findAllGroupsMapeaGrupoConHijos() {
        Category group = category(1L, "Bebidas", null);
        Category child = category(2L, "Gaseosas", group);
        when(categoryRepository.findByParentIsNullOrderByName()).thenReturn(List.of(group));
        when(categoryRepository.findByParentIdOrderByName(1L)).thenReturn(List.of(child));
        when(categoryRepository.countProductsByCategoryIdRecursive(1L)).thenReturn(10L);
        when(categoryRepository.countSubcategoriesByParentId(1L)).thenReturn(1L);
        stubLeaf(2L, 4, 0);

        List<CategoryGroupDTO> groups = service.findAllGroups();

        assertEquals(1, groups.size());
        assertEquals("Bebidas", groups.get(0).name());
        assertEquals(10, groups.get(0).itemCount());
        assertEquals(1, groups.get(0).subcategoriesCount());
        assertEquals(1, groups.get(0).children().size());
        CategoryDTO childDto = groups.get(0).children().get(0);
        assertEquals(2L, childDto.id());
        assertEquals(1L, childDto.parentId());
        assertEquals(4, childDto.itemCount());
    }

    @Test
    void findChildrenByGroupIdExistente() {
        Category group = category(1L, "Bebidas", null);
        Category child = category(2L, "Gaseosas", group);
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findByParentIdOrderByName(1L)).thenReturn(List.of(child));
        stubLeaf(2L, 0, 0);

        List<CategoryDTO> children = service.findChildrenByGroupId(1L);

        assertEquals(1, children.size());
        assertEquals("Gaseosas", children.get(0).name());
    }

    @Test
    void findChildrenByGroupIdInexistenteLanzaExcepcion() {
        when(categoryRepository.existsById(9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findChildrenByGroupId(9L));
    }

    @Test
    void findByIdEncontradoYNoEncontrado() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category(2L, "Gaseosas", null)));
        stubLeaf(2L, 3, 0);

        assertEquals("Gaseosas", service.findById(2L).name());

        when(categoryRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }

    @Test
    void updateImageValidaFormatoYGuardado() {
        Category c = category(1L, "Gaseosas", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(c));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));
        stubLeaf(1L, 0, 0);

        String uri = "data:image/png;base64,AAAA";
        CategoryDTO dto = service.updateImage(1L, uri);

        assertEquals(uri, dto.image());
    }

    @Test
    void updateImageRechazaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> service.updateImage(1L, "   "));
        assertThrows(IllegalArgumentException.class, () -> service.updateImage(1L, "http://x/imagen.png"));
        assertThrows(IllegalArgumentException.class,
                () -> service.updateImage(1L, "data:image/png;base64," + "A".repeat(1_500_000)));
    }

    @Test
    void createGroupDuplicadoLanzaExcepcion() {
        when(categoryRepository.findByName("Bebidas")).thenReturn(Optional.of(category(1L, "Bebidas", null)));

        assertThrows(IllegalArgumentException.class,
                () -> service.createGroup(new CategoryCreateRequest("Bebidas", "d", null)));
    }

    @Test
    void createGroupExitoso() {
        when(categoryRepository.findByName("Bebidas")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            if (c.getId() == null) c.setId(1L);
            return c;
        });
        stubLeaf(1L, 0, 0);

        CategoryDTO dto = service.createGroup(new CategoryCreateRequest("Bebidas", "desc", null));

        assertEquals("Bebidas", dto.name());
    }

    @Test
    void createCategorySinPadreLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createCategory(new CategoryCreateRequest("X", "d", null)));
    }

    @Test
    void createCategoryConPadreInexistenteLanzaExcepcion() {
        when(categoryRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.createCategory(new CategoryCreateRequest("X", "d", 9L)));
    }

    @Test
    void createCategoryExitoso() {
        Category parent = category(1L, "Bebidas", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            if (c.getId() == null) c.setId(5L);
            return c;
        });
        stubLeaf(5L, 0, 0);

        CategoryDTO dto = service.createCategory(new CategoryCreateRequest("Gaseosas", "d", 1L));

        assertEquals("Gaseosas", dto.name());
        assertEquals(1L, dto.parentId());
    }

    @Test
    void updateCambiaNombreYDescripcion() {
        Category c = category(2L, "Gaseosas", category(1L, "Bebidas", null));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(c));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));
        stubLeaf(2L, 0, 0);

        CategoryDTO dto = service.update(2L, new CategoryCreateRequest("Refrescos", "nueva desc", null));

        assertEquals("Refrescos", dto.name());
        assertEquals("nueva desc", dto.description());
    }

    @Test
    void deleteGrupoConHijosLanzaExcepcion() {
        Category group = category(1L, "Bebidas", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(group));
        when(categoryRepository.findByParentIdOrderByName(1L)).thenReturn(List.of(category(2L, "Hijo", group)));

        assertThrows(IllegalArgumentException.class, () -> service.delete(1L));
    }

    @Test
    void deleteCategoriaHojaExitoso() {
        Category leaf = category(2L, "Gaseosas", category(1L, "Bebidas", null));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(leaf));

        service.delete(2L);

        assertTrue(true);
    }

    @Test
    void deleteNoEncontradoLanzaExcepcion() {
        when(categoryRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(9L));
    }
}