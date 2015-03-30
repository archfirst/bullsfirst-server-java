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
package org.archfirst.bfexch.restservice.instrument;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.archfirst.bfexch.domain.referencedata.Instrument;
import org.archfirst.bfexch.domain.referencedata.ReferenceDataService;

/**
 * InstrumentResource
 *
 * @author Naresh Bhatia
 */
@Stateless
@Path("/instruments")
@Produces(MediaType.APPLICATION_JSON)
public class InstrumentResource {

    @GET
    public List<Instrument> getInstruments() {
        return referenceDataService.getInstruments();
    }
    
    @GET
    @Path("{symbol}")
    public Instrument getInstrument(
            @PathParam("symbol") String symbol) {
        return referenceDataService.lookup(symbol);
    }
    
    // ----- Attributes -----
    @Inject
    private ReferenceDataService referenceDataService;
}