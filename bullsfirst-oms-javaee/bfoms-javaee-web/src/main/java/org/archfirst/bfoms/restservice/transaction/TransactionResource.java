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
package org.archfirst.bfoms.restservice.transaction;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.archfirst.bfoms.domain.account.TransactionCriteria;
import org.archfirst.bfoms.domain.account.TransactionSummaryV2;
import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountService;
import org.joda.time.LocalDate;

/**
 * TransactionResource
 *
 * @author Naresh Bhatia
 */
@Stateless
@Path("/secure/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {
    @GET
    public List<TransactionSummaryV2> getTransactionSummaries(
            @Context SecurityContext sc,
            @QueryParam("accountId") Long accountId,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate) {
        
        // Build the transaction criteria
        TransactionCriteria criteria = new TransactionCriteria();
        criteria.setAccountId(accountId);
        if (fromDate != null) {
            criteria.setFromDate(new LocalDate(fromDate));
        }
        if (toDate != null) {
            criteria.setToDate(new LocalDate(toDate));
        }
        
        // Get transactions matching the criteria
        return this.brokerageAccountService.getTransactionSummaries(getUsername(sc), criteria);
    }
    
    private String getUsername(SecurityContext sc) {
        return sc.getUserPrincipal().getName();
    }

    // ----- Attributes -----
    @Inject
    private BrokerageAccountService brokerageAccountService;
}