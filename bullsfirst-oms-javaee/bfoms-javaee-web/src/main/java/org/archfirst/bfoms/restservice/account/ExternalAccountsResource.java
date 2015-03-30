/**
 * Copyright 2012 Archfirst
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
package org.archfirst.bfoms.restservice.account;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.archfirst.bfcommon.restutils.Link;
import org.archfirst.bfoms.domain.account.external.ExternalAccountParams;
import org.archfirst.bfoms.domain.account.external.ExternalAccountService;
import org.archfirst.bfoms.domain.account.external.ExternalAccountSummary;

/**
 * ExternalAccountsResource
 *
 * @author Naresh Bhatia
 */
@Stateless
@Path("/secure/external_accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExternalAccountsResource {

    @POST
    public Response createExternalAccount(
            @Context SecurityContext sc,
            CreateExternalAccountRequest request) {
        
        Long id = this.externalAccountService.addExternalAccount(
                getUsername(sc),
                new ExternalAccountParams(
                        request.getName(),
                        request.getRoutingNumber(),
                        request.getAccountNumber()));
        
        // Return link to self
        Link self = new Link(uriInfo, Long.toString(id));
        return Response.created(self.getUri()).entity(self).build();
    }
    
    private String getUsername(SecurityContext sc) {
        return sc.getUserPrincipal().getName();
    }

    @GET
    public List<ExternalAccountSummary> getExternalAccountSummaries(
            @Context SecurityContext sc) {
        return this.externalAccountService.getAccountSummaries(getUsername(sc));
    }
    
    // ----- Helper Classes -----
    private static class CreateExternalAccountRequest {
        private String name;
        private String routingNumber;
        private String accountNumber;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        private String getRoutingNumber() {
            return routingNumber;
        }
        private void setRoutingNumber(String routingNumber) {
            this.routingNumber = routingNumber;
        }
        private String getAccountNumber() {
            return accountNumber;
        }
        private void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }
    }

    // ----- Attributes -----
    @Context
    private UriInfo uriInfo;

    @Inject
    private ExternalAccountService externalAccountService;
}