package com.portfolio.porfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.porfolio.models.Portfolio;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
    /**
     * @description Retrieves the first portfolio record ordered by ID.
     *              Avoids loading the entire table into memory via findAll().
     * @return Optional containing the first Portfolio if present.
     */
    Optional<Portfolio> findFirstByOrderByIdAsc();
}
