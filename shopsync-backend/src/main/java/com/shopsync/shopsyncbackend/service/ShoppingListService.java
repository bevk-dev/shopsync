package com.shopsync.shopsyncbackend.service;

import com.shopsync.shopsyncbackend.entity.Item;
import com.shopsync.shopsyncbackend.entity.Product;
import com.shopsync.shopsyncbackend.repository.ItemRepository;
import com.shopsync.shopsyncbackend.entity.ShoppingList;
import com.shopsync.shopsyncbackend.repository.ProductRepository;
import com.shopsync.shopsyncbackend.repository.ShoppingListRepository;
import com.shopsync.shopsyncbackend.entity.User;
import com.shopsync.shopsyncbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final AuthorizationService authorizationService;
    private final ItemService itemService;

    public ShoppingListService(ShoppingListRepository shoppingListRepository, UserRepository userRepository, ItemRepository itemRepository, ProductRepository productRepository, AuthorizationService authorizationService, ItemService itemService) {
        this.shoppingListRepository = shoppingListRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
        this.authorizationService = authorizationService;
        this.itemService = itemService;
    }

    public ShoppingList createList(String auth0Id, ShoppingList list) {
        // v bazi poiščem lastnika, če ta podan user sploh obstaja
        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new RuntimeException("Uporabnik ne obstaja."));
        list.setOwner(user);
        return shoppingListRepository.save(list);
    }

    public Item addItemToList(Long listId, Item itemRequest, String userAuth0Id) {
        // 1. preveri če seznam kamor hočem dodajati obstaja
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("Seznam ni bil najden"));

        authorizationService.validateListAccess(list, userAuth0Id);

        Optional<Item> existingItemOnList = itemRepository
                .findByNameAndShoppingListId(itemRequest.getName(), listId);

        if(existingItemOnList.isPresent()) {
            Item itemToUpdate = existingItemOnList.get();
            return itemService.updateItem(itemToUpdate.getId(), itemRequest, userAuth0Id);
        }

        String listOwnerId = list.getOwner().getAuth0Id();

        // 2. preveri osebni katalog izdelkov, če uporabnik že ima za izdelek v katalogu
        Optional<Product> existingProduct = productRepository
                .findByNameAndOwnerAuth0Id(itemRequest.getName(), listOwnerId);

        if (existingProduct.isPresent()) {
            // Izdelek najden v katalogu, povežemo z requestanim itemom
            itemRequest.setProduct(existingProduct.get());
        } else {
            // Izdelka ni v katalogu. Nastavimo product na null.
            // Frontend bo pokazal gumb "Dodaj v katalog" ali pa bom gumb že dal nekam drugam.
            itemRequest.setProduct(null);
        }

        itemRequest.setPurchased(false);
        itemRequest.setShoppingList(list);
        return itemRepository.save(itemRequest);
    }

    public List<ShoppingList> getListsForUser(String auth0Id) {
        return shoppingListRepository.findAllForUser(auth0Id);
    }

    public void deleteList(Long listId, String auth0Id) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Seznam ne obstaja"));

        authorizationService.validateListOwner(list, auth0Id);

        shoppingListRepository.delete(list);
    }

    public void shareList(Long listId, String friendEmail, String ownerAuth0Id) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Seznam ne obstaja"));

        authorizationService.validateListOwner(list, ownerAuth0Id);

        User friend = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new EntityNotFoundException("Uporabnik s tem emailom ni najden."));

        if (!list.getSharedWith().contains(friend)) {
            list.getSharedWith().add(friend);
            shoppingListRepository.save(list);
        }
    }

    public ShoppingList updateListName(Long listId, String newName, String userId) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("Seznam ni bil najden."));

        authorizationService.validateListAccess(list, userId);

        list.setName(newName);
        return shoppingListRepository.save(list);
    }
}
