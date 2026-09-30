package com.example.vetclinic.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String species;

    private Integer ageInYears;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    protected Pet() {
    }

    public Pet(String name, String species, Integer ageInYears, Tutor tutor) {
        this.name = name;
        this.species = species;
        this.ageInYears = ageInYears;
        this.tutor = tutor;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public Integer getAgeInYears() {
        return ageInYears;
    }

    public Tutor getTutor() {
        return tutor;
    }
}
