package com.example.vetclinic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simulated e-mail delivery: only writes the message to the log.
 */
@Component
public class LoggingEmailGateway implements EmailGateway {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailGateway.class);

    @Override
    public void send(String to, String subject, String body) {
        log.info("E-mail to={} subject='{}' body='{}'", to, subject, body);
    }
}
