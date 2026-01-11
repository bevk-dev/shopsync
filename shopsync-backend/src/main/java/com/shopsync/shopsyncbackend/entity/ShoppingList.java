package com.shopsync.shopsyncbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;
import java.util.ArrayList;

@Entity
public class ShoppingList {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({"shoppingLists", "products", "email"})
    private User owner;

    @ManyToMany
    @JoinTable(
        name = "shoppinglist_shared_users",
        joinColumns = @JoinColumn(name = "shoppinglist_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private List<User> sharedWith = new ArrayList<>();

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Item> items = new ArrayList<>();

    // Getterji in setterji
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public List<User> getSharedWith() { return sharedWith; }
    public void setSharedWith(List<User> sharedWith) { this.sharedWith = sharedWith; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}
