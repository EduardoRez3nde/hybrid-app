package com.rezende.user_service.entities;

import com.rezende.user_service.entities.enums.RoleType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private RoleType roleType;

    private User(
            final String name,
            final String email,
            final String password,
            final RoleType roleType
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleType = roleType;
    }

    private User from(
            final String name,
            final String email,
            final String password,
            final RoleType roleType
    ) {
        return new User(name, email, password, roleType);
    }

}
