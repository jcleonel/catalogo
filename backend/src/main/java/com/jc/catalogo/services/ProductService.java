package com.jc.catalogo.services;

import com.jc.catalogo.dto.ProductDTO;
import com.jc.catalogo.entities.Category;
import com.jc.catalogo.entities.Product;
import com.jc.catalogo.repositories.CategoryRepository;
import com.jc.catalogo.repositories.ProductRepository;
import com.jc.catalogo.services.exceptions.DatabaseException;
import com.jc.catalogo.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
        Page<Product> list = productRepository.findAll(pageRequest);
        return list.map(x -> new ProductDTO(x));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        Product product = productOptional.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDTO(product, product.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO productDTO) {
        Product product = new Product();
        copyDtoToEntity(productDTO, product);
        product = productRepository.save(product);
        return new ProductDTO(product);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO) {
        try {
            Product product = productRepository.getReferenceById(id);
            copyDtoToEntity(productDTO, product);
            product = productRepository.save(product);
            return new ProductDTO(product);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
    }

    public void delete(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }

        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException | EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Entegrity Violation");
        }

    }

    private void copyDtoToEntity(ProductDTO productDTO, Product product) {
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setImgUrl(productDTO.getImgUrl());
        product.setDate(productDTO.getDate());

        product.getCategories().clear();
        productDTO.getCategories().forEach(categoryDTO -> {
            Category category = categoryRepository.getReferenceById(categoryDTO.getId());
            product.getCategories().add(category);
        });
    }

}
