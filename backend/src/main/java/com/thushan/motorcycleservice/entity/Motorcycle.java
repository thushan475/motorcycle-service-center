package com.thushan.motorcycleservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "motorcycles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Motorcycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String registrationNumber;

    @Column(nullable = false, length = 50)
    private String chassisNumber;

    @Column(nullable = false, length = 50)
    private String engineNumber;

    @Column(nullable = false)
    private Integer year;

    @Column(length = 30)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorcycle_brand_id", nullable = false)
    private MotorcycleBrand motorcycleBrand;
}
