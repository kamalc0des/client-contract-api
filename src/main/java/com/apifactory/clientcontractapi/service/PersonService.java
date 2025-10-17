package com.apifactory.clientcontractapi.service;

import com.apifactory.clientcontractapi.model.Person;
import com.apifactory.clientcontractapi.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing Person-type clients.
 */
@Service
@Transactional
public class PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Creates a new Person client after validating birth date.
     *
     * @param person the person entity to save
     * @return the saved person
     * @throws IllegalArgumentException if birth date is in the future
     */
    public Person createPerson(Person person) {
        if (person.getBirthDate() != null && person.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future.");
        }
        logger.info("Creating person client: {}", person.getName());
        return personRepository.save(person);
    }

    /**
     * Retrieves all Person-type clients.
     *
     * @return list of persons
     */
    public List<Person> getAllPersons() {
        logger.info("Fetching all persons from database");
        return personRepository.findAll();
    }

    /**
     * Retrieves a person by ID.
     *
     * @param id the person's UUID
     * @return the person if found
     */
    public Person getPersonById(UUID id) {
        logger.info("Fetching person with ID: {}", id);
        return personRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + id));
    }

    /**
     * Deletes a person by ID.
     *
     * @param id the person's UUID
     */
    public void deletePerson(UUID id) {
        logger.info("Deleting person with ID: {}", id);
        personRepository.deleteById(id);
    }
}
