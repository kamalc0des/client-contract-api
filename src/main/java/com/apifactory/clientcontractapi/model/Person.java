package com.apifactory.clientcontractapi.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Represents an individual client with a birth date.
 */
@Getter
@Setter
@Entity
public class Person extends Client {

    @Past
    private LocalDate birthDate;

    public Person() {
        setType(ClientType.PERSON); // define the type of a Person
    }
}
