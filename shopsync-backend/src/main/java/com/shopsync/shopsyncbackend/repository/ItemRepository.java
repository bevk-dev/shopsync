package com.shopsync.shopsyncbackend.repository;

import com.shopsync.shopsyncbackend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByShoppingListId(Long shoppingListId);

    List<Item> findByShoppingListIdAndPurchasedFalse(Long shoppingListId);

    Optional<Item> findByNameAndShoppingListId(String name, Long shoppingListId);
}
