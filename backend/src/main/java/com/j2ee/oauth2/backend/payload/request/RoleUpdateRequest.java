package com.j2ee.oauth2.backend.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class RoleUpdateRequest {
    private Set<String> role;

}
