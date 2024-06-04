package com.imgarena.resourceserver.web;

import static com.imgarena.resourceserver.config.ApiConstants.API_ENDPOINT_PREFIX;
import static org.keycloak.representations.IDToken.EMAIL;
import static org.keycloak.representations.IDToken.PREFERRED_USERNAME;

import java.util.stream.Collectors;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_ENDPOINT_PREFIX)
public class AuthController {

  private static final String USER_DATA = "User: %s <%s>, roles: %s, expires: %tc  (%s)";

  @GetMapping(value = "/admin")
  @Secured({"ROLE_SUPER_ADMIN"})
  public String getUserForSuperAdmin() {
    return "<H1>SUPER_ADMIN</H1>" + getUserData();
  }

  @GetMapping(value = "/operator")
  @Secured({"ROLE_OPERATOR", "ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
  public String getUserForOperator() {
    return "<H1>OPERATOR</H1>" + getUserData();
  }

  private String getUserData() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    var roles =
        auth.getAuthorities().stream().map(Object::toString).collect(Collectors.joining(","));
    var jwt = (Jwt) auth.getPrincipal();
    return USER_DATA.formatted(
        jwt.getClaim(PREFERRED_USERNAME),
        jwt.getClaim(EMAIL),
        roles,
        jwt.getExpiresAt().getEpochSecond() * 1000L,
        auth.getName());
  }

}
