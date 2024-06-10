package com.imgarena.resourceserver.model;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

public record UserInfoDTO(
    UUID id,
    String name,
    String surname,
    String email,
    Set<Role.RoleName> roles,
    ZonedDateTime expiration) {}
