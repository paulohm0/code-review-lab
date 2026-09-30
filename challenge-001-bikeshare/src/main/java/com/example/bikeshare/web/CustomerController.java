package com.example.bikeshare.web;

import com.example.bikeshare.domain.Customer;
import com.example.bikeshare.service.CustomerService;
import com.example.bikeshare.web.dto.CreateCustomerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@RequestBody CreateCustomerRequest request) {
        return customerService.register(request.name(), request.email(), request.password());
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) {
        return customerService.findById(id);
    }
}
