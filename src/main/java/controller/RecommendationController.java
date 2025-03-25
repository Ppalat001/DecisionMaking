package controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CollaborativeFilteringService;
import service.ContentBasedRecommendationService;
import service.FuzzyRecommendationService;
import service.RecommendationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService bayesianRecommendationService;
    private final CollaborativeFilteringService collaborativeFilteringService;
    private final FuzzyRecommendationService fuzzyRecommendationService;
    private final ContentBasedRecommendationService contentBasedRecommendationService;

    @PostMapping("/bayesian")
    public ResponseEntity<Map<String, Object>> recommendBayesian(@RequestBody Map<String, Object> userPreferences) {
        List<Map<String, Object>> recommendations = bayesianRecommendationService.calculateBayesianRecommendations(userPreferences);
        return ResponseEntity.ok(Map.of("top_recommendations", recommendations));
    }

    @PostMapping("/collaborative")
    public ResponseEntity<Map<String, Object>> recommendCollaborative(@RequestBody Map<String, Object> userPreferences) {
        List<String> recommendations = collaborativeFilteringService.recommendBasedOnSimilarUsers(userPreferences);
        return ResponseEntity.ok(Map.of("collaborative_recommendations", recommendations));
    }

    @PostMapping("/fuzzy")
    public ResponseEntity<Map<String, Object>> recommendFuzzy(@RequestBody Map<String, Object> userPreferences) {
        List<String> recommendations = fuzzyRecommendationService.recommend(userPreferences);
        return ResponseEntity.ok(Map.of("fuzzy_recommendations", recommendations));
    }

    @PostMapping("/content")
    public ResponseEntity<Map<String, Object>> recommendContentBased(@RequestBody Map<String, Object> userPreferences) {
        List<String> recommendations = contentBasedRecommendationService.recommend(userPreferences);
        return ResponseEntity.ok(Map.of("content_based_recommendations", recommendations));
    }
}