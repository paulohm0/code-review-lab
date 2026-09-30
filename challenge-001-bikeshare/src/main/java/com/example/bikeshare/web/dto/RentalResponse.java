package com.example.bikeshare.web.dto;

import com.example.bikeshare.domain.Rental;
import java.time.LocalDateTime;

public record RentalResponse(
        Long id,
        Long bikeId,
        Long customerId,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Double totalPrice) {

    public static RentalResponse from(Rental rental) {
        return new RentalResponse(
                rental.getId(),
                rental.getBike().getId(),
                rental.getCustomer().getId(),
                rental.getStartedAt(),
                rental.getEndedAt(),
                rental.getTotalPrice());
    }
}
