package com.example.cartservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;

    private String name;

    private String description;

    private Double price;

    private Long remainingQuantity;

    private List<Review> reviews;
}
