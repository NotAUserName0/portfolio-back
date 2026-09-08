package com.portfolio.porfolio.controllers;

import com.portfolio.porfolio.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

import com.portfolio.porfolio.dto.PortfolioDto;
import com.portfolio.porfolio.service.portfolio.PortfolioService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    // Dependency injection for the PortfolioService
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Object>> createPortfolioController(
        @RequestPart("portfolioData") PortfolioDto data,
        @RequestPart(value = "profilePic", required = false) MultipartFile profileFile,
        @RequestPart(value = "proyect", required = false) List<MultipartFile> proyectFile,
        @RequestPart(value = "linksIcon", required = false) List<MultipartFile> linkFile,
        @RequestPart(value = "icon", required = false) List<MultipartFile> iconFile
    ) {
        return portfolioService.createPortfolioService(data, profileFile, proyectFile, linkFile, iconFile).toResponseEntity();
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Object>> getPortfolio() {
        return portfolioService.getPortfolio().toResponseEntity();
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Object>> updatePortfolio(
        @PathVariable("id") Integer id,
        @RequestPart("portfolioData") PortfolioDto data,
        @RequestPart(value = "profilePic", required = false) MultipartFile profileFile,
        @RequestPart(value = "proyect", required = false) List<MultipartFile> proyectFile,
        @RequestPart(value = "linksIcon", required = false) List<MultipartFile> linkFile,
        @RequestPart(value = "icon", required = false) List<MultipartFile> iconFile
    ) {
        return portfolioService.updatePortfolioService(id, data, profileFile, proyectFile, linkFile, iconFile).toResponseEntity();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePortfolio(
        @PathVariable("id") Integer id
    ) {
        return portfolioService.deletePortfolioService(id).toResponseEntity();
    }
}
