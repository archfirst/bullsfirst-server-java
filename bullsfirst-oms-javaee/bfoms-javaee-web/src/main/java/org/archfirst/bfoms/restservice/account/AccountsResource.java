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
package org.archfirst.bfoms.restservice.account;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.archfirst.bfcommon.restutils.ErrorMessage;
import org.archfirst.bfoms.domain.account.BaseAccountService;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;

/**
 * AccountsResource
 *
 * @author Naresh Bhatia
 */
@Stateless
@Path("/secure/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountsResource {

    @POST
    @Path("{id}/change_name")
    public Response changeAccountName(
            @PathParam("id") Long id,
            ChangeAccountNameRequest request) {
        
        baseAccountService.changeAccountName(id, request.getNewName());
        return Response.ok().build();
    }
    
    @POST
    @Path("{id}/transfer_cash")
    public Response transferCash(
            @Context SecurityContext sc,
            @PathParam("id") Long id,
            TransferCashRequest request) {
        
        try {
            baseAccountService.transferCash(
                    getUsername(sc),
                    request.getAmount(),
                    id,
                    request.getToAccountId());
        }
        catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorMessage(e.getMessage())).build();
        }

        return Response.ok().build();
    }
    
    @POST
    @Path("{id}/transfer_securities")
    public Response transferSecurities (
            @Context SecurityContext sc,
            @PathParam("id") Long id,
            TransferSecuritiesRequest request) {
        
        try {
            baseAccountService.transferSecurities(
                    getUsername(sc),
                    request.getSymbol(),
                    request.getQuantity(),
                    request.getPricePaidPerShare(),
                    id,
                    request.getToAccountId());
        }
        catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorMessage(e.getMessage())).build();
        }

        return Response.ok().build();
    }
    
    private String getUsername(SecurityContext sc) {
        return sc.getUserPrincipal().getName();
    }

    // ----- Helper Classes -----
    private static class ChangeAccountNameRequest {
        private String newName;
        public String getNewName() {
            return newName;
        }
        public void setNewName(String newName) {
            this.newName = newName;
        }
    }

    private static class TransferCashRequest {
        private Money amount;
        private Long toAccountId;
        private Money getAmount() {
            return amount;
        }
        private void setAmount(Money amount) {
            this.amount = amount;
        }
        private Long getToAccountId() {
            return toAccountId;
        }
        private void setToAccountId(Long toAccountId) {
            this.toAccountId = toAccountId;
        }
    }

    private static class TransferSecuritiesRequest {
        private String symbol;
        private DecimalQuantity quantity;
        private Money pricePaidPerShare;
        private Long toAccountId;
        private String getSymbol() {
            return symbol;
        }
        private void setSymbol(String symbol) {
            this.symbol = symbol;
        }
        private DecimalQuantity getQuantity() {
            return quantity;
        }
        private void setQuantity(DecimalQuantity quantity) {
            this.quantity = quantity;
        }
        private Money getPricePaidPerShare() {
            return pricePaidPerShare;
        }
        private void setPricePaidPerShare(Money pricePaidPerShare) {
            this.pricePaidPerShare = pricePaidPerShare;
        }
        private Long getToAccountId() {
            return toAccountId;
        }
        private void setToAccountId(Long toAccountId) {
            this.toAccountId = toAccountId;
        }
    }

    // ----- Attributes -----
    @Inject
    private BaseAccountService baseAccountService;
}