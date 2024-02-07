package com.example.productDemo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.example.productDemo.Entity.Product;
import com.example.productDemo.Entity.Product.Offer;
import com.example.productDemo.Entity.Product.Review;
import com.example.productDemo.Service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    
    //**TO CREATE A PRODUCT**
    @Operation(
            summary = "Create a new product",
            description = "Create a new product with the provided details."
        )
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody Product product, BindingResult bindingResult) {
        try {
        	// Check if there are validation errors in the input
            if (bindingResult.hasErrors()) {
            	// Extract field errors and create a list of error messages
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                // Return a bad request response with validation error details
                return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Validation errors occurred. Please check your input", errors));
            }

            // If input is valid, proceed to create the product
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("201", "Product created successfully", createdProduct));
        } catch (IllegalArgumentException e) {
        	// Handle exceptions related to product creation and return a bad request response with error details
            List<String> errors = Collections.singletonList("Error occured in product creation");
            return ResponseEntity.badRequest().body(new ApiResponse<>("400", e.getMessage(), errors));
        }
    }

    //**TO RETRIEVE A PRODUCT**

    @Operation(
        summary = "Get product by ID",
        description = "Retrieve a product by its ID."
    )
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Optional<Product>>> getProduct(@PathVariable String productId) {
        Optional<Product> product = productService.getProduct(productId);

        // Check if the product is present in the optional result
        if (product.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>("200", "Product retrieved successfully", product));
        } else {
        	// If the product is not found, create an error response with status code 404 (Not Found)
            List<String> errors = Collections.singletonList("Error occured in retrieving the product");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("404", "Product with ID " + productId + " not found", errors));
        }
    }

    //**TO UPDATE A PRODUCT**
    @Operation(
            summary = "Update product by ID",
            description = "Update details of a product by its ID."
        )
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateProduct(@PathVariable String productId, @Valid @RequestBody Product updatedProduct,
                                                              BindingResult bindingResult) {
        try {
        	// Check if there are validation errors in the input
            if (bindingResult.hasErrors()) {
            	// Extract field errors and create a list of error messages
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                // Return a bad request response with validation error details
                return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Validation errors occurred. Please check your input", errors));
            }

            Optional<Product> existingProduct = productService.getProduct(productId);

            // Check if the product with the given ID exists
            if (existingProduct.isPresent()) {
                productService.updateProduct(productId, updatedProduct);

                // Create a response map containing updated product details
                Map<String, Object> response = new HashMap<>();
                response.put("name", updatedProduct.getName());
                response.put("description", updatedProduct.getDescription());
                response.put("price", updatedProduct.getPrice());

                return ResponseEntity.ok(new ApiResponse<>("200", "Product updated successfully", response));
            }else {
            	// If the product is not found, create an error response with status code 404 (Not Found)
                List<String> errors = Collections.singletonList("Error occured in updating the product");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("404", "Product with ID " + productId + " not found", errors));
            }
        } catch (IllegalArgumentException e) {
        	// Handle exceptions related to product updating and return a bad request response with error details
            List<String> errors = Collections.singletonList(e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Error in updating the product", errors));
        }
    }

    //**TO ADD REVIEW TO A PRODUCT**
    @Operation(
            summary = "Add review to product",
            description = "Add a review to the specified product by its ID."
    )
    @PostMapping("/reviews/{productId}")
    public ResponseEntity<ApiResponse<@Valid Review>> addReviewToProduct(@PathVariable String productId, @Valid @RequestBody Review review,
                                                                   BindingResult bindingResult) {
        try {
        	// Check if there are validation errors in the input
            if (bindingResult.hasErrors()) {
            	// Extract field errors and create a list of error messages
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                // Return a bad request response with validation error details
                return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Validation errors occurred. Please check your input", errors));
            }

            Optional<Product> existingProduct = productService.getProduct(productId);

            // Check if the product with the given ID exists
            if (existingProduct.isPresent()) {
                productService.addReviewToProduct(productId, review);
                return ResponseEntity.ok(new ApiResponse<>("200", "Reviews added successfully for product "+productId,review));
            } else {
            	// If the product is not found, create an error response with status code 404 (Not Found)
                List<String> errors = Collections.singletonList("Error occured in adding review to the product");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("404", "Product with ID " + productId + " not found", errors));
            }
        } catch (IllegalArgumentException e) {
        	// Handle exceptions related to adding a review and return a bad request response with error details
            List<String> errors = Collections.singletonList(e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Error in adding review to the product", errors));
        }
    }

    //**TO ADD OFFER TO A PRODUCT**
    @Operation(
            summary = "Add offer to product",
            description = "Add an offer to the specified product by its ID."
    )
    @PostMapping("/offers/{productId}")
    public ResponseEntity<ApiResponse<@Valid Offer>> addOfferToProduct(@PathVariable String productId, @Valid @RequestBody Offer offer,
                                                                  BindingResult bindingResult) {
        try {
        	// Check if there are validation errors in the input
            if (bindingResult.hasErrors()) {
            	// Extract field errors and create a list of error messages
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                // Return a bad request response with validation error details
                return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Validation errors occurred. Please check your input", errors));
            }

            Optional<Product> existingProduct = productService.getProduct(productId);

            // Check if the product with the given ID exists
            if (existingProduct.isPresent()) {
                productService.addOfferToProduct(productId, offer);
                return ResponseEntity.ok(new ApiResponse<>("200", "Offers added successfully for product "+productId,offer));
            } else {
            	// If the product is not found, create an error response with status code 404 (Not Found)
                List<String> errors = Collections.singletonList("Error occured in adding offer to the product");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("404", "Product with ID " + productId + " not found", errors));
            }
        } catch (IllegalArgumentException e) {
        	// Handle exceptions related to adding an offer and return a bad request response with error details
            List<String> errors = Collections.singletonList(e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Error in adding offer to the product", errors));
        }
    }

  
     //additional endPoints
    //***To retrieve summaries of all products***
    @Operation(
            summary = "Get all product summaries",
            description = "Retrieve summaries of all products in a paginated format."
    )
    @GetMapping("/summaries")
    public ResponseEntity<Object> getAllProductSummaries(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "4", required = false) int size
    ) {

        // Check if the requested page is valid
        if (page < 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Error: Invalid page number. Page number must be greater than or equal to 0.", null));
        }

        try {
           
            
            PageRequest pageRequest = PageRequest.of(page, size);

            // Retrieve reviews for the specified product
            Page<Map<String, Object>> productReviews = productService.getAllProductSummaries(pageRequest);

            // Check if there are no reviews
            if (productReviews.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>("200", "No reviews available.", null));
            }

            // Check if the requested page is out of bounds
            if (page >= productReviews.getTotalPages()) {
                // Handle the specific exception for an out-of-bounds page
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("404", "Page number exceeds, No reviews found for the specified page.", null));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("TotalPages", productReviews.getTotalPages());
            response.put("TotalElements", productReviews.getTotalElements());
            response.put("CurrentPage", productReviews.getNumber());
            response.put("PRODUCTS", productReviews.getContent());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Handle other exceptions if needed
            return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Error: Please provide valid values for size and page.", null));
        }
    }
    
    //***To Delete a product
    @Operation(
            summary = "Delete product by ID",
            description = "Delete a product by its ID."
        )
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Optional<Product>>> deleteProduct(@PathVariable String productId) {
        Optional<Product> existingProduct = productService.getProduct(productId);

        if (existingProduct.isPresent()) {
            //If Product found, delete it
            productService.deleteProduct(productId);

            return ResponseEntity.ok(new ApiResponse<>("200", "Product deleted successfully",existingProduct));
        } else {
            //If Product not found, return a not found response with a error message
            List<String> errors = Collections.singletonList("Error occured in deleting the product");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("404", "Product with ID " + productId + " not found", errors));
        }
    }

   //*** Retrieves product reviews in a paginated format****
    @Operation(
            summary = "Get reviews for product",
            description = "Retrieve reviews for the specified product in a paginated format."
    )
    @GetMapping("/allreviews/{productId}")
    public ResponseEntity<Object> getReviewsForProduct(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "4", required = false) int size) {

        // Check if the requested page is valid
        if (page < 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Error: Invalid page number. Page number must be greater than or equal to 0.", null));
        }

        try {
            // Check if the product ID exists
            Optional<Product> productOptional = productService.getProduct(productId);
            if (productOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("404", "Error: Product not found with ID " + productId, null));
            }

            PageRequest pageRequest = PageRequest.of(page, size);

            // Retrieve reviews for the specified product
            Page<Map<String, Object>> productReviews = productService.getReviewsForProduct(productId, pageRequest);

            // Check if there are no reviews
            if (productReviews.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>("200", "No reviews available.", null));
            }

            // Check if the requested page is out of bounds
            if (page >= productReviews.getTotalPages()) {
                // Handle the specific exception for an out-of-bounds page
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("404", "Page number exceeds, No reviews found for the specified page.", null));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("TotalPages", productReviews.getTotalPages());
            response.put("TotalElements", productReviews.getTotalElements());
            response.put("CurrentPage", productReviews.getNumber());
            response.put("PRODUCT REVIEWS", productReviews.getContent());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Handle other exceptions if needed
            return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Error: Please provide valid values for size and page.", null));
        }
    }
    
   
}


