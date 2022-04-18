package com.hydra.spark.sample.service;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.hydra.spark.sample.persistence.domain.Person;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ElasticsearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchService.class);

    @Inject
    Gson gson;

    @Inject
    RestHighLevelClient restHighLevelClient;

    public Integer addIndex(String index, String docType, Integer id, Person document) {
        IndexRequest request = new IndexRequest(index);
        request.id(String.valueOf(id));
        request.type(docType);
        request.source(gson.toJson(document), XContentType.JSON);
        try {
            IndexResponse idxResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            logger.info("Adding document to index {} is success with response {}", index, idxResponse.getId());
            return Integer.valueOf(idxResponse.getId());
        } catch (IOException e) {
            logger.error("failed add document to index {}, {}", index, e.getMessage());
            return null;
        }
    }

    public List<Person> findNameLike(String field, String value, String indexName) {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery(field, value)
                .fuzziness(Fuzziness.AUTO).operator(Operator.AND);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            logger.info("search response {}", response.toString());
            return createPersonResponse(response);
        } catch (IOException e) {
            logger.error("failed search matchQuery to elasticsearch {}", e.getMessage());
            return new ArrayList<>();
        }
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
