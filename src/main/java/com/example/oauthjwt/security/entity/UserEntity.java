package com.example.oauthjwt.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String oAuthName;

    private String email;

    private String role;

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeName(String name) {
        this.oAuthName = name;
    }
}