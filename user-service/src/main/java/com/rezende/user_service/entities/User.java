package com.rezende.user_service.entities;

import com.rezende.user_service.entities.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "{name.not.blank}")
    @Size(min = 2, message = "{name.too.short}")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "{email.not.blank}")
    @Email(message = "{email.not.valid}")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "{password.too.short}")
    @Size(min = 8, message = "{password.too.short}")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "{role.not.null}")
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

    public User from(
            final String name,
            final String email,
            final String password,
            final RoleType roleType
    ) {
        return new User(name, email, password, roleType);
    }

}
