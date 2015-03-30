/**
 * Copyright 2010 Archfirst
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
package org.archfirst.bfoms.domain.account.brokerage;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.archfirst.common.quantity.DecimalQuantity;

/**
 * TradeAllocation
 *
 * @author Naresh Bhatia
 */
@Entity
public class TradeAllocation extends Allocation {
    private static final long serialVersionUID = 1L;

    // ----- Constructors -----
    public TradeAllocation() {
    }

    public TradeAllocation(DecimalQuantity quantity, Trade trade) {
        super(quantity);
        this.trade = trade;
    }

    // ----- Attributes -----
    private Trade trade;

    // ----- Getters and Setters -----
    @ManyToOne
    public Trade getTrade() {
        return trade;
    }
    // Allow access to Trade
    void setTrade(Trade trade) {
        this.trade = trade;
    }
}