package com.fritomix.erp.modules.products.application.mapper;

import com.fritomix.erp.modules.products.application.dto.response.ProductResponse;
import com.fritomix.erp.modules.products.domain.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .unit(product.getUnit())
                .active(product.getActive())
                .presentation(product.getPresentation())
                .weight(product.getWeight())
                .weightGrams(product.getWeightGrams())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .pesoUnidad(product.getPesoUnidad())
                .dimension(product.getDimension())
                .pesoTotalCargue(product.getPesoTotalCargue())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
