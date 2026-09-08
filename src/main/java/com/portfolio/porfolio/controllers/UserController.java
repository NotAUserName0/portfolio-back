package com.portfolio.porfolio.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import com.portfolio.porfolio.service.portfolio.PortfolioService;
import com.portfolio.porfolio.utils.ApiResponse;

@RestController
public class UserController {

    private final PortfolioService portfolioService;

    public UserController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/liveness")
    public String helthCheck() {
        //returns JSON
        return "Service is running";
    }

    @GetMapping("/getPortfolio")
    public ResponseEntity<ApiResponse<Object>> getPortfolio() {
        return portfolioService.getPortfolio().toResponseEntity();
    }
}
