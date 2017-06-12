package com.hydra.spark.sample;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hydra.spark.sample.controller.PersonController;
import com.hydra.spark.sample.model.ErrorResponse;
import com.hydra.spark.sample.model.OkResponse;
import com.hydra.spark.sample.persistence.domain.Person;
import com.hydra.spark.sample.util.JsonUtil;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;

import javax.validation.ValidationException;
import java.util.*;

import static spark.Spark.*;

public class Main {

    static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)  {

        LOGGER.info("Starting Main server at {}", new Date().toString());

        // inject all controllers
        Injector injector = Guice.createInjector(new AppModule());
        PersonController personController = injector.getInstance(PersonController.class);
        Config config = injector.getInstance(Config.class);

        LOGGER.info("Sample to reading config by key {} : {}", "environment", config.getConfig("config").getString("environment"));

        // define your routes
        get("/", (req, res) -> "Hi there!");

        get("/person/:id", (req, res) -> {
            Integer id = Integer.parseInt(req.params(":id"));
            Person p = personController.getById(id);
            if(p != null) res.status(200); else res.status(404);
            return "person find : "+ ( p != null ? p.getName() : "not_found" );
        });

        get("/person/name/:name", (req, res) -> {
            String nameLike = req.params(":name");
            List<Person> persons = personController.findNameLike(nameLike);
            if(persons != null){
                res.status(200);
                String json = JsonUtil.toJson(persons);
                return new OkResponse(200, "found "+persons.size()+" person with name :"+nameLike, json);
            } else {
                res.status(406);
                return new ErrorResponse(406, "failed new person name :"+nameLike + ", check your server logs.." );
            }
        }, JsonUtil.json());

        put("/person/", (req, res) -> {
            Person personRequest = JsonUtil.GSON.fromJson(req.body(), Person.class);
            try {
                Person p = personController.add(personRequest);
                res.status(201);
                return new OkResponse(201, "added new person name : "+p.getName());
            } catch (ValidationException ve){
                res.status(400);
                return new ErrorResponse(400, ve.getMessage() );
            }

        }, JsonUtil.json());

        // global exception handler
        exception(Exception.class, (e, req, res) -> {
            LOGGER.error(String.format("%s : Got an exception for request : %s  ", e.getLocalizedMessage(), req.url()));
            LOGGER.error(e.getLocalizedMessage(), e);
            res.status(500);
            res.body(JsonUtil.toJson(new ErrorResponse(500, "Internal Server Error")));
        });

        // apply cors filter
        Filter corsFilter = (request, response) -> {
            Map<String, String> corsHeaders = Collections.unmodifiableMap(new HashMap<String, String>() {
                {
                    put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
                    put("Access-Control-Allow-Origin", "*");
                    put("Access-Control-Allow-Headers", "Content-Type");
                    put("Access-Control-Max-Age", "1800");//30 min
                }
            });
            corsHeaders.forEach(response::header);
        };
        after(corsFilter);

    }

}
