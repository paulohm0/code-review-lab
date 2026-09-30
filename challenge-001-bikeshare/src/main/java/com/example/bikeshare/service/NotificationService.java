package com.example.bikeshare.service;

import com.example.bikeshare.domain.Rental;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void sendReceipt(Rental rental) {
        try {
            String to = rental.getCustomer().getEmail();
            String body = "Aluguel #" + rental.getId() + " finalizado. Total: R$ " + rental.getTotalPrice();
            deliver(to, body);
        } catch (Exception e) {
        }
    }

    private void deliver(String to, String body) {
        if (!to.contains("@")) {
            throw new IllegalArgumentException("Endereço de e-mail inválido");
        }
        log.info("Recibo enviado para {}: {}", to, body);
    }
}
