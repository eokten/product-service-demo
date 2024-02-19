package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Product;
import org.example.mapper.ProductMapper;
import org.example.repository.ProductRepository;
import org.example.rest.model.ProductDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.fromDto(productDto);
        Product createdProduct = productRepository.save(product);
        return productMapper.toDto(createdProduct);
    }

    public Optional<ProductDto> findProduct(Long id) {
        return productRepository.findById(id).map(productMapper::toDto);
    }

    public ProductDto updateProduct(Long targetProductId, ProductDto source) {
        Product targetProduct = productRepository
                .findById(targetProductId)
                .orElseThrow();

        productMapper.updateProduct(targetProduct, source);

        Product modifiedProduct = productRepository.save(targetProduct);

        return productMapper.toDto(modifiedProduct);
    }

    public ProductDto patchProduct(Long targetProductId, ProductDto source) {
        Product targetProduct = productRepository
                .findById(targetProductId)
                .orElseThrow();

        productMapper.patchProduct(targetProduct, source);

        Product modifiedProduct = productRepository.save(targetProduct);

        return productMapper.toDto(modifiedProduct);
    }
}
