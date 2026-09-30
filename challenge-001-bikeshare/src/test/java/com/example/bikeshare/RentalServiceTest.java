package com.example.bikeshare;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.bikeshare.domain.Bike;
import com.example.bikeshare.domain.BikeStatus;
import com.example.bikeshare.domain.Customer;
import com.example.bikeshare.domain.Rental;
import com.example.bikeshare.repository.BikeRepository;
import com.example.bikeshare.repository.CustomerRepository;
import com.example.bikeshare.repository.RentalRepository;
import com.example.bikeshare.exception.NotFoundException;
import com.example.bikeshare.service.RentalService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RentalServiceTest {

    @Autowired
    RentalService rentalService;

    @Autowired
    RentalRepository rentalRepository;

    @Autowired
    BikeRepository bikeRepository;

    @Autowired
    CustomerRepository customerRepository;

    Customer customer;
    Bike bike;

    @BeforeEach
    void setUp() {
        rentalRepository.deleteAll();
        bikeRepository.deleteAll();
        customerRepository.deleteAll();
        customer = customerRepository.save(new Customer("Ana", "ana@example.com", "senha1234"));
        bike = bikeRepository.save(new Bike("Caloi 10"));
    }

    @Test
    void startMarksBikeAsRented() {
        Rental rental = rentalService.start(customer.getId(), bike.getId());

        assertThat(rental.getId()).isNotNull();
        assertThat(bikeRepository.findById(bike.getId()).orElseThrow().getStatus())
                .isEqualTo(BikeStatus.RENTED);
    }

    @Test
    void startFailsForUnknownCustomer() {
        assertThatThrownBy(() -> rentalService.start(999L, bike.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void finishChargesFiveReaisPerHour() {
        Rental rental = rentalRepository.save(
                new Rental(bike, customer, LocalDateTime.now().minusHours(2)));

        Rental finished = rentalService.finish(rental.getId());

        assertThat(finished.isFinished()).isTrue();
        assertThat(finished.getTotalPrice()).isEqualTo(10.0);
        assertThat(bikeRepository.findById(bike.getId()).orElseThrow().getStatus())
                .isEqualTo(BikeStatus.AVAILABLE);
    }

    @Test
    void finishTwiceIsRejected() {
        Rental rental = rentalService.start(customer.getId(), bike.getId());
        rentalService.finish(rental.getId());

        assertThatThrownBy(() -> rentalService.finish(rental.getId()))
                .isInstanceOf(IllegalStateException.class);
    }
}
