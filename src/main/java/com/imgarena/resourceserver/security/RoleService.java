package com.imgarena.resourceserver.security;


import com.imgarena.resourceserver.model.Role;
import com.imgarena.resourceserver.model.Role.RoleName;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
  public static final String ROLE_PREFIX = "ROLE_";

  private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);

  private Map<RoleName, List<SimpleGrantedAuthority>> authorities;

  @PostConstruct
  public void generateAuthorities() {
    authorities = Map.of(
        RoleName.SUPER_ADMIN,
        List.of(new SimpleGrantedAuthority(ROLE_PREFIX + Role.RoleName.ADMIN.name())),
        RoleName.OPERATOR,
        List.of(new SimpleGrantedAuthority(ROLE_PREFIX + Role.RoleName.OPERATOR.name()))
    );
  }

  public List<SimpleGrantedAuthority> getPermissionsFor(Role.RoleName role) {
    return authorities.getOrDefault(role, List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role.name())));
  }

}
