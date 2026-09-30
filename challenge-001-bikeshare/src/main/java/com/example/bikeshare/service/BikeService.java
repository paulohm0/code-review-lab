package com.example.bikeshare.service;

import com.example.bikeshare.domain.Bike;
import com.example.bikeshare.domain.BikeStatus;
import com.example.bikeshare.exception.NotFoundException;
import com.example.bikeshare.repository.BikeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BikeService {

    private final BikeRepository bikeRepository;

    public BikeService(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    @Transactional
    public Bike register(String model) {
        return bikeRepository.save(new Bike(model));
    }

    @Transactional(readOnly = true)
    public List<Bike> listAll() {
        return bikeRepository.findAll();
    }

    @Transactional
    public Bike sendToMaintenance(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bicicleta não encontrada: " + id));
        bike.setStatus(BikeStatus.MAINTENANCE);
        return bike;
    }
}
