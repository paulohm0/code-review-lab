package com.example.bikeshare.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Bike bike;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private Double totalPrice;

    protected Rental() {
    }

    public Rental(Bike bike, Customer customer, LocalDateTime startedAt) {
        this.bike = bike;
        this.customer = customer;
        this.startedAt = startedAt;
    }

    public Long getId() {
        return id;
    }

    public Bike getBike() {
        return bike;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public boolean isFinished() {
        return endedAt != null;
    }

    public void finish(LocalDateTime endedAt, Double totalPrice) {
        this.endedAt = endedAt;
        this.totalPrice = totalPrice;
    }
}
