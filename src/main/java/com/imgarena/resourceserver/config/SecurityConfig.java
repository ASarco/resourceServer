package com.imgarena.resourceserver.config;

import com.imgarena.resourceserver.security.RoleService;
import com.imgarena.resourceserver.security.SimpleAccessDeniedHandler;
import com.imgarena.resourceserver.security.UsersAuthenticationProviderImpl;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = false)
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.cors(cors -> cors.configure(http))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .securityMatchers(sm -> sm.requestMatchers("/**"))
        .authorizeHttpRequests(ar -> ar.anyRequest().authenticated())
        .oauth2ResourceServer(
            o2 ->
                o2.jwt(
                    jwtConfigurer ->
                        jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .build();
  }

  private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
    return jwtAuthenticationConverter;
  }

  /*  private CompositeFilter tokenFilters(TokenService tokenService, IPListService ipListService)
      throws Exception {
    var compositeFilter = new CompositeFilter();
    compositeFilter.setFilters(
        List.of(
            new APITokenProcessingFilter(authenticationManager(), tokenService, ipListService),
            new AppsTokenProcessingFilter(authenticationManager(), tokenService)));
    return compositeFilter;
  }*/

  @Bean
  public AuthenticationManager authManager(HttpSecurity http, RoleService roleService)
      throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.authenticationProvider(
        new UsersAuthenticationProviderImpl(roleService));
    //authenticationManagerBuilder.authenticationProvider(new AppsAuthenticationProviderImpl());
    return authenticationManagerBuilder.build();
  }

  /*
    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
      return new AnnotationsSecurityMetadataSource();
    }

  */

  private class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
      if (jwt.getClaims() == null) {
        return List.of();
      }
      final Map<String, List<String>> realmAccess =
          (Map<String, List<String>>) jwt.getClaims().get("realm_access");
      return realmAccess.get("roles").stream()
          .map(role -> "ROLE_" + role)
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());
    }

    @Override
    public <U> Converter<Jwt, U> andThen(
        Converter<? super Collection<GrantedAuthority>, ? extends U> after) {
      return Converter.super.andThen(after);
    }

  }
}
