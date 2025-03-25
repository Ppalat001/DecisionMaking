package config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    private static final String URI = "mongodb+srv://giorgos:12345@cluster0.b776d.mongodb.net/";

    @Primary
    @Bean(name = "decisionMongoTemplate")
    public MongoTemplate decisionMongoTemplate() {
        return new MongoTemplate(MongoClients.create(URI), "DecisionPreferences");
    }

    @Bean(name = "restaurantMongoTemplate")
    public MongoTemplate restaurantMongoTemplate() {
        return new MongoTemplate(MongoClients.create(URI), "Restaurants"); // ✅ This points to correct DB
    }
}
