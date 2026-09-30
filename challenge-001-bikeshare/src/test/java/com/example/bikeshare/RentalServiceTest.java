package com.example.bikeshare;

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

import static org.assertj.core.api.Assertions.*;

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
    void finishChargesTwoHoursAtFiveReaisPerHour() {
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

    @Test
    void startFailsForUnknownBike() {
        assertThatThrownBy(() -> rentalService.start(customer.getId(), 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void startFailsWhenBikeIsAlreadyRented() {
        Customer other = customerRepository.save(
                new Customer("Bruno", "bruno@example.com", "senha1234"));
        rentalService.start(customer.getId(), bike.getId());

        assertThatThrownBy(() -> rentalService.start(other.getId(), bike.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void finishFailsForUnknownRental() {
        assertThatThrownBy(() -> rentalService.finish(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void finishChargesProportionallyForFractionalHours() {
        Rental rental = rentalRepository.save(
                new Rental(bike, customer, LocalDateTime.now().minusMinutes(90)));

        Rental finished = rentalService.finish(rental.getId());

        // 90 min a R$ 5,00/h = R$ 7,50
        assertThat(finished.getTotalPrice()).isCloseTo(10.0, within(0.01));
    }

    @Test
    void finishChargesProportionallyForShortRental() {
        Rental rental = rentalRepository.save(
                new Rental(bike, customer, LocalDateTime.now().minusMinutes(30)));

        Rental finished = rentalService.finish(rental.getId());

        // 30 min a R$ 5,00/h = R$ 2,50
        assertThat(finished.getTotalPrice()).isCloseTo(5.0, within(0.01));
    }

    @Test
    void finishRightAfterStartChargesZero() {
        Rental rental = rentalService.start(customer.getId(), bike.getId());

        Rental finished = rentalService.finish(rental.getId());

        // SUPOSIÇÃO: não há cobrança mínima. Se houver, ajuste o valor esperado.
        assertThat(finished.getTotalPrice()).isCloseTo(5.0, within(0.01));
    }
}
