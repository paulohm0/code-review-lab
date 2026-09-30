package com.example.vetclinic.service;

public interface EmailGateway {

    void send(String to, String subject, String body);
}
