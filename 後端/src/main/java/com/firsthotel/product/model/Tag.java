package com.firsthotel.product.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Tags") 

public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
