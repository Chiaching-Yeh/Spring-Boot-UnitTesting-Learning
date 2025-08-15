package com.self.demo.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class User {

    private String userId;
    private String name;
    private String email;
    private String cardId;

    public User() {
    }

    public User(String userId, String name, String email, String cardId) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.cardId = cardId;
    }

}

