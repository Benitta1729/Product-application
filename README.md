
# <h1>Product Demo Application</h1>
This is a demo application for managing products. It provides CRUD (Create, Read, Update, Delete) operations for products along with the ability to add reviews and offers to products.

## Technologies Used
- Java
- Spring Boot
- Spring Data JPA
- Spring Web
- Swagger (OpenAPI)
- Postman
- JUnit (for unit testing)
- mysql database

### Build the project using Maven:

```bash
mvn clean install
```
### Run the application:

```bash
mvn spring-boot:run
```

The application will start running at http://localhost:8080.

## API Documentation
Swagger UI is integrated into the application to provide interactive API documentation. You can access the API documentation by visiting the following URL:

Swagger UI: [Swagger](http://localhost:8080/swagger-ui.html)

### Postman Tests
The Postman collection includes tests for each API endpoint with different scenarios, including input validation and performance testing with a dataset of 10,000 records.


### Test Cases
Each API endpoint has the following test cases:

#### Input Validation Test Cases: 
Ensure that the API handles invalid inputs correctly, such as null values, invalid data types, and exceeding maximum lengths.
#### Performance Testing:
Used a dataset of 10,000 records to test the performance of the APIs. 
### Unit Testing
Unit tests are implemented using JUnit to ensure the correctness of service and controller methods.

## MySQL Database

In pom.xml add:

        ```<dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>```
The Product Demo application uses MySQL as its database backend. Below are details about the database setup and the structure of the tables.

### Schema
The database schema consists of the following tables:
#### product: 
Stores information about each product, including its ID, name, description, price, and other attributes.

#### product_review: 
Stores reviews submitted for each product, including the reviewer's name, rating, comments, and other details.

#### product_offer:
Stores information about offers applicable to products, including the discount percentage and other details.


## Product Entity
The Product entity represents a product in the system. It contains attributes such as ID, name, description, price, reviews, and offers.

### Product Entity Attributes:
#### ID:
Unique identifier for the product.
#### Name:
Name of the product.
#### Description:
Description of the product.
#### Price:
Price of the product.
#### Average Rating: 
The average rating given by users for the product.
#### Reviews: 
List of reviews associated with the product.
#### Offers:
List of offers associated with the product.
#### Discounted Price: 
The price of the product after applying any discounts.

### Offers Attributes:

#### Offer Details:
Details about the offer, such as discount percentage or special conditions.
#### Discount Amount: 
The amount discounted from the original price.
#### Start Date:
The start date of the offer.
#### End Date: 
The end date of the offer.
#### Product ID:
The ID of the product to which the offer applies.

### Reviews Attributes:

#### Reviewer:
The name or identifier of the user who submitted the review.
#### Rating: 
The numerical rating given by the reviewer (usually on a scale of 1 to 5).
#### Comments:
Additional comments or feedback provided by the reviewer.
#### Product ID:
The ID of the product for which the review is submitted.

## ProductService
The ProductService class provides methods to perform business logic related to products:

#### createProduct(Product product): 
Creates a new product in the system.
#### getProduct(String productId):
Retrieves product details by ID.
#### updateProduct(String productId, Product updatedProduct): 
Updates existing product details.
#### addReviewToProduct(String productId, Review review):
Adds a review to the specified product.
#### addOfferToProduct(String productId, Offer offer):
Adds an offer to the specified product.
#### generateNewId():
Generates a new unique ID for a product.
#### getLastProductId():
Retrieves the last product ID from the database.
#### updateAverageRating(Product product):
Updates the average rating of a product based on its reviews.
#### calculateAverageRating(Product product):
Calculates the average rating of a product based on its reviews.
#### calculateDiscountedPrice(Product product):
Calculates the discounted price for a product based on its offers.

## ProductController
The ProductController class defines REST endpoints for performing CRUD operations on products:

#### createProduct(Product product):
Creates a new product.
#### getProduct(String productId):
Retrieves product details by ID.
#### updateProduct(String productId, Product updatedProduct): 
Updates existing product details.
#### addReviewToProduct(String productId, Review review):
Adds a review to the specified product.
#### addOfferToProduct(String productId, Offer offer): 
Adds an offer to the specified product.
#### getAllProductSummaries(int page, int size): 
Retrieves summaries of all products in a paginated format.
#### deleteProduct(String productId): 
Deletes a product by ID.
#### getReviewsForProduct(String productId, int page, int size): 
Retrieves reviews for the specified product in a paginated format.

### HTTP Request and Response Examples:
#### GET-Fetch a single product

```` 
Request:

GET /products/PDNO_00001
Accept: application/json

Response:

HTTP/1.1 200 OK
Content-Type: application/json

{
    "id": "PDNO_00001",
    "name": "Smartphone",
    "description": "High-performance smartphone with advanced features",
    "price": 7056.82,
    "reviews": [
            {
                "reviewer": "Reviewer0",
                "comments": "Superb!",
                "rating": 4.5
            }
        ],
   "offers": [
            {
                "offerdetails": "40% discount",
                "couponCode": "SAVE40",
                "startDate": "2024-02-19",
                "endDate": "2024-03-03",
                "discountAmount": 2822.73
            }
        ],
        "averageRating": 4.5,
        "discountedPrice": 4234.09
    }
} 
````
#### POST-Create a Product

