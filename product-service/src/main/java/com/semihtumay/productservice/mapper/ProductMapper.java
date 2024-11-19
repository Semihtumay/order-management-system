package com.semihtumay.productservice.mapper;

import com.semihtumay.productservice.dto.ProductDto;
import com.semihtumay.productservice.dto.request.CreateProductRequest;
import com.semihtumay.productservice.dto.response.CreateProductResponse;
import com.semihtumay.productservice.dto.response.UpdateProductResponse;
import com.semihtumay.productservice.model.Product;

public class ProductMapper {
    public static Product requestToProduct(CreateProductRequest request){
        Product product = new Product();

        product.setName(request.name());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setTax(request.tax());
        product.setDiscount(request.discount());

        return product;
    }

    public static CreateProductResponse productToResponse(Product product){
       return new CreateProductResponse(product.getId().toString(),
               product.getName(),
               product.getPrice(),
               product.getQuantity(),
               product.getTax(),
               product.getDiscount(),
               product.getTotalPrice());
    }

    public static UpdateProductResponse updateProductToResponse(Product product){
        return new UpdateProductResponse(product.getId().toString(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getTax(),
                product.getDiscount(),
                product.getTotalPrice());
    }

    public static ProductDto productToProductDto(Product product){
        return new ProductDto(product.getId().toString(), product.getName(), product.getPrice(), product.getQuantity(), product.getTax(), product.getDiscount(), product.getTotalPrice());

    }
}
