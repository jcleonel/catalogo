package com.jc.catalogo.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jc.catalogo.dto.ProductDTO;
import com.jc.catalogo.services.ProductService;
import com.jc.catalogo.services.exceptions.DatabaseException;
import com.jc.catalogo.services.exceptions.ResourceNotFoundException;
import com.jc.catalogo.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProcuctResourcesTests {

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private ProductDTO productDTO;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 2L;
        productDTO = Factory.creatProductDTO();
        PageImpl<ProductDTO> page = new PageImpl<>(List.of(productDTO));

        when(productService.findAllPaged(any())).thenReturn(page);

        when(productService.findById(existingId)).thenReturn(productDTO);
        when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(productService.update(eq(existingId), any())).thenReturn(productDTO);
        when(productService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(productService).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        doThrow(DatabaseException.class).when(productService).delete(dependentId);

        when(productService.insert(any())).thenReturn(productDTO);
    }

    @Test
    public void findAllShouldReturnPaged() throws Exception {
        mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        mockMvc.perform(get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        String jsonBady = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBady)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        String jsonBady = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBady)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnProductDTOCreated() throws Exception {
        String jsonBady = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(post("/products")
                        .content(jsonBady)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void deleteShouldReturnNotContentWhenIdExists() throws Exception {
        mockMvc.perform(delete("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundtWhenIdDoesNotExists() throws Exception {
        mockMvc.perform(delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnBadRequestWhenIdIdHasDependent() throws Exception {
        mockMvc.perform(delete("/products/{id}", dependentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
