package com.github.wiigen.auth.security;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import com.github.wiigen.auth.domain.AdUser;
import com.github.wiigen.auth.service.AdService;
import com.github.wiigen.auth.service.TokenService;

@PreMatching
public class AuthorizationFilter implements ContainerRequestFilter {

    @Inject
    private TokenService tokenService;

    @Inject
    private AdService adService;

    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationToken = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (isEmpty(authorizationToken)) {
            throw new WebApplicationException("Authorization header is missing", Status.UNAUTHORIZED);
        }

        String username = tokenService.getUsernameFromToken(authorizationToken);
        if (isEmpty(username)) {
            throw new WebApplicationException("Authentication failed", Status.UNAUTHORIZED);
        }

        AdUser user = adService.getUser(username);

        requestContext.setSecurityContext(new AppSecurityContext(user));
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}
