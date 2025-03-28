package controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CollaborativeFilteringService;
import service.ContentBasedRecommendationService;
import service.FuzzyRecommendationService;
import service.BayesianService;

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
}