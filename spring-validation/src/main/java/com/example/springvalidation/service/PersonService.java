package com.example.springvalidation.service;

import com.example.springvalidation.entity.Person;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

@Service
@Validated
public class PersonService {

    public void validatePerson(@Valid Person person) {
        // do something
    }

    @Validated(AddPersonGroup.class)
    public void validatePersonGroupForAdd(@Valid Person person) {
        // do something
    }

    @Validated(DeletePersonGroup.class)
    public void validatePersonGroupForDelete(@Valid Person person) {
        // do something
    }

}
