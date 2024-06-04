package com.imgarena.resourceserver.model;

public record Role(Integer id, RoleName name) {


  public enum RoleName {
    PUBLIC,
    ENGINEER,
    TRADER,
    OPERATOR,
    IMG,
    ADMIN,
    SUPER_ADMIN,
    ENCODING_ENGINEER,
    UXQA,
    CONTENT_OPERATIONS,
    ARENA,
    DICE,
    SPOC,
    CH,
    L1_SUPPORT
  }
}
