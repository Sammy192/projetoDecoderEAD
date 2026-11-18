package com.ead.recommendationagent.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questions")
public class RecommendationAgentController {

    @GetMapping()
    public String getRecommendation() {
        return "Recommendation Agent is working!";
    }

}
