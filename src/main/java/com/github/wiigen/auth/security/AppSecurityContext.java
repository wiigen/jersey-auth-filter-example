package com.github.wiigen.auth.security;

import java.security.Principal;
import java.util.List;

import javax.ws.rs.core.SecurityContext;

import com.github.wiigen.auth.domain.AdUser;

public class AppSecurityContext implements SecurityContext {

    private final String username;
    private final List<String> roles;

    public AppSecurityContext(AdUser adUser) {
        this.username = adUser.getUsername();
        this.roles = adUser.getRoles();
    }

    public Principal getUserPrincipal() {
        return new Principal() {
            public String getName() {
                return username;
            }
        };
    }

    public boolean isUserInRole(String role) {
        return roles.contains(role);
    }

    public boolean isSecure() {
        return false;
    }

    public String getAuthenticationScheme() {
        return "Bearer";
    }

}
