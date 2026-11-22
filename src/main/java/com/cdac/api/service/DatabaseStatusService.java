package com.cdac.api.service;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/** Checks connectivity to both databases (PostgreSQL and MongoDB). */
@Service
public class DatabaseStatusService {

    private final DataSource dataSource;
    private final MongoTemplate mongoTemplate;

    public DatabaseStatusService(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.dataSource = dataSource;
        this.mongoTemplate = mongoTemplate;
    }

    public Map<String, String> status() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("postgres", postgresStatus());
        result.put("mongodb", mongoStatus());
        return result;
    }

    private String postgresStatus() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2) ? "up" : "down";
        } catch (Exception e) {
            return "down";
        }
    }

    private String mongoStatus() {
        try {
            mongoTemplate.executeCommand(new Document("ping", 1));
            return "up";
        } catch (Exception e) {
            return "down";
        }
    }
}
