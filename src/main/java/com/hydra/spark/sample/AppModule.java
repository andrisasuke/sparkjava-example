package com.hydra.spark.sample;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hydra.spark.sample.controller.PersonController;
import com.hydra.spark.sample.persistence.dao.PersonDao;
import com.hydra.spark.sample.service.ElasticsearchService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AppModule implements Module {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppModule.class);

    @Override
    public void configure(Binder binder) {
        // bind your all singleton classes (DAO, services, controller, etc)
        binder.bind(PersonDao.class).asEagerSingleton();
        binder.bind(PersonController.class).asEagerSingleton();
        binder.bind(ElasticsearchService.class).asEagerSingleton();
    }

    @Singleton
    @Provides
    public Gson provideGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Singleton
    @Provides
    public EntityManager providesEntityManager() {
        LOGGER.info("Initialize entity manager from persistence");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();
        return em;
    }

    @Singleton
    @Provides
    public Config providesConfigFactory(){
        return ConfigFactory.load("application");
    }

    @Singleton
    @Provides
    public Client provideElasticSearchClient(){
        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            LOGGER.info("ES transport client is created {}", client);
            return client;
        }catch (UnknownHostException e){
            LOGGER.error("Failed to create ES transport client, {}", e.getMessage());
        }
        return null;
    }

    @Singleton
    @Provides
    public Validator provideValidator(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }
}
