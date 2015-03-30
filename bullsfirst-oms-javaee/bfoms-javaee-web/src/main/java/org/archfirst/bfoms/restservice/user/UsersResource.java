/**
 * Copyright 2011 Archfirst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archfirst.bfoms.restservice.user;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.archfirst.bfcommon.restutils.ErrorMessage;
import org.archfirst.bfcommon.restutils.Link;
import org.archfirst.bfoms.domain.security.AuthenticationResponse;
import org.archfirst.bfoms.domain.security.RegistrationRequest;
import org.archfirst.bfoms.domain.security.SecurityService;
import org.archfirst.bfoms.domain.security.UsernameExistsException;
import org.dozer.Mapper;

/**
 * UsersResource
 *
 * @author Naresh Bhatia
 */
@Stateless
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

    @POST
    public Response createUser(RegistrationRequest request) {
        
        // Create user
        try {
            securityService.registerUser(request);
        }
        catch (UsernameExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorMessage(e.getMessage())).build();
        }
        catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorMessage(e.getMessage())).build();
        }

        // Return link to self
        Link self = new Link(uriInfo, request.getUsername());
        return Response.created(self.getUri()).entity(self).build();
    }

    @GET
    @Path("{username}")
    public User getUser(
            @PathParam("username") String username,
            @Context HttpServletRequest request) {
        
        String password = request.getHeader("password");
        if (password == null || password.isEmpty() ) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        // Authenticate the user before returning any information
        AuthenticationResponse response =
            securityService.authenticateUser(username, password);
        
        // Don't return user information if authentication failed
        if (!response.isSuccess()) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        return mapper.map(response.getUser(), User.class);
    }
    
    // ----- Attributes -----
    @Context
    private UriInfo uriInfo;

    @Inject
    private SecurityService securityService;
    
    @Inject
    private Mapper mapper;
}