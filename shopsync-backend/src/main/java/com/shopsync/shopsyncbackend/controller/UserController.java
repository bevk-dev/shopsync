package com.shopsync.shopsyncbackend.controller;


import com.shopsync.shopsyncbackend.entity.User;
import com.shopsync.shopsyncbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@AuthenticationPrincipal Jwt jwt) {
        String namespace = "https://shopsync-api.com";

        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString(namespace + "/email");
        String name = jwt.getClaimAsString(namespace + "/name");

        // Če žeton ne vsebuje emaila, ne smemo ustvariti uporabnika
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = userService.syncUserWithAuth0(auth0Id, email, name);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /*@GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUserByAuth0Id(jwt.getSubject());
        return ResponseEntity.ok(user);
    } */

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        // 1. Pridobimo podatke iz žetona
        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");

        // Če emaila ni v standardnem polju, poskusi še s tvojim namespace-om
        if (email == null) {
            email = jwt.getClaimAsString("https://shopsync-api.com/email");
        }

        // 2. Namesto getUserByAuth0Id uporabi syncUserWithAuth0!
        // Ta metoda bo uporabnika poiskala, če pa ga ni, ga bo USTVARILA.
        User user = userService.syncUserWithAuth0(
                auth0Id,
                email != null ? email : "neznan@email.com",
                name != null ? name : "Neznan Uporabnik"
        );

        return ResponseEntity.ok(user);
    }

    @GetMapping("/{email:.+}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email, @AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build(); // Vrne 404 namesto 500
        }

        return ResponseEntity.ok(user);
    }

    @PutMapping("/update-name")
    public User updateName(@RequestBody String newName, Principal principal) {
        return userService.updateName(principal.getName(), newName);
    }
}
