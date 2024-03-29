package ru.job4j.auth.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.handlers.Operation;
import ru.job4j.auth.service.SimplePersonService;

import javax.validation.Valid;

/**
 * 3. Мидл
 * 3.4. Spring
 * 3.4.6. Rest
 * 1. RESTFul. Описание архитектуры [#6884]
 * 5. Обработка исключений и Spring REST [#504797]
 * PersonController rest контроллер CRUD операций модели Person
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 24.04.2023
 */
@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {
    private final SimplePersonService persons;

    @GetMapping("/")
    public Iterable<Person> getFindAll() {
        return this.persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getFindById(@PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<Person>(
                person.orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Person id: " + id + ", not found"
                        )
                ),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        if (person == null || person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("Person login and password mustn't be empty");
        }
        var personSave = this.persons.save(person);
        return new ResponseEntity<Person>(
                personSave.orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "The person has not been saved, the username is already taken"
                        )
                ),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/")
    @Validated(Operation.OnUpdate.class)
    public Person updatePatch(@Valid @RequestBody Person person) throws Exception {
        var updateUser = persons.updatePatch(person);
        return updateUser.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST)
        );
    }

    @PutMapping("/")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        this.persons.update(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        this.persons.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
