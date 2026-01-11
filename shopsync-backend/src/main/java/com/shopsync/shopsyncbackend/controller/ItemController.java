package com.shopsync.shopsyncbackend.controller;

import com.shopsync.shopsyncbackend.entity.Item;
import com.shopsync.shopsyncbackend.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(@PathVariable Long itemId, @RequestBody Item itemUpdates, @AuthenticationPrincipal Jwt jwt) {
        Item updated = itemService.updateItem(itemId, itemUpdates, jwt.getSubject());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{itemId}/purchase")
    public ResponseEntity<Item> toggleItem(@PathVariable Long itemId, @AuthenticationPrincipal Jwt jwt) {
        Item item = itemService.togglePurchased(itemId, jwt.getSubject());
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> deleteItem(@PathVariable Long itemId, @AuthenticationPrincipal Jwt jwt) {
        itemService.deleteItem(itemId, jwt.getSubject());
        return ResponseEntity.ok("Item " + itemId + " deleted successfully.");
    }
}
