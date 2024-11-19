package com.semihtumay.productservice.controller;

import com.semihtumay.productservice.dto.ProductDto;
import com.semihtumay.productservice.dto.request.CreateProductRequest;
import com.semihtumay.productservice.dto.request.UpdateProductRequest;
import com.semihtumay.productservice.dto.response.CreateProductResponse;
import com.semihtumay.productservice.dto.response.UpdateProductResponse;
import com.semihtumay.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<CreateProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request){
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateProductResponse> updateProduct(@PathVariable("id") UUID productId, @RequestBody UpdateProductRequest request){
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") UUID productId){
        productService.deleteProduct(productId);
    }

    @GetMapping("/{id}/check-stock")
    public ProductDto checkStock(@PathVariable("id") UUID productId, @RequestParam("quantity") Integer quantity){
        return productService.checkStock(productId, quantity);
    };

    @PostMapping("/{id}/decrease-stock")
    public void decreaseStock(@PathVariable("id") UUID productId, @RequestParam("quantity") Integer quantity){
        productService.decreaseStock(productId, quantity);
    };
}
