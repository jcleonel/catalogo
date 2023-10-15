package com.jc.catalogo.services;

import com.jc.catalogo.dto.ProductDTO;
import com.jc.catalogo.entities.Category;
import com.jc.catalogo.entities.Product;
import com.jc.catalogo.repositories.CategoryRepository;
import com.jc.catalogo.repositories.ProductRepository;
import com.jc.catalogo.services.exceptions.DatabaseException;
import com.jc.catalogo.services.exceptions.ResourceNotFoundException;
import com.jc.catalogo.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    ProductDTO productDTO;
    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private Category category;
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;

        product = Factory.creatProduct();
        page = new PageImpl<>(List.of(product));

        category = Factory.creatCategory();

        productDTO = Factory.creatProductDTO();

        doReturn(true).when(productRepository).existsById(existingId);
        doNothing().when(productRepository).deleteById(existingId);

        doThrow(ResourceNotFoundException.class).when(productRepository).deleteById(nonExistingId);

        doReturn(true).when(productRepository).existsById(dependentId);
        doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);

        when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(productRepository.getReferenceById(existingId)).thenReturn(product);
        when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> productService.delete(dependentId));
        verify(productRepository, times(1)).existsById(dependentId);
        verify(productRepository, times(1)).deleteById(dependentId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.delete(nonExistingId));
        verify(productRepository, times(1)).existsById(nonExistingId);
        verify(productRepository, times(0)).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> productService.delete(existingId));
        verify(productRepository, times(1)).existsById(existingId);
        verify(productRepository, times(1)).deleteById(existingId);
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = productService.findAllPaged(pageable);
        Assertions.assertNotNull(result);
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = productService.findById(existingId);
        Assertions.assertNotNull(result);
        verify(productRepository, times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingId);
            verify(productRepository, times(1)).findById(nonExistingId);
        });
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = productService.update(existingId, productDTO);
        Assertions.assertNotNull(result);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(nonExistingId, productDTO);
        });
    }

}
