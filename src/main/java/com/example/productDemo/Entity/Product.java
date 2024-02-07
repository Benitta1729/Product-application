package com.example.productDemo.Entity;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Product {
	
	@Id
    private String id;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Name is required")
    @NotNull(message="Name should not be null")
    @Size(min = 1, max = 15, message = "Name must have a length of 1 to 15 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*[a-zA-Z][a-zA-Z0-9]*$", message = "Name must be alpha-numeric")
	private String name;
    
    @Column(nullable = false)
    @NotBlank(message = "Description is required")
    @NotNull(message="Description should not be null")
    private String description;
    
    @Column(nullable = false)
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;
   
    // Reviews associated with the product
	@ElementCollection
    @CollectionTable(name = "product_reviews", joinColumns = @JoinColumn(name = "product_id"))
    private List<Review> reviews;

	// Offers associated with the product
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_offers", joinColumns = @JoinColumn(name = "product_id"))
    private List<Offer> offers;

    // Transient field for storing calculated average rating
    @Transient
    private Double averageRating;
    
    // Transient field to store the discounted price of the product, calculated based on the applied offers.
    @Transient
    private Double discountedPrice;
   
    // Constructors 
    public Product() { }
    
    public Product(String name, String description, double price, List<Review> reviews, List<Offer> offers, Double averageRating) {
    	this.name = name;
        this.description = description;
        this.price = price;
        this.reviews = reviews;
        this.offers = offers;
        this.averageRating = averageRating;
       
        
    } 

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    public Double getDiscountedPrice() {
        return discountedPrice;
    }
    
    public void setDiscountedPrice(Double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
   
    //***Embeddable class for Review***
    
    @Embeddable
    public static class Review {
    	
    	@Column(nullable = false)
    	@NotBlank(message = "Reviewer name is required")
    	@NotNull(message="Reviewer Name should not be null")
    	@Pattern(regexp = "^[a-zA-Z0-9]{1,15}$", message = "Reviewer name must be alphanumeric and have a length of 1 to 15 characters")
    	private String reviewer;
    	
    	@Column(nullable = false)
    	@NotBlank(message = "comments are required")
    	@NotNull(message="comments should not be null")
        public String comments;
    	
    	@Column(nullable = false)
    	@NotNull(message="Rating is required")
    	@PositiveOrZero(message="Rating must be positive")
        @Min(value=0,message="Rating minimum is zero") // Minimum value for Rating
        @Max(value=5,message="Rating maximum is 5") // Maximum value for Rating
        public Double rating;


        // Constructors 
        public Review() {
        }

        public Review(String reviewer,String comments, double rating) {
        	this.reviewer=reviewer;
            this.comments = comments;
            this.rating = rating;
        }

        // Getters and setters
        
        public String getComments() {
            return comments;
        }

        public String getReviewer() {
			return reviewer;
		}

		public void setReviewer(String user) {
			this.reviewer = user;
		}

		public void setComments(String comments) {
            this.comments = comments;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }
    }

	//***Embeddable class for Offer***
    
    @Embeddable
    public static class Offer {
    	
		@Column(nullable = false)
    	@NotBlank(message = "offer details are required")
    	@NotNull(message="offer details should not be null")
		@Pattern(regexp = "^\\d+% discount$", message = "Offer details must be in the format like '20% discount'")
        private String offerdetails;
    	
    	@Column(nullable=false)
    	@NotBlank(message = "Coupon code is required")
    	@NotNull(message="Coupon code should not be null")
    	private String couponCode;
    	
    	@NotNull(message="startDate should not be null")
    	@FutureOrPresent(message = "Start date must be in the present or future")
        private LocalDate startDate;

    	@NotNull(message="endDate should not be null")
        @Future(message = "End date must be in the future")
        private LocalDate endDate;
    	
    	@Column(nullable = false)
        private Double discountAmount;
        
		// Constructors
        public Offer() {
        }
        
		public Offer(String offerdetails,String couponCode, LocalDate startDate,LocalDate endDate) {
			super();
			this.offerdetails = offerdetails;
			this.couponCode = couponCode;
			this.startDate = startDate;
			this.endDate = endDate;
		}

        // Getters and setters
      
		public String getOfferdetails() {
			return offerdetails;
		}

		public void setOfferdetails(String offerdetails) {
			this.offerdetails = offerdetails;
		}
		
		public String getCouponCode() {
			return couponCode;
		}

		public void setCouponCode(String couponCode) {
			this.couponCode = couponCode;
		}
		
		public LocalDate getStartDate() {
			return startDate;
		}

		public void setStartDate(LocalDate startDate) {
			this.startDate = startDate;
		}
		
		public LocalDate getEndDate() {
			return endDate;
		}

		public void setEndDate(LocalDate endDate) {
			this.endDate = endDate;
		}
		
		public Double getDiscountAmount() {
	        return discountAmount;
	    }

	    public void setDiscountAmount(Double discountAmount) {
	        this.discountAmount = discountAmount;
	    }
        
    }
}


