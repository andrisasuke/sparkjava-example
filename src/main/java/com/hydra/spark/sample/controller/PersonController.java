package com.hydra.spark.sample.controller;

import com.google.inject.Inject;
import com.hydra.spark.sample.persistence.dao.PersonDao;
import com.hydra.spark.sample.persistence.domain.Person;
import com.hydra.spark.sample.service.ElasticsearchService;
import com.hydra.spark.sample.util.ESValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Inject
    PersonDao personDao;

    @Inject
    ElasticsearchService elasticsearchService;

    @Inject
    Validator validator;

    public Person getById(Integer id){
        return personDao.find(id);
    }
    
    public Person add(Person person) {
        validatePerson(person);
        Person result = personDao.save(person.getName(), person.getAddress(), person.getPhone());
        elasticsearchService.addIndex(ESValue.PERSON_INDEX, ESValue.POFILE_DOCTYPE, result.getId(), result);
        return result;
    }

    public List<Person> findNameLike(String name){
        List<Person> persons = elasticsearchService.findNameLike("name", name, ESValue.PERSON_INDEX);
        if (!persons.isEmpty()) {
            return persons;
        }
        return personDao.findName(name);
    }

    protected void validatePerson(Person data) throws ValidationException {
        Set<ConstraintViolation<Person>> violations = validator.validate(data);
        for (ConstraintViolation<Person> violation : violations) {
            if (!StringUtils.isEmpty(violation.getMessage())){
                throw new ValidationException(violation.getMessage());
            }
        }
    }

}
