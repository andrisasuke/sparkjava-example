package com.hydra.spark.sample.service;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.hydra.spark.sample.persistence.domain.Person;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class ElasticsearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchService.class);

    @Inject
    Client client;

    @Inject
    Gson gson;

    public Integer addIndex(String index, String docType, Integer id, XContentBuilder builder){
        Integer resultId = null;
        IndexResponse response = client.prepareIndex(index, docType, String.valueOf(id))
                .setSource(builder)
                .execute()
                .actionGet();

        String idStr = response.getId();
        if(idStr != null){
            resultId = Integer.parseInt(idStr);
            LOGGER.info("Adding to index {} is success with response {}", index, response.getId());
        }
        return resultId;
    }

    public List<Person> findNameLike(String field, String value, String indexName , String type) {

        QueryBuilder queryBuilder = QueryBuilders.matchQuery(field, value).fuzziness(Fuzziness.AUTO).operator(Operator.AND);

        SearchRequestBuilder requestBuilder = client.prepareSearch(indexName)
                .setTypes(type)
                .setQuery(queryBuilder);
        requestBuilder.setFrom(0);
        requestBuilder.setSize(100);

        SearchResponse response = requestBuilder.execute().actionGet();

        LOGGER.info("search response {}", response.toString());

        return createPersonResponse(response);
    }

    private List<Person> createPersonResponse(SearchResponse response){
        List<Person> persons = new LinkedList<>();
        for(int i=0; i<response.getHits().getHits().length; i++){
            String eachSource = response.getHits().getHits()[i].getSourceAsString();
            Person person = gson.fromJson(eachSource, Person.class);
            persons.add(person);
        }
        return persons;
    }
}
