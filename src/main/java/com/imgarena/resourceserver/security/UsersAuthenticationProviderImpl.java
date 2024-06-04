package com.imgarena.resourceserver.security;

import com.imgarena.resourceserver.model.Account;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UsersAuthenticationProviderImpl implements AuthenticationProvider {
  private static final Logger LOG = LoggerFactory.getLogger(UsersAuthenticationProviderImpl.class);

  private final RoleService roleService;

  public UsersAuthenticationProviderImpl(RoleService roleService) {
    this.roleService = roleService;
  }

  @Override
  public Authentication authenticate(final Authentication authentication) {
    final Account account = (Account) authentication.getPrincipal();
    List<SimpleGrantedAuthority> authorities = roleService.getPermissionsFor(account.role());

    LOG.debug("Authenticate user id={}", account.id());
    return new UsernamePasswordAuthenticationToken(account, account.password(), authorities);
  }

  @Override
  public boolean supports(final Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
