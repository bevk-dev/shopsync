package com.shopsync.shopsyncbackend.service;

import com.shopsync.shopsyncbackend.entity.Product;
import com.shopsync.shopsyncbackend.entity.ShoppingList;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthorizationService {

    public void validateListAccess(ShoppingList list, String auth0Id) {
        // 1. Preveri če je uporabnik LASTNIK
        boolean isOwner = list.getOwner().getAuth0Id().equals(auth0Id);

        // 2. Preveri če je uporabnik med tistimi, s katerimi je seznam DELJEN
        boolean isShared = list.getSharedWith().stream()
                .anyMatch(user -> user.getAuth0Id().equals(auth0Id));

        if (!isOwner && !isShared) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nimate pravic za urejanje tega seznama.");
        }
    }

    public void validateListOwner(ShoppingList list, String auth0Id) {
        if (!list.getOwner().getAuth0Id().equals(auth0Id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Samo lastnik lahko izvede to dejanje.");
        }
    }

    public void validateProductOwner(Product product, String auth0Id) {
        if (!product.getOwner().getAuth0Id().equals(auth0Id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nimate pravice za urejanje tega izdelka.");
        }
    }
}
