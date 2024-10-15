package com.example.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "address_sequence", sequenceName = "address_sequence", allocationSize = 50)  // Adjust allocationSize as needed
    private Long addressId;

    private String street;

    private String city;

    private String state;

    private String country;

    private String zipCode;

}
