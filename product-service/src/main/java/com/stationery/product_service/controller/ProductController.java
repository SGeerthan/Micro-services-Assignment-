package com.stationery.product_service.controller;

import com.stationery.product_service.dto.CreateProductRequest;
import com.stationery.product_service.dto.UpdateProductRequest;
import com.stationery.product_service.dto.ProductResponse;
import com.stationery.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Product Service", description = "Product catalog and admin management endpoints")
public class ProductController {

    private final ProductService productService;

    // ─── Public Endpoints - All Users ───────────────────────────────────────

    /**
     * Get all products
     * @return List of all products
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Returns every product in the catalog")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Get product by ID
     * @param id Product ID
     * @return Product details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Returns a product by its numeric identifier")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Get all available products
     * @return List of available products
     */
    @GetMapping("/available")
    @Operation(summary = "Get available products", description = "Returns only products currently marked available")
    public ResponseEntity<List<ProductResponse>> getAvailableProducts() {
        return ResponseEntity.ok(productService.getAvailableProducts());
    }

    /**
     * Search product by name
     * @param name Product name
     * @return Product details
     */
    @GetMapping("/search/{name}")
    @Operation(summary = "Search product by name", description = "Finds a product by exact or matching name")
    public ResponseEntity<ProductResponse> getProductByName(@PathVariable String name) {
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    // ─── Admin-Only Endpoints ──────────────────────────────────────────────

    /**
     * Create a new product (ADMIN ONLY)
     * @param request Product details
     * @return Created product
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create product", description = "Creates a new product; admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid product payload"),
            @ApiResponse(responseCode = "403", description = "Admin role required")
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Update an existing product (ADMIN ONLY)
     * @param id Product ID
     * @param request Updated product details
     * @return Updated product
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product", description = "Updates an existing product by ID; admin only")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        ProductResponse updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Delete a product (ADMIN ONLY)
     * @param id Product ID
     * @return No content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product", description = "Deletes a product by ID; admin only")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Admin-Only Endpoints (Alternative route) ──────────────────────────

    /**
     * Admin - Create product
     * @param request Product details
     * @return Created product
     */
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin create product", description = "Alternate admin route for creating a product")
    public ResponseEntity<ProductResponse> adminCreateProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Admin - Update product
     * @param id Product ID
     * @param request Updated product details
     * @return Updated product
     */
    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin update product", description = "Alternate admin route for updating a product")
    public ResponseEntity<ProductResponse> adminUpdateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        ProductResponse updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Admin - Delete product
     * @param id Product ID
     * @return No content
     */
    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin delete product", description = "Alternate admin route for deleting a product")
    public ResponseEntity<Void> adminDeleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
