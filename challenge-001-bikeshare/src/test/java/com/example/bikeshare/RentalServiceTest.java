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

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
class RentalServiceTest {

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired
    RentalService rentalService;

    @Autowired
    RentalRepository rentalRepository;

    @Autowired
    BikeRepository bikeRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    Clock clock;

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

    private Rental rentalStartedSecondsAgo(long seconds) {
        return rentalRepository.save(
                new Rental(bike, customer, LocalDateTime.now(clock).minusSeconds(seconds)));
    }

    @Nested
    class StartRental {
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
    }

    @Nested
    class FinishRental {
        @Test
        void finishMarksRentalAsFinishedAndFreesBike() {
            Rental rental = rentalService.start(customer.getId(), bike.getId());

            Rental finished = rentalService.finish(rental.getId());

            assertThat(finished.isFinished()).isTrue();
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
        void finishFailsForUnknownRental() {
            assertThatThrownBy(() -> rentalService.finish(999L))
                    .isInstanceOf(NotFoundException.class);
        }

        @ParameterizedTest(name = "{0} sec → R$ {1}")
        @CsvSource({
                "0,     5.0",
                "1,     5.0",
                "3599,  5.0",
                "3600,  5.0",
                "3601,  10.0",
                "7200,  10.0",
                "7201,  15.0"
        })
        void finishChargesByStartedHour(long seconds, double expectedPrice) {
            Rental rental = rentalStartedSecondsAgo(seconds);

            Rental finished = rentalService.finish(rental.getId());

            assertThat(finished.getTotalPrice()).isCloseTo(expectedPrice, within(0.01));
        }
    }
}
