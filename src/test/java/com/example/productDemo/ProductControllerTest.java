package com.example.productDemo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

import com.example.productDemo.Controller.ApiResponse;
import com.example.productDemo.Controller.ProductController;
import com.example.productDemo.Entity.Product;
import com.example.productDemo.Entity.Product.Offer;
import com.example.productDemo.Entity.Product.Review;
import com.example.productDemo.Repository.ProductRepository;
import com.example.productDemo.Service.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;
    
    @Mock
    private ProductRepository productRepository; 

    /**
     * Test case for successful creation of a product.
     * Scenario: When a valid product object is provided with no validation errors, 
     * it should be successfully created by the product controller.
     */
    @Test
    public void testCreateProduct_Success() {
        // Arrange
        Product product = new Product("Product1", "Description1", 1789.0, null, null, null); 
        BindingResult bindingResult = mock(BindingResult.class);

        // Mock the behavior to simulate no validation errors
        when(bindingResult.hasErrors()).thenReturn(false);

        // Mock the productService to return the product when createProduct is called
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        // Act
        ResponseEntity<ApiResponse<Product>> responseEntity = productController.createProduct(product, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Product created successfully", responseEntity.getBody().getMessage());
        assertNotNull(responseEntity.getBody().getData());
    }

    /**
     * Test case for handling validation errors during product creation.
     * Scenario: When an invalid product object with validation errors is provided, 
     * the product controller should return a BAD_REQUEST response with appropriate error message.
     */
    @Test
    public void testCreateProduct_ValidationErrors() {
        // Arrange
        Product product = new Product("", "Description1", 1789.0, null, null, null);  // Set name to an empty string
        BindingResult bindingResult = mock(BindingResult.class);

        // Mock the behavior to simulate validation errors
        when(bindingResult.hasErrors()).thenReturn(true);

        // Create a List<FieldError> with a field error for the "name" field indicating it is required
        List<FieldError> fieldErrors = Collections.singletonList(new FieldError("objectName", "name", "Name is required"));
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Act
        ResponseEntity<ApiResponse<Product>> responseEntity = productController.createProduct(product, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Validation errors occurred. Please check your input", responseEntity.getBody().getMessage());
    }

    /**
     * Test case for handling IllegalArgumentException during product creation.
     * Scenario: When attempting to create a product with a duplicate name, 
     * the product controller should return a BAD_REQUEST response with appropriate error message.
     */
    @Test
    public void testCreateProduct_IllegalArgumentException() {
        // Arrange
        Product productWithDuplicateName = new Product("Product1", "Description", 789.0, null, null, null);
        BindingResult bindingResult = mock(BindingResult.class);

        // Mock the behavior to simulate no validation errors
        when(bindingResult.hasErrors()).thenReturn(false);

        // Mock the productService to throw an IllegalArgumentException
        when(productService.createProduct(any(Product.class))).thenThrow(new IllegalArgumentException("Name must be unique"));

        // Act
        ResponseEntity<ApiResponse<Product>> responseEntity = productController.createProduct(productWithDuplicateName, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Name must be unique", responseEntity.getBody().getMessage());
    }

    /**
     * Test case for successful update of a product.
     * Scenario: When updating an existing product with valid data and no validation errors, 
     * the product controller should return an OK response with the updated product details.
     */
    @Test
    public void testUpdateProduct_Success() {
        // Arrange
        String productId = "PDNO_00001";
        Product updatedProduct = new Product("UpdatedProduct", "UpdatedDescription", 2000.0, null, null, null);
        BindingResult bindingResult = mock(BindingResult.class);
        
        // Mock the behavior to simulate no validation errors
        when(bindingResult.hasErrors()).thenReturn(false);

        // Mock the productService to return an existing product
        when(productService.getProduct(productId)).thenReturn(Optional.of(new Product("ExistingProduct", "ExistingDescription", 1000.0, null, null, null)));

        // Act
        ResponseEntity<ApiResponse<Map<String, Object>>> responseEntity = productController.updateProduct(productId, updatedProduct, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Product updated successfully", responseEntity.getBody().getMessage());
        assertNotNull(responseEntity.getBody().getData());
        assertEquals("UpdatedProduct", responseEntity.getBody().getData().get("name"));
        assertEquals("UpdatedDescription", responseEntity.getBody().getData().get("description"));
        assertEquals(2000.0, responseEntity.getBody().getData().get("price"));
    }
    
    /**
     * Test case for updating a product that is not found.
     * Scenario: When attempting to update a product with a non-existent ID, 
     * the product controller should return a NOT_FOUND response with an appropriate error message.
     */
    @Test
    public void testUpdateProduct_NotFound() {
        // Arrange
        String productId = "PDNO_10557";
        Product updatedProduct = new Product("UpdatedProduct", "UpdatedDescription", 2000.0, null, null, null);
        BindingResult bindingResult = mock(BindingResult.class);

        // Mock the behavior to simulate no validation errors
        when(bindingResult.hasErrors()).thenReturn(false);

        // Mock the productService to return an empty Optional, indicating product not found
        when(productService.getProduct(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<Map<String, Object>>> responseEntity = productController.updateProduct(productId, updatedProduct, bindingResult);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Product with ID " + productId + " not found", responseEntity.getBody().getMessage());
    }

    /**
     * Test case for updating a product with validation errors.
     * Scenario: When attempting to update a product with invalid data (e.g., negative price), 
     * the product controller should return a BAD_REQUEST response with appropriate error message.
     */
    @Test
    public void testUpdateProduct_ValidationErrors() {
        // Arrange
        String productId = "PDNO_00001";
        Product updatedProduct = new Product("UpdatedProduct", "UpdatedDescription", -2000.0, null, null, null);  // Set price to a negative value
        BindingResult bindingResult = mock(BindingResult.class);

        // Mock the behavior to simulate validation errors
        when(bindingResult.hasErrors()).thenReturn(true);

        // Create a List<FieldError> with a field error for the "price" field indicating it is invalid
        List<FieldError> fieldErrors = Collections.singletonList(new FieldError("objectName", "price", "Price must be greater than zero"));
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Act
        ResponseEntity<ApiResponse<Map<String, Object>>> responseEntity = productController.updateProduct(productId, updatedProduct, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Validation errors occurred. Please check your input", responseEntity.getBody().getMessage());
    }
    
    /**
     * Test case for adding a review to a product successfully.
     * Scenario: When adding a review to an existing product with valid data and no validation errors, 
     * the product controller should return an OK response with the added review details.
     */
    @Test
    public void testAddReviewToProduct_Success() {
        // Arrange
        String productId = "PDNO_00001";
        Review review = new Review("John Doe","Good product!",4.8);
        BindingResult bindingResult = mock(BindingResult.class);

        // Mock the behavior to simulate no validation errors
        when(bindingResult.hasErrors()).thenReturn(false);

        // Mock the productService to return an existing product
        when(productService.getProduct(productId)).thenReturn(Optional.of(new Product("Product1", "ExistingDescription", 1000.0, null, null, null)));

        // Act
        ResponseEntity<ApiResponse<Review>> responseEntity = productController.addReviewToProduct(productId, review, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Reviews added successfully for product " + productId, responseEntity.getBody().getMessage());
        assertNotNull(responseEntity.getBody().getData());
        assertEquals("Good product!", responseEntity.getBody().getData().getComments());
        assertEquals("John Doe", responseEntity.getBody().getData().getReviewer());
    }

    /**
     * Test case for adding a review to a product that is not found.
     * Scenario: When attempting to add a review to a product with a non-existent ID, 
     * the product controller should return a NOT_FOUND response with an appropriate error message.
     */
    @Test
    public void testAddReviewToProduct_NotFound() {
        // Arrange
        String productId = "PDNO_00005";
        Review review = new Review("Jane Doe","Great product!",4.6);
        BindingResult bindingResult = mock(BindingResult.class);

        // Mock the behavior to simulate no validation errors
        when(bindingResult.hasErrors()).thenReturn(false);

        // Mock the productService to return an empty Optional, indicating product not found
        when(productService.getProduct(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<Review>> responseEntity = productController.addReviewToProduct(productId, review, bindingResult);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Product with ID " + productId + " not found", responseEntity.getBody().getMessage());
    }


    /**
     * Test case for adding a review to a product with validation errors.
     * Scenario: When attempting to add a review to a product with invalid data (e.g., empty comments), 
     * the product controller should return a BAD_REQUEST response with appropriate error message.
     */
    @Test
    public void testAddReviewToProduct_ValidationErrors() {
        // Arrange
        String productId = "PDNO_00001";
        Review review = new Review("Messi","",3.9);  // Set comments to an empty string
        BindingResult bindingResult = mock(BindingResult.class);

        // Mock the behavior to simulate validation errors
        when(bindingResult.hasErrors()).thenReturn(true);

        // Create a List<FieldError> with a field error for the "content" field indicating it is required
        List<FieldError> fieldErrors = Collections.singletonList(new FieldError("objectName", "comments", "comments are required"));
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Act
        ResponseEntity<ApiResponse<Review>> responseEntity = productController.addReviewToProduct(productId, review, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Validation errors occurred. Please check your input", responseEntity.getBody().getMessage());
    }

    /**
     * Test case for creating an offer for a product successfully.
     * Scenario: When adding an offer to an existing product with valid data and no validation errors, 
     * the product controller should return an OK response with the added offer details.
     */
    @Test
    public void testCreateOffer_Success() {
        // Arrange
    	 String productId = "PDNO_00001";
        Offer offer = new Offer("20% discount", "SAVE20", LocalDate.now(), LocalDate.now().plusDays(30));
        BindingResult bindingResult = mock(BindingResult.class);

        // Mock the behavior to simulate no validation errors
        when(bindingResult.hasErrors()).thenReturn(false);

     // Mock the productService to return an existing product
        when(productService.getProduct(productId)).thenReturn(Optional.of(new Product("Product1", "ExistingDescription", 1000.0, null, null, null)));

        // Act
        ResponseEntity<ApiResponse<Offer>> responseEntity = productController.addOfferToProduct(productId, offer, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Offers added successfully for product "+productId, responseEntity.getBody().getMessage());
        assertNotNull(responseEntity.getBody().getData());
        assertEquals("20% discount", responseEntity.getBody().getData().getOfferdetails());
        assertEquals("SAVE20", responseEntity.getBody().getData().getCouponCode());
    }

    /**
     * Test case for creating an offer for a product with validation errors.
     * Scenario: When attempting to add an offer to a product with invalid data (e.g., empty offer details), 
     * the product controller should return a BAD_REQUEST response with appropriate error message.
     */
    @Test
    public void testCreateOffer_ValidationErrors() {
        // Arrange
    	 String productId = "PDNO_00001";
        Offer offer = new Offer("", "SAVE30", LocalDate.now(), LocalDate.now().plusDays(15));  // Set offer details to an empty string
        BindingResult bindingResult = mock(BindingResult.class);

        // Mock the behavior to simulate validation errors
        when(bindingResult.hasErrors()).thenReturn(true);

        // Create a List<FieldError> with a field error for the "offerdetails" field indicating it is required
        List<FieldError> fieldErrors = Collections.singletonList(new FieldError("objectName", "offerdetails", "Offer details are required"));
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Act
        ResponseEntity<ApiResponse<Offer>> responseEntity = productController.addOfferToProduct(productId, offer, bindingResult);


        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Validation errors occurred. Please check your input", responseEntity.getBody().getMessage());
    }

    /**
     * Test case for adding an offer to a product that is not found.
     * Scenario: When attempting to add an offer to a product with a non-existent ID, 
     * the product controller should return a NOT_FOUND response with an appropriate error message.
     */
    @Test
    public void testAddOfferToProduct_ProductNotFound() {
        // Arrange
    	 String productId = "PDNO_00001";
        Offer offer = new Offer("15% discount", "SAVE15", LocalDate.now(), LocalDate.now().plusDays(20));
        BindingResult bindingResult = mock(BindingResult.class);

        // Mock the behavior to simulate no validation errors
        when(bindingResult.hasErrors()).thenReturn(false);

        // Mock the productService to return an empty Optional, indicating product not found
        when(productService.getProduct(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<Offer>> responseEntity = productController.addOfferToProduct(productId, offer, bindingResult);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Product with ID " + productId + " not found", responseEntity.getBody().getMessage());
    }

    /**
     * Test case for retrieving a product successfully.
     * Scenario: When attempting to retrieve a product with a valid product ID, 
     * the product controller should return an OK response with the product details.
     */
    @Test
    public void testGetProduct_Success() {
        // Arrange
        String productId = "PDNO_00001";
        when(productService.getProduct(productId)).thenReturn(Optional.of(new Product("Product1", "Description1", 100.0, null, null, null)));

        // Act
        ResponseEntity<ApiResponse<Optional<Product>>> responseEntity = productController.getProduct(productId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Product retrieved successfully", responseEntity.getBody().getMessage());
        assertTrue(responseEntity.getBody().getData().isPresent());
        assertEquals("Product1", responseEntity.getBody().getData().get().getName());
    }

    /**
     * Test case for retrieving a non-existent product.
     * Scenario: When attempting to retrieve a product with an invalid/non-existent product ID, 
     * the product controller should return a NOT_FOUND response with an appropriate error message.
     */
    @Test
    public void testGetProduct_NotFound() {
        // Arrange
        String productId = "PDNO_00006";
        
        // Mock the productService to return an empty Optional, indicating product not found
        when(productService.getProduct(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<Optional<Product>>> responseEntity = productController.getProduct(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Product with ID " + productId + " not found", responseEntity.getBody().getMessage());
        
    }

    /**
     * Test case for retrieving a product that does not exist.
     * Scenario: When attempting to retrieve a product with a valid product ID that does not exist, 
     * the product controller should return a NOT_FOUND response.
     */
    @Test
    public void testGetProduct_ValidSuccess() {
    	 // Arrange
        String productId = "PDNO_00006";
        // Mock the productService to return an empty Optional, indicating product not found
        when(productService.getProduct(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<Optional<Product>>> responseEntity = productController.getProduct(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
      
    }
    
    /**
     * Test case for successfully retrieving product reviews.
     * Scenario: When attempting to retrieve reviews for a product with existing reviews,
     * the product controller should return a page of reviews with a status code of 200 (OK).
     */
    @Test
    public void testGetReviewsForProduct_Successful() {
        // Arrange

    	String productId="PDNO_00001";
    	List<Review> mockedReviews = Arrays.asList(
    			new Review("John Doe", "Excellent review", 5),
                new Review("Jane Smith", "Good review", 4),
                new Review("Bob Johnson", "Average review", 3)
    	);
        // Mocking the ProductRepository to return a product when findById is called
        // Mock the productService to return an existing product
        when(productService.getProduct(productId)).thenReturn(Optional.of(new Product("Product1", "ExistingDescription", 1000.0, mockedReviews, null, null)));


        // Mocking the ProductService to return a page of reviews (assuming positive scenario)
        when(productService.getReviewsForProduct(eq("PDNO_00001"), any(PageRequest.class)))
            .thenReturn(createMockedReviewsPage());

        // Act
        ResponseEntity<Object> response = productController.getReviewsForProduct("PDNO_00001", 0, 4);

        // Assert

        // Checking that the HTTP status code is 200 (OK)
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Checking that the response body is not null
        assertNotNull(response.getBody());

        // Extracting the response body for further assertions
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        // Add assertions based on the expected response structure and data

       

        List<Map<String, Object>> productReviews = (List<Map<String, Object>>) responseBody.get("PRODUCT REVIEWS");
        assertNotNull(productReviews);
        assertEquals(3, productReviews.size()); // Assuming 3 reviews in this case
    }

    // Helper method to create a mock Page of reviews
    private Page<Map<String, Object>> createMockedReviewsPage() {
        List<Review> mockedReviews = Arrays.asList(
            new Review("John Doe", "Excellent review", 5),
            new Review("Jane Smith", "Good review", 4),
            new Review("Bob Johnson", "Average review", 3)
            // Add more reviews as needed
        );

        List<Map<String, Object>> reviewMaps = mockedReviews.stream()
            .map(review -> {
                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("name", review.getReviewer());
                reviewMap.put("rating", review.getRating());
                reviewMap.put("comments", review.getComments());
                // Add other fields as needed
                return reviewMap;
            })
            .collect(Collectors.toList());

        // Assuming PageRequest is used for pagination
        PageRequest pageRequest = PageRequest.of(0, 4);

        return new PageImpl<>(reviewMaps, pageRequest, mockedReviews.size());
    }
    
    /**
     * Test case for retrieving reviews for a non-existent product.
     * Scenario: When attempting to retrieve reviews for a product that does not exist,
     * the product controller should return a NOT_FOUND response with an appropriate error message.
     */
    @Test
    public void testGetReviewsForProduct_ProductNotFound() {
        // Arrange
        String nonExistingProductId = "NonExistingPD123";

        // Mocking the ProductService to return an empty Optional, simulating a product not found scenario
        when(productService.getProduct(nonExistingProductId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = productController.getReviewsForProduct(nonExistingProductId, 0, 4);

        // Assert

        // Checking that the HTTP status code is 404 (NOT_FOUND) in this case
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Checking that the response body is not null
        assertNotNull(response.getBody());

        // Assuming ApiResponse class structure
        assertTrue(response.getBody() instanceof ApiResponse);

        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertNotNull(apiResponse);

        // Add assertions based on the expected response structure and data for a product not found scenario
        assertEquals("404", apiResponse.getCode()); 
        assertEquals("Error: Product not found with ID " + nonExistingProductId, apiResponse.getMessage());
       
    }

    /**
     * Test case for retrieving reviews with an invalid page number.
     * Scenario: When attempting to retrieve reviews with an invalid page number,
     * the product controller should return a BAD_REQUEST response with an appropriate error message.
     */
    @Test
    public void testGetReviewsForProduct_InvalidPageNumber() {
        // Arrange
        String existingProductId = "PDNO_00123";
        int invalidPageNumber = -1;

        // Act
        ResponseEntity<Object> response = productController.getReviewsForProduct(existingProductId, invalidPageNumber, 4);

        // Assert

        // Checking that the HTTP status code is 400 (Bad Request) for an invalid page number
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Checking that the response body is not null
        assertNotNull(response.getBody());

        // Assuming ApiResponse class structure
        assertTrue(response.getBody() instanceof ApiResponse);

        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertNotNull(apiResponse);
        
        assertEquals("400", apiResponse.getCode()); 
        assertEquals("Error: Invalid page number. Page number must be greater than or equal to 0.", apiResponse.getMessage());
      
    }

    /**
     * Test case for successfully deleting a product.
     * Scenario: When attempting to delete a product that exists,
     * the product controller should return an OK response with a success message.
     */
    @Test
    public void testDeleteProduct_SuccessfulDeletion() {
        // Arrange
        String productId = "PDNO_00123";
        when(productService.getProduct(productId)).thenReturn(Optional.of(new Product()));

        // Act
        ResponseEntity<ApiResponse<Optional<Product>>> responseEntity = productController.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Product deleted successfully", responseEntity.getBody().getMessage());
        assertTrue(responseEntity.getBody().getData().isPresent());
    }

    /**
     * Test case for deleting a non-existent product.
     * Scenario: When attempting to delete a product that does not exist,
     * the product controller should return a NOT_FOUND response with an appropriate error message.
     */
    @Test
    public void testDeleteProduct_ProductNotFound() {
        // Arrange
        String productId = "NonExistentPD_456";
        when(productService.getProduct(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<Optional<Product>>> responseEntity = productController.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("404", responseEntity.getBody().getCode());
        assertEquals("Product with ID " + productId + " not found", responseEntity.getBody().getMessage());
    }
}

