package service;

import model.Restaurant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FuzzyRecommendationService {

    private final MongoTemplate restaurantMongoTemplate;

    public FuzzyRecommendationService(@Qualifier("restaurantMongoTemplate") MongoTemplate restaurantMongoTemplate) {
        this.restaurantMongoTemplate = restaurantMongoTemplate;
    }

    public List<String> recommend(Map<String, Object> userPrefs) {
        List<Restaurant> restaurants = restaurantMongoTemplate.findAll(Restaurant.class);

        Map<String, String> budgetPref = castToStringMap(userPrefs.get("budget_range"));
        Map<String, String> distancePref = castToStringMap(userPrefs.get("distance"));
        Map<String, String> cuisinePref = castToStringMap(userPrefs.get("cuisine"));

        Map<String, Double> scores = new HashMap<>();

        for (Restaurant r : restaurants) {
            double score = 1.0;

            String normalizedBudget = normalize(r.getBudget_range());
            String normalizedDistance = normalize(r.getDistance());
            String normalizedCuisine = r.getType().toLowerCase();

            score *= fuzzyScore(normalizedBudget, budgetPref);
            score *= fuzzyScore(normalizedDistance, distancePref);
            score *= fuzzyScore(normalizedCuisine, cuisinePref); // new

            scores.put(r.getName(), score);
        }

        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double fuzzyScore(String key, Map<String, String> userPrefs) {
        if (!userPrefs.containsKey(key)) return 0.2;
        try {
            double weight = Double.parseDouble(userPrefs.get(key));
            if (weight == 0.0) return 0.2;
            return 0.5 + (0.5 * (weight / 3.0));
        } catch (NumberFormatException e) {
            return 0.2;
        }
    }

    private String normalize(String input) {
        if (input == null) return "";
        return input.toLowerCase().replace("£", "").replace("-", "").replace(" ", "");
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> castToStringMap(Object obj) {
        try {
            return (Map<String, String>) obj;
        } catch (ClassCastException e) {
            return new HashMap<>();
        }
    }
}
