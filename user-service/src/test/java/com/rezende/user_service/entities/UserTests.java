package com.rezende.user_service.entities;

import com.rezende.user_service.enums.AccountStatus;
import com.rezende.user_service.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTests {

    @Test
    @DisplayName("Deve criar um usuário corretamente usando o factory method 'from'")
    void shouldCreateUserUsingFrom() {
        final UUID id = UUID.randomUUID();
        final String name = "Fulano";
        final String email = "fulano@email.com";
        final String password = "senha123";
        final RoleType roleType = RoleType.CUSTOMER;
        final AccountStatus status = AccountStatus.ACTIVE;

        final User user = User.from(id, name, email, password, roleType, status);

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(roleType, user.getRoleType());
        assertEquals(status, user.getAccountStatus());
    }

    @Test
    @DisplayName("Deve validar o equals e hashCode da entidade User")
    void shouldValidateEqualsAndHashCode() {
        final UUID id = UUID.randomUUID();
        final User user1 = User.from(id, "Fulano", "fulano@email.com", "senha123", RoleType.CUSTOMER, AccountStatus.ACTIVE);
        final User user2 = User.from(id, "Fulano", "fulano@email.com", "senha123", RoleType.CUSTOMER, AccountStatus.ACTIVE);
        final User user3 = User.from(UUID.randomUUID(), "Outro", "outro@email.com", "senha456", RoleType.DRIVER, AccountStatus.DEACTIVATED);

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);

        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    @DisplayName("Deve criar múltiplos usuários com IDs diferentes")
    void shouldCreateMultipleUsersWithDifferentIds() {
        final User user1 = User.from(UUID.randomUUID(), "Fulano", "fulano@email.com", "senha123", RoleType.CUSTOMER, AccountStatus.ACTIVE);
        final User user2 = User.from(UUID.randomUUID(), "Beltrano", "beltrano@email.com", "senha456", RoleType.DRIVER, AccountStatus.DEACTIVATED);

        assertNotEquals(user1.getId(), user2.getId());
        assertNotEquals(user1.getEmail(), user2.getEmail());
    }
}
