package com.example.vetclinic.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "veterinarians")
public class Veterinarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialty;

    @Column(nullable = false)
    private double consultationFee;

    protected Veterinarian() {
    }

    public Veterinarian(String name, String specialty, double consultationFee) {
        this.name = name;
        this.specialty = specialty;
        this.consultationFee = consultationFee;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public double getConsultationFee() {
        return consultationFee;
    }
}