````
Request:

POST /products
Accept: application/json
Content-Type: application/json

{
    "name": "Smartphone",
    "description": "High-performance smartphone with advanced features",
    "price": 699.99
}

Response:

HTTP/1.1 201 Created
Content-Type: application/json

{   
    "code": "201",
    "message": "Product created successfully",
    "data": {
       "id": "PDNO_00002",
       "name": "Smartphone",
       "description": "High-performance smartphone with advanced features",
       "price": 699.99,
       "reviews": [],
       "offers": [],
       "averageRating": null
       "discountedPrice": 699.99
    }
}
````

#### PUT-Update a Product

````
Request:

PUT /products/PDNO_00002
Accept: application/json
Content-Type: application/json

{
    "name": "High-performance Smartphone",
    "description": "Updated description",
    "price": 749.99
}

Response:

HTTP/1.1 200 OK
Content-Type: application/json

{
    "code": "200",
    "message": "Product updated successfully",
    "data": {
        "name": "High-performance Smartphone",
        "description": "Updated description",
        "price": 749.99
       }
}
````
#### DELETE-Delete a Product

````
Request:

DELETE /products/PDNO_00001
Accept: application/json

Response:

HTTP/1.1 200 OK
Content-Type: application/json

{
    "code": "200",
    "message": "Product deleted successfully",
    "data": {
       "id": "PDNO_00001",
       "name": "High-performance Smartphone",
       "description": "Updated description",
       "price": 749.99,
       "reviews": [
        {
            "reviewer": "John Doe",
            "rating": 5,
            "comments": "Excellent phone!"
        },
        {
            "reviewer": "Jane Smith",
            "rating": 4,
            "comments": "Good value for money."
        }
    ],
    "offers": [],
    "averageRating": 4.5,
    "discountedPrice": 749.99
}
````
#### POST_Add a Review to a Product
````
Request:

POST /products/reviews/PDNO_00001
Accept: application/json
Content-Type: application/json

{
    "reviewer": "Alice",
    "rating": 4,
    "comments": "Great product!"
}
Response:

HTTP/1.1 200 OK
Content-Type: application/json

{
   "code": "200",
   "message": "Reviews added successfully for product PDNO_00001",
   "data": {
        {
            "reviewer": "Alice",
            "rating": 4,
            "comments": "Great product!"
        }
}
````
#### POST-Add an Offer to a Product
````
Request:

POST /products/offers/PDNO_00001
Accept: application/json
Content-Type: application/json

{
   "offerdetails": "23% discount",
   "couponCode": "SUPER20",
   "startDate": "2024-02-15",
   "endDate": "2024-02-15"
}

Response:

HTTP/1.1 200 OK
Content-Type: application/json

{
    "code": "200",
    "message": "Reviews added successfully for product PDNO_00001",
    "data": {
       "offerdetails": "23% discount",
       "couponCode": "SUPER20",
       "startDate": "2024-02-15",
       "endDate": "2024-02-15"
      }
}
````
#### GET-Retrieve Product Summaries using pagination
````
Request:

GET /products/summaries?size=4&page=0

Response:

HTTP/1.1 200 OK
Content-Type: application/json

{
    "CurrentPage": 0,
    "PRODUCTS": [
        {
            "id": "PDNO_00001",
            "name": "Product9275",
            "description": "Description for Product9275",
            "price": 7056.82
        },
        {
            "id": "PDNO_00002",
            "name": "ProductA",
            "description": "description ",
            "price": 8976.8
        },
        {
            "id": "PDNO_00003",
            "name": "Product11133",
            "description": "Description for Product11133",
            "price": 9277.52
        },
        {
            "id": "PDNO_00004",
            "name": "Product1236",
            "description": "Description for Product1236",
            "price": 2506.43
        }
    ],
    "TotalElements": 10050,
    "TotalPages": 2513
}
````

#### GET-Retrieve Product Reviews using pagination

````
Request:

GET /products/allreviews/PDNO_00003?size=2&page=0

Response:

HTTP/1.1 200 OK
Content-Type: application/json

{
    "PRODUCT REVIEWS": [
        {
            "name:": "Reviewer0",
            "comments:": "Superb!",
            "rating:": 4.5
        }
        {
            "name:": "Reviewer1",
            "comments:": "Superb!",
            "rating:": 4.8
        }
    ],
    "CurrentPage": 0,
    "TotalElements": 2,
    "TotalPages": 1
}
````



