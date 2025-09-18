package com.rezende.user_service.entities;

import com.rezende.user_service.enums.AccountStatus;
import com.rezende.user_service.enums.RoleType;
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
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    private User(
            final UUID id,
            final String firstName,
            final String lastName,
            final String email,
            final String password,
            final RoleType roleType,
            final AccountStatus accountStatus
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roleType = roleType;
        this.accountStatus = accountStatus;
    }

    public static User from(
            final UUID id,
            final String firstName,
            final String lastName,
            final String email,
            final String password,
            final RoleType roleType,
            final AccountStatus accountStatus
    ) {
        return new User(id, firstName, lastName, email, password, roleType, accountStatus);
    }
}
