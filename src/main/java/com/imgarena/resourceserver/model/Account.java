package com.imgarena.resourceserver.model;

import com.imgarena.resourceserver.model.Role.RoleName;

public record Account(
    String id,
    String username,
    String password,
    RoleName role ) {

}
