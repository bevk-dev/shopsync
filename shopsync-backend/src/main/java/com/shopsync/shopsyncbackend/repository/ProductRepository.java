package com.shopsync.shopsyncbackend.repository;

import com.shopsync.shopsyncbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    List<Product> findByCategoryAndOwner_Auth0Id(String category, String auth0Id);

    boolean existsByName(String name);

    @Query("SELECT p FROM Product p JOIN p.owner u WHERE p.name = :name AND u.auth0Id = :userAuth0Id")
    Optional<Product> findByNameAndOwnerAuth0Id(String name, String userAuth0Id);

    List<Product> findByOwner_Auth0Id(String auth0Id);
}
