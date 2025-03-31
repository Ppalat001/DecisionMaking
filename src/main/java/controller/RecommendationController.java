package controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendationController {

    private final BayesianService bayesianService;
    private final CollaborativeFilteringService collaborativeFilteringService;
    private final FuzzyRecommendationService fuzzyRecommendationService;
    private final ContentBasedRecommendationService contentBasedRecommendationService;
    private final BayesianServiceRegular bayesianServiceRegular;
    private final CollaborativeFilteringServiceRegular collaborativeFilteringServiceRegular;
    private final FuzzyRecommendationServiceRegular fuzzyRecommendationServiceRegular;
    private final ContentBasedRecommendationServiceRegular contentBasedRecommendationServiceRegular;

    @PostMapping("/bayesian")
    public ResponseEntity<Map<String, Object>> recommendBayesian(@RequestBody Map<String, Object> userPreferences) {
        Map<String, Object> response = bayesianService.calculateBayesianRecommendations(userPreferences);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/collaborative")
    public ResponseEntity<Map<String, Object>> recommendCollaborative(@RequestBody Map<String, Object> userPreferences) {
        Map<String, Object> response = collaborativeFilteringService.recommendBasedOnSimilarUsers(userPreferences);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/fuzzy")
    public ResponseEntity<Map<String, Object>> recommendFuzzy(@RequestBody Map<String, Object> userPreferences) {
        Map<String, Object> response = fuzzyRecommendationService.recommend(userPreferences);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/content")
    public ResponseEntity<Map<String, Object>> recommendContentBased(@RequestBody Map<String, Object> userPreferences) {
        Map<String, Object> response = contentBasedRecommendationService.recommend(userPreferences);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bayesian/regular")
    public ResponseEntity<Map<String, Object>> recommendBayesianReg(@RequestBody Map<String, Object> userPreferences) {
        Map<String, Object> response = bayesianServiceRegular.calculateBayesianRecommendations(userPreferences);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/collaborative/regular")
    public ResponseEntity<Map<String, Object>> recommendCollaborativeReg(@RequestBody Map<String, Object> userPreferences) {
        Map<String, Object> response = collaborativeFilteringServiceRegular.recommendBasedOnSimilarUsers(userPreferences);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/fuzzy/regular")
    public ResponseEntity<Map<String, Object>> recommendFuzzyReg(@RequestBody Map<String, Object> userPreferences) {
        Map<String, Object> response = fuzzyRecommendationServiceRegular.recommend(userPreferences);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/content/regular")
    public ResponseEntity<Map<String, Object>> recommendContentBasedReg(@RequestBody Map<String, Object> userPreferences) {
        Map<String, Object> response = contentBasedRecommendationServiceRegular.recommend(userPreferences);
        return ResponseEntity.ok(response);
    }
}