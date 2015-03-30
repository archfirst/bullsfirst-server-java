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
package org.archfirst.bfoms.webservice.security;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.archfirst.bfoms.domain.security.RegistrationRequest;
import org.archfirst.bfoms.domain.security.SecurityService;
import org.archfirst.bfoms.domain.security.UsernameExistsException;
import org.dozer.Mapper;

/**
 * SecurityTxnService
 *
 * @author Naresh Bhatia
 */
@Stateless
public class SecurityTxnService {

    @Inject
    private SecurityService securityService;
    
    @Inject
    private Mapper mapper;
    
    // ----- Commands -----
    public void registerUser(
            RegistrationRequest request)
    throws UsernameExistsException {
        securityService.registerUser(request);
    }
    
    // ----- Queries -----
    public AuthenticationResponse authenticateUser(
            String username,
            String password) {
        return mapper.map(
                securityService.authenticateUser(username, password),
                AuthenticationResponse.class);
    }
}