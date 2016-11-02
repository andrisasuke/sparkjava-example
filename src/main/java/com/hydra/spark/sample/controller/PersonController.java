package com.hydra.spark.sample.controller;

import com.google.inject.Inject;
import com.hydra.spark.sample.persistence.dao.PersonDao;
import com.hydra.spark.sample.persistence.domain.Person;

import java.util.List;

public class PersonController {

    @Inject
    PersonDao personDao;

    public Person getById(Integer id){
        return personDao.find(id);
    }
    
    public Person add(String name, String address, String phone) {
        return personDao.save(name, address, phone);
    }

    public List<Person> findNameLike(String name){
        return personDao.findName(name);
    }
}
