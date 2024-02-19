package org.example.mapper;

import org.example.entity.Product;
import org.example.rest.model.ProductDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface ProductMapper {
    @Mapping(target = "status", source = "quantity", qualifiedByName = "parseStatus")
    ProductDto toDto(Product product);

    Product fromDto(ProductDto productDto);

    void updateProduct(@MappingTarget Product target, ProductDto source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchProduct(@MappingTarget Product target, ProductDto source);

    @Named("parseStatus")
    default ProductDto.StatusEnum parseStatus(Integer quantity) {
        if (quantity == 0) {
            return ProductDto.StatusEnum.OUT_OF_STOCK;
        } else {
            return ProductDto.StatusEnum.IN_STOCK;
        }
    }
}
