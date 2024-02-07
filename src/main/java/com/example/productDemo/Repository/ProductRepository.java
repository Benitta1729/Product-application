package com.example.productDemo.Repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.productDemo.Entity.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
    //  custom query methods if needed
	Optional<Product> findByNameIgnoreCase(String name);
	
	List<Projection> findAllBy();

    interface Projection {
        String getId();
        String getName();
        String getDescription();
        double getPrice();
    }
    Optional<Product> findTopByOrderByIdDesc();

	Page<Projection> findAllBy(Pageable pageable);

	 // Add a method to check if a similar product already exists
    boolean existsByNameIgnoreCaseAndDescriptionIgnoreCaseAndPrice(String name, String description, Double price);

    
}