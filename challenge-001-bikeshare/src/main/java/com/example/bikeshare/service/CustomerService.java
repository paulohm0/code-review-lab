package com.example.bikeshare.service;

import com.example.bikeshare.domain.Customer;
import com.example.bikeshare.exception.NotFoundException;
import com.example.bikeshare.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer register(String name, String email, String password) {
        if (customerRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Já existe um cliente com o e-mail " + email);
        }
        return customerRepository.save(new Customer(name, email, password));
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + id));
    }
}
