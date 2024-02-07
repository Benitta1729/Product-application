package com.example.productDemo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.productDemo.Service.ProductDataGenerator;

@RestController
@RequestMapping("/populate-data")
public class DataGeneratorController {

    @Autowired
    private ProductDataGenerator dataPopulationScript;
    
    //***End point to trigger the population of random product data.***
    @PostMapping
    public ResponseEntity<String> populateData() {
    	
    	// Invoke the method to populate the database with random product data
        dataPopulationScript.populateData();
        String responseMessage = "Products created successfully";
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}

