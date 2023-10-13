package com.jc.catalogo.repositories;

import com.jc.catalogo.entities.Product;
import com.jc.catalogo.services.exceptions.ResourceNotFoundException;
import com.jc.catalogo.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void findByIdShouldReturnObjectWhenIdExists() {
        Optional<Product> result = productRepository.findById(existingId);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void findByIdNotShouldReturnObjectWhenIdNotExists() {
        Optional<Product> result = productRepository.findById(nonExistingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void saveShouldPersistWhitAutoincrementWhenIdIsNull() {
        Product product = Factory.creatProduct();
        product.setId(null);
        product = productRepository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        productRepository.deleteById(existingId);

        Optional<Product> result = productRepository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void existsByIdShouldFalseWhenIdNotExists() {
        Assertions.assertFalse(productRepository.existsById(nonExistingId));
    }

}
