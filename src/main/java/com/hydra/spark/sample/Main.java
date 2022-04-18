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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)  {

        logger.info("Starting Main server at {}", new Date().toString());

        // inject all controllers
        Injector injector = Guice.createInjector(new AppModule());
        PersonController personController = injector.getInstance(PersonController.class);
        Config config = injector.getInstance(Config.class);

        // Sample to reading config by key
        port(config.getConfig("config").getInt("port"));

        // define your routes
        get("/", (req, res) -> "Hi there!");

        path("/person", () -> {

            // find by param id
            get("/id/:id", (req, res) -> {
                Integer id = Integer.parseInt(req.params(":id"));
                Person p = personController.getById(id);
                if (p != null) {
                    res.status(200);
                } else {
                    res.status(404);
                }
                return "person find : " + (p != null ? p.getName() : "not_found");
            });

            // find by query parameter
            get("/name", (req, res) -> {
                String nameLike = req.queryParams("q");
                List<Person> persons = personController.findNameLike(nameLike);
                if (persons != null) {
                    res.status(200);
                    return new OkResponse<List<Person>>(200,
                            "found " + persons.size() + " person with name :" + nameLike,
                            persons);
                } else {
                    res.status(406);
                    return new ErrorResponse(406, "failed new person name :" + nameLike + ", check your server logs..");
                }
            }, JsonUtil.json());

            post("/", (req, res) -> {
                Person personRequest = JsonUtil.fromJson(req.body(), Person.class);
                try {
                    Person p = personController.add(personRequest);
                    res.status(201);
                    return new OkResponse<String>(201,
                            String.format("added new person Id: %d, Name : %s", p.getId(), p.getName()));
                } catch (ValidationException ve) {
                    res.status(400);
                    return new ErrorResponse(400, ve.getMessage());
                }

            }, JsonUtil.json());
        });

        // global exception handler
        exception(Exception.class, (e, req, res) -> {
            logger.error("{} : Got an exception for request : {}  ", e.getLocalizedMessage(), req.url());
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
