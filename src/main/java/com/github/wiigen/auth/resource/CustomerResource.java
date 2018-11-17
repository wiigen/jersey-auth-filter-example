package com.github.wiigen.auth.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.wiigen.auth.domain.Customer;
import com.github.wiigen.auth.service.CustomerService;

@Path(CustomerResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {
    static final String PATH = "customers";

    @Inject
    private CustomerService customerService;

    @GET
    @Path("{customerId}")
    @RolesAllowed({"USER", "ADMIN"})
    public Customer getCustomer(@PathParam("customerId") int customerId) {
        return customerService.getCustomer(customerId);
    }

    @DELETE
    @Path("{customerId}")
    @RolesAllowed({"ADMIN"})
    public void deleteCustomer(@PathParam("customerId") int customerId) {
        customerService.deleteCustomer(customerId);
    }

}
