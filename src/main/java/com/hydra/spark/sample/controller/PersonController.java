package com.hydra.spark.sample.controller;

import com.google.inject.Inject;
import com.hydra.spark.sample.persistence.dao.PersonDao;
import com.hydra.spark.sample.persistence.domain.Person;
import com.hydra.spark.sample.service.ElasticsearchService;
import com.hydra.spark.sample.util.ESValue;
import com.typesafe.config.Config;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    @Inject
    PersonDao personDao;

    @Inject
    Config config;

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
        if(result != null){

            XContentBuilder builder = null;
            try {
                builder = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", person.getName())
                        .field("address", person.getAddress())
                        .field("phone", person.getPhone())
                        .endObject();
                elasticsearchService.addIndex(ESValue.PERSON_INDEX, ESValue.POFILE_DOCTYPE, result.getId(), builder);
            } catch (IOException e) {
                LOGGER.error("Failed to build XContentBuilder for person, {}", e.getMessage());
            }
        }
        return result;
    }

    public List<Person> findNameLike(String name){
        List<Person> persons = elasticsearchService.findNameLike("name", name, ESValue.PERSON_INDEX, ESValue.POFILE_DOCTYPE);
        if(!persons.isEmpty()) return persons;
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
