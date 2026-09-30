package com.example.bikeshare.service;

import com.example.bikeshare.domain.Bike;
import com.example.bikeshare.domain.BikeStatus;
import com.example.bikeshare.domain.Customer;
import com.example.bikeshare.domain.Rental;
import com.example.bikeshare.exception.NotFoundException;
import com.example.bikeshare.repository.BikeRepository;
import com.example.bikeshare.repository.CustomerRepository;
import com.example.bikeshare.repository.RentalRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BikeRepository bikeRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;
    private final Clock clock;

    private static final double HOURLY_RATE = 5.0;
    private static final double MINIMUM_CHARGE = 5.0;
    private static final double SECONDS_PER_HOUR = 3600.0;

    public RentalService(RentalRepository rentalRepository,
                         BikeRepository bikeRepository,
                         CustomerRepository customerRepository,
                         NotificationService notificationService,
                         Clock clock) {
        this.rentalRepository = rentalRepository;
        this.bikeRepository = bikeRepository;
        this.customerRepository = customerRepository;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    @Transactional
    public Rental start(Long customerId, Long bikeId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + customerId));
        Bike bike = bikeRepository.findById(bikeId)
                .orElseThrow(() -> new NotFoundException("Bicicleta não encontrada: " + bikeId));
        if (bike.getStatus() != BikeStatus.AVAILABLE) {
            throw new IllegalStateException("Bike is not available");
        }

        bike.setStatus(BikeStatus.RENTED);
        return rentalRepository.save(new Rental(bike, customer, LocalDateTime.now(clock)));
    }

    @Transactional
    public Rental finish(Long rentalId) {
        Rental rental = findById(rentalId);
        if (rental.isFinished()) {
            throw new IllegalStateException("Aluguel já foi finalizado: " + rentalId);
        }

        LocalDateTime now = LocalDateTime.now(clock);
        long seconds = Duration.between(rental.getStartedAt(), now).toSeconds();
        double total = Math.max(MINIMUM_CHARGE, Math.ceil(seconds / SECONDS_PER_HOUR) * HOURLY_RATE);

        rental.finish(now, total);
        rental.getBike().setStatus(BikeStatus.AVAILABLE);
        notificationService.sendReceipt(rental);
        return rental;
    }

    public Rental findById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aluguel não encontrado: " + id));
    }
}
