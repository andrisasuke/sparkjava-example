package com.hydra.spark.sample;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hydra.spark.sample.controller.PersonController;
import com.hydra.spark.sample.model.ErrorResponse;
import com.hydra.spark.sample.model.OkResponse;
import com.hydra.spark.sample.persistence.domain.Person;
import com.hydra.spark.sample.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static spark.Spark.*;

public class Main {

    static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)  {

        LOGGER.info("starting Main server at {}", new Date().toString());

        // inject all controllers
        Injector injector = Guice.createInjector(new AppModule());
        PersonController personController = injector.getInstance(PersonController.class);

        get("/", (req, res) -> "Hail, Hydra!");

        get("/person/:id", (req, res) -> {
            Integer id = Integer.parseInt(req.params(":id"));
            Person p = personController.getById(id);
            if(p != null) res.status(200); else res.status(404);
            return "person find : "+ ( p != null ? p.getName() : "not_found" );
        });

        put("/person/", (req, res) -> {
            Person personRequest = JsonUtil.GSON.fromJson(req.body(), Person.class);
            Person p = personController.add(personRequest.getName(), personRequest.getAddress(), personRequest.getPhone());
            if(p != null){
                res.status(201);
                return new OkResponse(201, "added new person name :"+p.getName());
            } else {
                res.status(406);
                return new OkResponse(406, "failed new person name :"+personRequest.getName() + ", check your server logs.." );
            }
        }, JsonUtil.json());


        // global exception handler
        exception(Exception.class, (e, req, res) -> {
            LOGGER.error(String.format("%s : Got an exception for request : %s  ", e.getLocalizedMessage(), req.url()));
            LOGGER.error(e.getLocalizedMessage(), e);
            res.status(500);
            res.body(JsonUtil.toJson(new ErrorResponse(500, "Internal Server Error")));
        });

    }

}
