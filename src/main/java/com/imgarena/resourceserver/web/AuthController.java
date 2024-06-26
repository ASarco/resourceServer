package com.imgarena.resourceserver.web;

import static com.imgarena.resourceserver.config.ApiConstants.API_ENDPOINT_PREFIX;
import static com.imgarena.resourceserver.config.ApiConstants.ROLE_PREFIX;
import static org.keycloak.representations.IDToken.EMAIL;
import static org.keycloak.representations.IDToken.FAMILY_NAME;
import static org.keycloak.representations.IDToken.GIVEN_NAME;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.imgarena.resourceserver.model.Role.RoleName;
import com.imgarena.resourceserver.model.UserInfoDTO;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

  public static final String ANONYMOUS_USER = "anonymousUser";

  @GetMapping(value = API_ENDPOINT_PREFIX + "/admin")
  @Secured({"ROLE_SUPER_ADMIN"})
  public UserInfoDTO getUserForSuperAdmin() {
    return getUserData();
  }

  @GetMapping(value = API_ENDPOINT_PREFIX + "/operator")
  @Secured({"ROLE_OPERATOR", "ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
  public UserInfoDTO getUserForOperator() {
    return getUserData();
  }

  @GetMapping("/userInfo")
  public UserInfoDTO getUserInfo() {
    return getUserData();
  }


  private UserInfoDTO getUserData() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth.getPrincipal().equals(ANONYMOUS_USER)) {
      throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
    }
    var roles =
        auth.getAuthorities().stream()
            .map(String::valueOf)
            .map(role -> role.replace(ROLE_PREFIX, ""))
            // Don't do this, bad performance
            .filter(role -> Arrays.stream(RoleName.values()).map(String::valueOf).anyMatch(
                role::equals))
            .map(RoleName::valueOf)
            .collect(Collectors.toSet());
    var jwt = (Jwt) auth.getPrincipal();
    return new UserInfoDTO(
        UUID.fromString(jwt.getId()),
        jwt.getClaim(GIVEN_NAME),
        jwt.getClaim(FAMILY_NAME),
        jwt.getClaim(EMAIL),
        roles,
        ZonedDateTime.ofInstant(Objects.requireNonNullElse(jwt.getExpiresAt(), Instant.EPOCH), ZoneId.systemDefault()));
}
}
