package com.github.wiigen.auth.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.wiigen.auth.domain.AdUser;
import com.github.wiigen.auth.domain.Customer;
import com.github.wiigen.auth.security.AuthorizationFilter;
import com.github.wiigen.auth.service.AdService;
import com.github.wiigen.auth.service.CustomerService;
import com.github.wiigen.auth.service.TokenService;

/**
 * Integration test
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerResourceTest extends JerseyTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private AdService adService;

    @Mock
    private CustomerService customerService;

    @Override
    protected Application configure() {
        return getTestResourceConfig();
    }

    @Test
    public void shouldReturnUnauthorizedWhenHeaderIsMissingOrBlank() {
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), target(CustomerResource.PATH)
                .path("1")
                .request()
                .get().getStatus());

        assertEquals(Status.UNAUTHORIZED.getStatusCode(), target(CustomerResource.PATH)
                .path("1")
                .request()
                .header(HttpHeaders.AUTHORIZATION, " ")
                .get().getStatus());
    }

    @Test
    public void shouldReturnUnauthorizedWhenTokenNotOk() {
        when(tokenService.getUsernameFromToken("not-ok-token")).thenReturn(null);

        Response response = target(CustomerResource.PATH)
                .path("1")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "not-ok-token")
                .get();

        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void shouldGetCustomer() {
        Customer expected = Customer.builder()
                .name("Customer Name")
                .build();

        when(tokenService.getUsernameFromToken("valid-token")).thenReturn("valid-username");
        when(adService.getUser("valid-username")).thenReturn(adUserWithOnlyUserRole());
        when(customerService.getCustomer(1)).thenReturn(expected);

        Response response = target(CustomerResource.PATH)
                .path("1")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "valid-token")
                .get();

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(expected, response.readEntity(Customer.class));
    }

    @Test
    public void shouldReturnForbiddenWhenNormalUserTriesToDeleteCustomer() {
        when(tokenService.getUsernameFromToken("valid-token")).thenReturn("valid-username");
        when(adService.getUser("valid-username")).thenReturn(adUserWithOnlyUserRole());

        Response response = target(CustomerResource.PATH)
                .path("1")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "valid-token")
                .delete();

        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void adminUserShouldBeAbleToDeleteCustomer() {
        when(tokenService.getUsernameFromToken("valid-token")).thenReturn("valid-username");
        when(adService.getUser("valid-username")).thenReturn(adUserWithAdminRole());

        Response response = target(CustomerResource.PATH)
                .path("1")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "valid-token")
                .delete();

        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    private Application getTestResourceConfig() {
        ResourceConfig config = new ResourceConfig();
        config.register(CustomerResource.class);
        config.register(AuthorizationFilter.class);
        config.register(JacksonFeature.class);
        config.register(RolesAllowedDynamicFeature.class);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(tokenService).to(TokenService.class);
                bind(adService).to(AdService.class);
                bind(customerService).to(CustomerService.class);
            }
        });
        return config;
    }

    private AdUser adUserWithOnlyUserRole() {
        return AdUser.builder()
                .username("valid-username")
                .firstName("John")
                .lastName("Doe")
                .role("USER")
                .build();
    }

    private AdUser adUserWithAdminRole() {
        return AdUser.builder()
                .username("valid-username")
                .firstName("John")
                .lastName("Doe")
                .role("ADMIN")
                .build();
    }

}
