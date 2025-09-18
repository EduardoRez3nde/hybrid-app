package com.rezende.user_service.dto;

import com.rezende.user_service.enums.RoleType;

public interface RegisterUser {

    String getFirstName();
    String getLastName();
    String getEmail();
    String getPassword();
    RoleType getRoleType();
}
