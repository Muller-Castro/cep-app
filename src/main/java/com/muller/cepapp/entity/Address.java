package com.muller.cepapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Street is required")
    @Size(max = 255, message = "Street cannot exceed 255 characters")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "Number is required")
    @Size(max = 20, message = "Number cannot exceed 20 characters")
    @Column(nullable = false)
    private String number;

    @Size(max = 255, message = "Complement cannot exceed 255 characters")
    private String complement;

    @NotBlank(message = "Neighborhood is required")
    @Size(max = 255, message = "Neighborhood cannot exceed 255 characters")
    @Column(nullable = false)
    private String neighborhood;

    @NotBlank(message = "City is required")
    @Size(max = 255, message = "City cannot exceed 255 characters")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 2, message = "State must be 2 characters")
    @Pattern(
        regexp  = "^(AC|AL|AP|AM|BA|CE|DF|ES|GO|MA|MS|MT|MG|PA|PB|PR|PE|PI|RJ|RN|RS|RO|RR|SC|SP|SE|TO)$",
        message = "Invalid state abbreviation"
    )
    @Column(nullable = false, length = 2)
    private String state;

    @NotBlank(message = "ZIP Code is required")
    @Pattern(regexp = "^\\d{8}$", message = "ZIP Code must be 8 digits")
    @Column(nullable = false, length = 8)
    private String zipCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Address(String street, String number, String complement, String neighborhood, String city, String state, String zipCode, User user) {
        this.street       = street;
        this.number       = number;
        this.complement   = complement;
        this.neighborhood = neighborhood;
        this.city         = city;
        this.state        = state;
        this.zipCode      = zipCode;
        this.user         = user;
    }
    
}
