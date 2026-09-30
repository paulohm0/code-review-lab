package com.example.bikeshare.web;

import com.example.bikeshare.service.RentalService;
import com.example.bikeshare.web.dto.RentalResponse;
import com.example.bikeshare.web.dto.StartRentalRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponse start(@RequestBody StartRentalRequest request) {
        return RentalResponse.from(rentalService.start(request.customerId(), request.bikeId()));
    }

    @PostMapping("/{id}/return")
    public RentalResponse finish(@PathVariable Long id) {
        return RentalResponse.from(rentalService.finish(id));
    }

    @GetMapping("/{id}")
    public RentalResponse get(@PathVariable Long id) {
        return RentalResponse.from(rentalService.findById(id));
    }
}
