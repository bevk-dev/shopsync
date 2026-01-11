package com.shopsync.shopsyncbackend.repository;

import com.shopsync.shopsyncbackend.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    List<ShoppingList> findByOwner_Auth0Id(String auth0Id);

    @Query("SELECT DISTINCT s FROM ShoppingList s " +
            "LEFT JOIN s.sharedWith sw " +
            "WHERE s.owner.auth0Id = :auth0Id OR sw.auth0Id = :auth0Id")
    List<ShoppingList> findAllForUser(@Param("auth0Id") String auth0Id);
}
