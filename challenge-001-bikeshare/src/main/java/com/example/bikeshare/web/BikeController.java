package com.example.bikeshare.web;

import com.example.bikeshare.domain.Bike;
import com.example.bikeshare.service.BikeService;
import com.example.bikeshare.web.dto.CreateBikeRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bikes")
public class BikeController {

    private final BikeService bikeService;

    public BikeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Bike create(@Valid @RequestBody CreateBikeRequest request) {
        return bikeService.register(request.model());
    }

    @GetMapping
    public List<Bike> list() {
        return bikeService.listAll();
    }

    @PostMapping("/{id}/maintenance")
    public Bike maintenance(@PathVariable Long id) {
        return bikeService.sendToMaintenance(id);
    }
}
