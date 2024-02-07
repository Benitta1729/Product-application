package com.example.productDemo.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.productDemo.Entity.Product;
import com.example.productDemo.Repository.ProductRepository;

@Component
public class ProductDataGenerator {

    @Autowired
    private ProductService productService;
    
    @Autowired
    ProductRepository productRepository;

    //*** Populates the database with random product data.***
     public void populateData() {
    	
    	// Reset the database before populating data
       // resetDatabase();
        
        // Generates and inserts products with random data
        for (int i = 0; i < 2000; i++) {
            Product product = generateRandomProduct();
            try {
            	// Check if a similar product already exists in the database
            	if (!productRepository.existsByNameIgnoreCaseAndDescriptionIgnoreCaseAndPrice(
                        product.getName(), product.getDescription(), product.getPrice())) {
            		 System.out.println("Creating product with ID: " + product.getId());
                    productService.createProduct(product);
                }
            } catch (Exception e) {
                System.err.println("Error creating product: " + e.getMessage());
            }
        }

        System.out.println("Data population completed.");
    }

    /**
    * Generates a random product with random attributes.
    *      
    * @return Randomly generated product.
    */
    private static Product generateRandomProduct() {
        Random random = new Random();
        String productName = "Product" + random.nextInt(20000);
        String description = "Description for " + productName;
        // Generate a random price with two digits after the decimal point
        double price = random.nextDouble() * 10000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        price = Double.parseDouble(decimalFormat.format(price));

        List<Product.Review> reviews = generateRandomReviews(random.nextInt(4));
        List<Product.Offer> offers = generateRandomOffers(random.nextInt(1), price);

        return new Product(productName, description, price, reviews, offers, null);
    }

    /**
     * Generates a specified number of random reviews for a product.
     *
     * @param numberOfReviews Number of reviews to generate.
     * @return List of randomly generated reviews.
     */
    private static List<Product.Review> generateRandomReviews(int numberOfReviews) {
        List<Product.Review> reviews = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numberOfReviews; i++) {
            String reviewer = "Reviewer" + i;
            double rating = random.nextDouble() * 5.0;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            rating = Double.parseDouble(decimalFormat.format(rating));
            String comments;

            // Assign comments based on rating range
            if (rating >= 4.5) {
                comments = "Superb!";
            } else if (rating >= 3.5) {
                comments = "Good";
            } else if (rating >= 2.5) {
                comments = "Average";
            } else if (rating >= 1.5) {
                comments = "Below Average";
            } else {
                comments = "Poor";
            }

            Product.Review review = new Product.Review(reviewer, comments, rating);
            reviews.add(review);
        }

        return reviews;
    }


    /**
     * Generates a specified number of random offers for a product.
     *
     * @param numberOfOffers Number of offers to generate.
     * @return List of randomly generated offers.
     */
    private static List<Product.Offer> generateRandomOffers(int numberOfOffers,double price) {
        List<Product.Offer> offers = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i <= numberOfOffers; i++) {
            int discountPercentage = random.nextInt(56) + 5; // Generates a random percentage between 10 and 100
            String offerDetails = discountPercentage + "% discount";
            String couponCode = "SAVE" + discountPercentage; // Coupon code based on the discount percentage
            LocalDate startDate = LocalDate.now().plusDays(random.nextInt(30));
            LocalDate endDate = startDate.plusDays(random.nextInt(30));

            // Calculate the discount amount based on the product price and discount percentage
            double discountAmount = (price * discountPercentage) / 100.0;

            // Format discount amount to have only two decimal places
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            discountAmount = Double.parseDouble(decimalFormat.format(discountAmount));

            Product.Offer offer = new Product.Offer(offerDetails, couponCode, startDate, endDate);
            offer.setDiscountAmount(discountAmount);
            offers.add(offer);
        }

        return offers;
    }
    
    public void resetDatabase() {
        // Delete all records from the Product table
        productRepository.deleteAll();
    }
}

