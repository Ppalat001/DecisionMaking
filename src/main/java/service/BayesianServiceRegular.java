package service;

import lombok.RequiredArgsConstructor;
import model.DecisionPreference;
import model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BayesianServiceRegular {

    @Autowired
    @Qualifier("decisionMongoTemplateRegular")
    private MongoTemplate decisionMongoTemplate;

    @Autowired
    @Qualifier("restaurantMongoTemplate")
    private MongoTemplate restaurantMongoTemplate;

    public Map<String, Object> calculateBayesianRecommendations(Map<String, Object> userPrefs) {
        List<DecisionPreference> history = decisionMongoTemplate.findAll(DecisionPreference.class);
        List<Restaurant> restaurants = restaurantMongoTemplate.findAll(Restaurant.class);

        Map<String, Double> scores = new HashMap<>();

        for (Restaurant r : restaurants) {
            double score = 1.0;

            Map<String, String> cuisineMap = castToStringMap(userPrefs.get("cuisine"));
            score *= weightedProb("cuisine", r.getType().toLowerCase(), cuisineMap, history);

            Map<String, String> dietaryMap = castToStringMap(userPrefs.get("dietary_preferences"));
            for (String dp : r.getDietary_preferences()) {
                score *= weightedProb("dietary_preferences", dp.toLowerCase(), dietaryMap, history);
            }

            List<String> dishes = getDishes(r);
            Map<String, String> dishMap = castToStringMap(userPrefs.get(r.getType().toLowerCase() + "_cuisine"));
            for (String d : dishes) {
                score *= weightedProb(r.getType().toLowerCase() + "_cuisine", d.toLowerCase(), dishMap, history);
            }

            Map<String, String> budgetMap = castToStringMap(userPrefs.get("budget_range"));
            score *= weightedProb("budget_range", normalize(r.getBudget_range()), budgetMap, history);

            Map<String, String> seatingMap = castToStringMap(userPrefs.get("seating"));
            score *= weightedProb("seating", normalize(r.getSeating()), seatingMap, history);

            Map<String, String> distanceMap = castToStringMap(userPrefs.get("distance"));
            score *= weightedProb("distance", normalize(r.getDistance()), distanceMap, history);

            scores.put(r.getName(), score);
        }

        List<Map<String, Object>> recommendations = scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    double roundedScore = Math.round(entry.getValue() * 10000.0) / 10000.0;
                    map.put("score", roundedScore); // raw Bayesian probability
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("recommendations", recommendations);
        return response;
    }

    private double weightedProb(String category, String key, Map<String, String> userPrefs, List<DecisionPreference> history) {
        int count = 0, total = 0;

        for (DecisionPreference dp : history) {
            Map<String, String> map = extractMap(dp, category);
            if (map != null && map.containsKey(key)) {
                try {
                    count += Integer.parseInt(map.get(key));
                } catch (NumberFormatException ignored) {
                }
                total++;
            }
        }

        double baseProb = (count + 1.0) / (total + 1.0); // ✅ Laplace smoothing

        // Normalize key for userPrefs lookup
        String normalizedKey = key.replace(" ", "").toLowerCase();
        String weightStr = userPrefs.getOrDefault(normalizedKey, "1");

        double weight = 1.0;
        try {
            weight = Double.parseDouble(weightStr);
        } catch (NumberFormatException ignored) {
        }

        // ✅ If user gave 0 weight (doesn't care), skip this field entirely
        if (weight == 0.0) {
            return -1.0; // marker to skip multiplication
        }

        // ✅ Apply exponential weight
        return Math.pow(baseProb, weight);
    }


    private Map<String, String> extractMap(DecisionPreference dp, String category) {
        return switch (category) {
            case "cuisine" -> dp.getCuisine();
            case "dietary_preferences" -> dp.getDietary_preferences();
            case "greek_cuisine" -> dp.getGreek_cuisine();
            case "italian_cusine" -> dp.getItalian_cusine();
            case "mexican_cuisine" -> dp.getMexican_cuisine();
            case "budget_range" -> dp.getBudget_range();
            case "seating" -> dp.getSeating();
            case "distance" -> dp.getDistance();
            default -> null;
        };
    }

    private List<String> getDishes(Restaurant r) {
        return switch (r.getType().toLowerCase()) {
            case "greek" -> r.getGreek_food();
            case "italian" -> r.getItalian_food();
            case "mexican" -> r.getMexican_food();
            default -> new ArrayList<>();
        };
    }

    private String normalize(String input) {
        return input == null ? "" : input.toLowerCase().replace("£", "").replace("-", "").replace(" ", "");
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> castToStringMap(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .filter(e -> e.getKey() instanceof String && e.getValue() instanceof String)
                    .collect(Collectors.toMap(
                            e -> (String) e.getKey(),
                            e -> (String) e.getValue()
                    ));
        }
        return new HashMap<>();
    }
}
