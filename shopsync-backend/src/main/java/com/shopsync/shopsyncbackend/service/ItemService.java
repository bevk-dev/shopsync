package com.shopsync.shopsyncbackend.service;

import com.shopsync.shopsyncbackend.entity.Item;
import com.shopsync.shopsyncbackend.entity.ShoppingList;
import com.shopsync.shopsyncbackend.repository.ItemRepository;
import com.shopsync.shopsyncbackend.repository.ShoppingListRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final AuthorizationService authService;

    public ItemService(ItemRepository itemRepository, ShoppingListRepository shoppingListRepository, AuthorizationService authService) {
        this.itemRepository = itemRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.authService = authService;
    }

    public List<Item> getAllItemsInList(Long listId, String auth0Id) {
        ShoppingList list = shoppingListRepository.findById(listId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seznam ne obstaja."));

        authService.validateListAccess(list, auth0Id);
        return itemRepository.findByShoppingListId(listId);
    }

    public List<Item> getPendingItems(Long listId, String auth0Id) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seznam ne obstaja."));
        authService.validateListAccess(list, auth0Id);
        return itemRepository.findByShoppingListIdAndPurchasedFalse(listId);
    }

    public Item togglePurchased(Long itemId, String auth0Id) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found."));

        authService.validateListAccess(item.getShoppingList(), auth0Id);

        item.setPurchased(!item.isPurchased());
        return itemRepository.save(item);
    }

    public Item updateItem(Long itemId, Item updatedItem, String auth0Id) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found."));

        authService.validateListAccess(existingItem.getShoppingList(), auth0Id);

        if (updatedItem.getName() != null) {
            existingItem.setName(updatedItem.getName());
        }

        if (updatedItem.getQuantity() != null) {
            existingItem.setQuantity(updatedItem.getQuantity());
        }

        if (updatedItem.isPurchased() != null) {
            existingItem.setPurchased(updatedItem.isPurchased());
        }
        return itemRepository.save(existingItem);
    }

    @Transactional
    public void deleteItem(Long itemId, String auth0Id) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Izdelek ne obstaja"));

        authService.validateListAccess(item.getShoppingList(), auth0Id);

        ShoppingList list = item.getShoppingList();
        if (list != null) {
            list.getItems().remove(item);
        }

        itemRepository.delete(item);
    }
}
