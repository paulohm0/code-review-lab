package com.example.bikeshare.service;

import com.example.bikeshare.domain.Bike;
import com.example.bikeshare.domain.BikeStatus;
import com.example.bikeshare.domain.Customer;
import com.example.bikeshare.domain.Rental;
import com.example.bikeshare.repository.BikeRepository;
import com.example.bikeshare.repository.CustomerRepository;
import com.example.bikeshare.repository.RentalRepository;
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

    public RentalService(RentalRepository rentalRepository,
                         BikeRepository bikeRepository,
                         CustomerRepository customerRepository,
                         NotificationService notificationService) {
        this.rentalRepository = rentalRepository;
        this.bikeRepository = bikeRepository;
        this.customerRepository = customerRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Rental start(Long customerId, Long bikeId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + customerId));
        Bike bike = bikeRepository.findById(bikeId)
                .orElseThrow(() -> new NotFoundException("Bicicleta não encontrada: " + bikeId));

        bike.setStatus(BikeStatus.RENTED);
        return rentalRepository.save(new Rental(bike, customer, LocalDateTime.now()));
    }

    @Transactional
    public Rental finish(Long rentalId) {
        Rental rental = findById(rentalId);
        if (rental.isFinished()) {
            throw new IllegalStateException("Aluguel já foi finalizado: " + rentalId);
        }

        LocalDateTime now = LocalDateTime.now();
        long hours = Duration.between(rental.getStartedAt(), now).toHours();
        double total = hours * 5.0;

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
