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

import java.io.IOException;
import java.util.List;

public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    @Inject
    PersonDao personDao;

    @Inject
    Config config;

    @Inject
    ElasticsearchService elasticsearchService;

    public Person getById(Integer id){
        return personDao.find(id);
    }
    
    public Person add(String name, String address, String phone) {
        Person result = personDao.save(name, address, phone);
        if(result != null){

            XContentBuilder builder = null;
            try {
                builder = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", name)
                        .field("address", address)
                        .field("phone", phone)
                        .endObject();
                elasticsearchService.addIndex(ESValue.PERSON_INDEX, ESValue.POFILE_DOCTYPE, result.getId(), builder);
            } catch (IOException e) {
                LOGGER.error("Failed to build XContentBuilder for person, {}", e.getMessage());
            }
        }
        return result;
    }

    public List<Person> findNameLike(String name){
        elasticsearchService.findNameLike("title", name, "andri", "news");
        return personDao.findName(name);
    }

    public String config() {
        // string config
        Config root = config.getConfig("config");
        String titleString1 = config.getConfig("config").getConfig("notification").getString("title");
        String titleString2 = config.getString("config.notification.title");
        String messageString1 = config.getString("config.notification.message");
        LOGGER.info("root config {}", root);
        LOGGER.info("string title {}", titleString1);
        LOGGER.info("string title {}", titleString2);
        LOGGER.info("string message {}", messageString1);

        // json config
        Config jsonRoot = config.getConfig("foo");
        int jsonInt1 = config.getConfig("foo").getInt("bar");
        int jsonInt2 = config.getInt("foo.bar");
        int jsonInt3 = config.getInt("foo.baz");
        LOGGER.info("root json {}", jsonRoot);
        LOGGER.info("int json {}", jsonInt1);
        LOGGER.info("int bar {}", jsonInt2);
        LOGGER.info("int baz {}", jsonInt3);

        return "OK";
    }
}
