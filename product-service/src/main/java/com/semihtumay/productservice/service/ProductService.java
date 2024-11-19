package com.semihtumay.productservice.service;

import com.semihtumay.productservice.dto.ProductDto;
import com.semihtumay.productservice.dto.request.CreateProductRequest;
import com.semihtumay.productservice.dto.request.UpdateProductRequest;
import com.semihtumay.productservice.dto.response.CreateProductResponse;
import com.semihtumay.productservice.dto.response.UpdateProductResponse;
import com.semihtumay.productservice.exception.InsufficientStockException;
import com.semihtumay.productservice.exception.ProductNotFoundException;
import com.semihtumay.productservice.mapper.ProductMapper;
import com.semihtumay.productservice.model.Product;
import com.semihtumay.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public CreateProductResponse createProduct(CreateProductRequest request) {
        Product product = ProductMapper.requestToProduct(request);
        productRepository.save(product);

        return ProductMapper.productToResponse(product);
    }

    public UpdateProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + productId));

        existingProduct.setName(request.name());
        existingProduct.setPrice(request.price());
        existingProduct.setQuantity(request.quantity());
        existingProduct.setTax(request.tax());
        existingProduct.setDiscount(request.discount());

        productRepository.save(existingProduct);


        return ProductMapper.updateProductToResponse(existingProduct);
    }

    public void deleteProduct(UUID productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + productId));

        existingProduct.setDeleted(true);
        productRepository.save(existingProduct);
    }

    @Transactional(readOnly = true)
    public ProductDto checkStock(UUID productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + productId));

         if(product.getQuantity() >= quantity){
             return ProductMapper.productToProductDto(product);
         }else {
             throw new InsufficientStockException("Insufficient stock for product ID: " + productId);
         }
    }

    @Transactional
    public void decreaseStock(UUID productId, Integer quantity){
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + productId));

        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException("Not enough stock for product with id " + productId);
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }
}
