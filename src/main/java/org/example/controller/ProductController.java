package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.rest.controller.ProductsApi;
import org.example.rest.model.ProductDto;
import org.example.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

    private final ProductService productService;

    @Override
    public ResponseEntity<ProductDto> getProduct(Long id) {
        return ResponseEntity.of(productService.findProduct(id));
    }

    @Override
    public ResponseEntity<ProductDto> createProduct(ProductDto productDto) {
        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    // other methods implementation...
}
