package com.example.productDemo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.productDemo.Entity.Product;
import com.example.productDemo.Entity.Product.Offer;
import com.example.productDemo.Entity.Product.Review;
import com.example.productDemo.Repository.ProductRepository;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    //----- Creates a new product--------- 
    public Product createProduct(Product product) {
    	 // Checks if the product name is unique.
        if (productRepository.findByNameIgnoreCase(product.getName()).isPresent()) {
            throw new IllegalArgumentException("Name must be unique");
        }
        // Generate a new ID based on the last product ID
        String newProductId = generateNewId();
        
        // Set the new ID to the product
        product.setId(newProductId);
        
        return productRepository.save(product);
    }

    //----- Retrieves a product by its ID and updates its average rating if available.-----
    public Optional<Product> getProduct(String productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        productOptional.ifPresent(this::updateAverageRating);
        productOptional.ifPresent(this::calculateDiscountedPrice); // Calculate discounted price if product is present
        return productOptional;
    }

    //---- Updates an existing product with the provided details.------
    public Product updateProduct(String productId, Product updatedProduct) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        optionalProduct.ifPresent(product -> {
            // Update product attributes based on the updatedProduct
            product.setName(updatedProduct.getName());
            product.setDescription(updatedProduct.getDescription());
            product.setPrice(updatedProduct.getPrice());
            // Save the changes
            productRepository.save(product);
        });
        return optionalProduct.orElseGet(() -> null);


    }
    
    //---- Adds a review to the specified product.-------
    public Product addReviewToProduct(String productId, Review review) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            // Add the review to the product and update average rating
            product.getReviews().add(review);
            updateAverageRating(product);
            // Save the changes to the repository
            productRepository.save(product);
            System.out.println("Review added successfully for product with ID: " + productId);
            return product;
        }else {
            return null;
        }
        
    }

    //---- Adds an offer to the specified product.-----
    public Product addOfferToProduct(String productId, Offer offer) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            
            // Calculate discount amount based on the product's price and the offer details
            double productPrice = product.getPrice();
            int percentageDiscount = Integer.parseInt(offer.getOfferdetails().split("%")[0]);
            double discountAmount = productPrice * (percentageDiscount / 100.0);
            // Round the discounted price to two decimal places
            double roundedDiscountedAmount = Math.round(discountAmount * 100.0) / 100.0;

            // Set the discount amount in the offer object
            offer.setDiscountAmount(roundedDiscountedAmount);
            
            // Remove existing offers if any
            product.getOffers().clear();// Clearing existing offers to ensure only one offer or null is present
            
            // Add the offer to the product and save the changes
            product.getOffers().add(offer);
            productRepository.save(product);
            return product;
        }else {
            return null;
        }
       
    }

    public String generateNewId() {
        // Retrieve the last product ID from the database
        String lastProductId = getLastProductId();

        // Extract the numeric part from the last product ID
        String numericPart = lastProductId.replace("PDNO_", "");

        // Handle leading zeros by parsing as a decimal integer
        int lastNumericValue = Integer.parseInt(numericPart);

        // Increment the numeric part
        int newNumericValue = lastNumericValue + 1;

        // Create the new ID by combining the prefix and the incremented numeric part
        // return "PDNO_" + newNumericValue;
        // Create the new ID by combining the prefix and the incremented numeric part
        String newId = "PDNO_" + String.format("%05d", newNumericValue);

        return newId;
    }

    
    //---- Retrieves the last product ID from the database using Spring Data JPA.----
    public String getLastProductId() {
    	
        // Use Spring Data JPA to retrieve the last product ID from the database
        Optional<Product> lastProduct = productRepository.findTopByOrderByIdDesc();

        // If a product is present, extract and return its ID
        return lastProduct.map(Product::getId).orElse("PDNO_0");
    }

   
    //----- Updates the average rating of a product based on its reviews.-----
    public void updateAverageRating(Product product) {
        Double averageRating = calculateAverageRating(product);
        product.setAverageRating(averageRating);
    }

    //----- Calculates the average rating of a product based on its reviews.-----
    public Double calculateAverageRating(Product product) {
        List<Review> reviews = product.getReviews();

        if (reviews == null || reviews.isEmpty()) {
            return null; // Handle the case where there are no reviews
        }

        double sum = 0;
        int count = 0;

        for (Review review : reviews) {
            Double rating = review.getRating();
            if (rating != null) {
                sum += rating;
                count++;
            }
        }

        if (count == 0) {
            return null; // Avoid division by zero
        }

        // Calculate the average
        double average = sum / count;
        
        // Round to two decimal places
        return Math.round(average * 100.0) / 100.0;
    }
    
 // Method to calculate discounted price for a product
    private void calculateDiscountedPrice(Product product) {
        // Calculate the discounted price based on the offers
        if (product.getOffers() != null && !product.getOffers().isEmpty()) {
            double productPrice = product.getPrice();
            for (Offer offer : product.getOffers()) {
                // Assuming offerdetails contains the percentage discount
                int percentageDiscount = Integer.parseInt(offer.getOfferdetails().split("%")[0]);
                double discountAmount = productPrice * (percentageDiscount / 100.0);
                double discountedPrice = productPrice - discountAmount;
                // Round the discounted price to two decimal places
                double roundedDiscountedPrice = Math.round(discountedPrice * 100.0) / 100.0;

                product.setDiscountedPrice(roundedDiscountedPrice);
            }
        } else {
            product.setDiscountedPrice(product.getPrice()); // If no offers, discounted price is same as regular price
        }
    }
  
    //----- Retrieves summaries of all products.-----
    public Page<Map<String, Object>> getAllProductSummaries(Pageable pageable) {
        // Fetch all products
        List<Product> products = productRepository.findAll();

        // Transform the list of products into a list of Map<String, Object>
        List<Map<String, Object>> productSummaries = products.stream()
                .map(product -> {
                    Map<String, Object> summaryMap = new LinkedHashMap<>();
                    summaryMap.put("id", product.getId());
                    summaryMap.put("name", product.getName());
                    summaryMap.put("description", product.getDescription());
                    summaryMap.put("price", product.getPrice());
                    // Add other fields as needed
                    return summaryMap;
                })
                .collect(Collectors.toList());

        // Use Spring Data JPA's built-in pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), productSummaries.size());
        List<Map<String, Object>> paginatedProductSummaries = productSummaries.subList(start, end);

        // Create a Page from the paginated products
        return new PageImpl<>(paginatedProductSummaries, pageable, productSummaries.size());
    }

    //----- Deletes the product.----
    public Product deleteProduct(String productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);

            Product deletedProduct = optionalProduct.get();
            productRepository.deleteById(productId);
            return deletedProduct;
    }
  
    //---- Retrieves the product reviews in a paginated format.-----
    public Page<Map<String, Object>> getReviewsForProduct(String productId, Pageable pageable) {
        // Check if the product exists
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isPresent()) {
            // Product exists, fetch reviews for the product
            Product product = optionalProduct.get();
            List<Review> reviews = product.getReviews();

            // Paginate the reviews manually 
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), reviews.size());
            List<Review> paginatedReviews = reviews.subList(start, end);

            // Transform the list of products and reviews into a list of Map objects
            List<Map<String, Object>> reviewMaps = paginatedReviews.stream()
                    .map(review -> {
                        Map<String, Object> reviewMap = new HashMap<>();
                        reviewMap.put("name:", review.getReviewer());
                        reviewMap.put("rating:", review.getRating());
                        reviewMap.put("comments:", review.getComments());
                        // Add other fields as needed
                        return reviewMap;
                    })
                    .collect(Collectors.toList());

            // Create a Page from the paginated reviews
            return new PageImpl<>(reviewMaps, pageable, reviews.size());
        } else {
            // Product not found, return an empty page
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }
    

}

