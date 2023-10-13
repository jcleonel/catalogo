package com.jc.catalogo.tests;

import com.jc.catalogo.dto.ProductDTO;
import com.jc.catalogo.entities.Category;
import com.jc.catalogo.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product creatProduct() {
        Product product = new Product(1L, "Phone", "Goog Phone", 800.0, "https://img.com/img.png", Instant.parse("2023-10-12T16:30:00Z"));
        product.getCategories().add(new Category(2L, "Electronics"));
        return product;
    }

    public static ProductDTO creatProductDTO() {
        Product product = creatProduct();
        return new ProductDTO(product, product.getCategories());
    }
}
