package com.semihtumay.productservice.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Table(name = "products")
@SQLDelete(sql = "UPDATE products SET is_deleted = true WHERE id = ?")
@FilterDef(name = "deletedProductFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedProductFilter", condition = "is_deleted = :isDeleted")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "price", nullable = false, precision = 10, scale = 3)
    private BigDecimal price;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "tax", nullable = false, precision = 5, scale = 2)
    private BigDecimal tax;
    @Column(name = "discount", nullable = false, precision = 5, scale = 2)
    private BigDecimal discount;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "total_price", nullable = false, precision = 10, scale = 3)
    private BigDecimal totalPrice;
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    @PrePersist
    public void prePersist() {
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }

        if (this.totalPrice == null){
            calculateTotalPrice();
        }
    }

    private void calculateTotalPrice() {
        if (this.price != null && this.tax != null && this.discount != null) {
            BigDecimal discountAmount = this.price.multiply(this.discount);
            BigDecimal afterDiscountAmount = this.price.subtract(discountAmount);
            BigDecimal taxAmount = afterDiscountAmount.multiply(this.tax);
            this.totalPrice = afterDiscountAmount.add(taxAmount);
            this.totalPrice = this.totalPrice.setScale(2, RoundingMode.HALF_UP);
        }
    }

}
