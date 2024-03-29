package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Optional;

/**
 * 3. Мидл
 * 3.4. Spring
 * 3.4.6. Rest
 * 1. RESTFul. Описание архитектуры [#6884]
 * SimplePersonService реализация бизнес логики обработки модели Person.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 24.04.2023
 */
@Service
@AllArgsConstructor
@Slf4j
public class SimplePersonService implements PersonService {
    private final PersonRepository personRepository;

    @Override
    public Optional<Person> save(Person person) {
        try {
            personRepository.save(person);
            return Optional.of(person);
        } catch (Exception e) {
            log.error("Person save error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Person> updatePatch(Person person) throws InvocationTargetException, IllegalAccessException {
        var currentPerson = personRepository.findById(person.getId());
        if (currentPerson.isEmpty()) {
            throw new NullPointerException("Person id: " + person.getId() + " , not found");
        }
        var methods = currentPerson.get().getClass().getDeclaredMethods();
        var namePerMethod = new HashMap<String, Method>();
        for (var method : methods) {
            String name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }
        for (var name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                Method getMethod = namePerMethod.get(name);
                Method setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    return Optional.empty();
                }

                Object newValue = getMethod.invoke(person);
                if (newValue != null) {
                    setMethod.invoke(currentPerson.get(), newValue);
                }
            }
        }
        personRepository.save(currentPerson.get());
        return currentPerson;
    }

    @Override
    public Optional<Person> findById(int personId) {
        return personRepository.findById(personId);
    }

    @Override
    public void update(Person person) {
        personRepository.save(person);
    }

    @Override
    public void deleteById(int personId) {
        personRepository.deleteById(personId);
    }

    @Override
    public Iterable<Person> findAll() {
        return personRepository.findAll();
    }
}
