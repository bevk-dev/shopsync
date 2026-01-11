package com.shopsync.shopsyncbackend.controller;

import com.shopsync.shopsyncbackend.entity.Item;
import com.shopsync.shopsyncbackend.entity.ShoppingList;
import com.shopsync.shopsyncbackend.repository.ShoppingListRepository;
import com.shopsync.shopsyncbackend.service.ItemService;
import com.shopsync.shopsyncbackend.service.ShoppingListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;
    private final ItemService itemService;
    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListController(ShoppingListService shoppingListService, ItemService itemService, ShoppingListRepository shoppingListRepository) {
        this.shoppingListService = shoppingListService;
        this.itemService = itemService;
        this.shoppingListRepository = shoppingListRepository;
    }

    @PostMapping
    public ResponseEntity<ShoppingList> createList(@RequestBody ShoppingList list, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(shoppingListService.createList(jwt.getSubject(), list));
    }

    @PostMapping("/{listId}/items")
    public ResponseEntity<Item> addItemToList(@PathVariable Long listId, @RequestBody Item item, @AuthenticationPrincipal Jwt jwt) {
        Item created = shoppingListService.addItemToList(listId, item, jwt.getSubject());
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{listId}/items")
    public ResponseEntity<List<Item>> getAllItemsInList(@PathVariable Long listId, @AuthenticationPrincipal Jwt jwt) {
        List<Item> items = itemService.getAllItemsInList(listId, jwt.getSubject());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{listId}/pending")
    public ResponseEntity<List<Item>> getPendingItems(@PathVariable Long listId, @AuthenticationPrincipal Jwt jwt) {
        List<Item> items = itemService.getPendingItems(listId, jwt.getSubject());
        return ResponseEntity.ok(items);
    }

    @GetMapping
    public ResponseEntity<List<ShoppingList>> getMyLists(@AuthenticationPrincipal Jwt jwt) {
        List<ShoppingList> lists = shoppingListService.getListsForUser(jwt.getSubject());
        return ResponseEntity.ok(lists);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<String> deleteList(@PathVariable Long listId, @AuthenticationPrincipal Jwt jwt) {
        shoppingListService.deleteList(listId, jwt.getSubject());
        return ResponseEntity.ok("Seznam uspešno izbrisan.");
    }

    @PostMapping("/{listId}/share")
    public ResponseEntity<String> shareList(@PathVariable Long listId, @RequestBody String email, @AuthenticationPrincipal Jwt jwt) {
        shoppingListService.shareList(listId, email, jwt.getSubject());
        return ResponseEntity.ok("Seznam uspešno deljen.");
    }

    @PatchMapping("/{listId}/name")
    public ResponseEntity<ShoppingList> updateListName(@PathVariable Long listId, @RequestBody String newName, @AuthenticationPrincipal Jwt jwt) {
        String name = newName.replace("\"", "");
        ShoppingList updatedList = shoppingListService.updateListName(listId, name, jwt.getSubject());
        return ResponseEntity.ok(updatedList);
    }

}
