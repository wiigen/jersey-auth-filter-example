package com.github.wiigen.auth;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.github.wiigen.auth.resource.CustomerResource;
import com.github.wiigen.auth.security.AuthorizationFilter;
import com.github.wiigen.auth.service.AdService;
import com.github.wiigen.auth.service.CustomerService;
import com.github.wiigen.auth.service.TokenService;

public class AppResourceConfig extends ResourceConfig {

    public AppResourceConfig() {
        register(CustomerResource.class);
        register(AuthorizationFilter.class);
        register(JacksonFeature.class);
        register(RolesAllowedDynamicFeature.class);

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                    bindAsContract(TokenService.class);
                    bindAsContract(AdService.class);
                    bindAsContract(CustomerService.class);
            }
        });
    }

}
