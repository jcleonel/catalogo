package com.jc.catalogo.resources;

import com.jc.catalogo.dto.ProductDTO;
import com.jc.catalogo.services.ProductService;
import com.jc.catalogo.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProcuctResourcesTests {

    private ProductDTO productDTO;

    private PageImpl<ProductDTO> page;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() throws Exception {
        productDTO = Factory.creatProductDTO();
        page = new PageImpl<>(List.of(productDTO));

        when(productService.findAllPaged(any())).thenReturn(page);
    }

    @Test
    public void findAllShouldReturnPaged() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }
}
