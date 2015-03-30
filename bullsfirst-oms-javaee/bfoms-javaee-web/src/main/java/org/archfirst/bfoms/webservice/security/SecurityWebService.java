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

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.archfirst.bfoms.domain.security.RegistrationRequest;
import org.archfirst.bfoms.domain.security.UsernameExistsException;

/**
 * SecurityWebService
 *
 * @author Naresh Bhatia
 */
@WebService(targetNamespace = "http://archfirst.org/bfoms/securityservice.wsdl", serviceName = "SecurityService")
public class SecurityWebService {

    @Inject
    private SecurityTxnService securityTxnService;
    
    // ----- Commands -----
    @WebMethod(operationName = "RegisterUser", action = "RegisterUser")
    public void registerUser(
            @WebParam(name = "RegistrationRequest")
            RegistrationRequest request)
    throws UsernameExistsException {
        securityTxnService.registerUser(request);
    }
    
    // ----- Queries -----
    @WebMethod(operationName = "AuthenticateUser", action = "AuthenticateUser")
    @WebResult(name = "AuthenticationResponse")
    public AuthenticationResponse authenticateUser(
            @WebParam(name = "Username")
            String username,
            @WebParam(name = "Password")
            String password) {
        return securityTxnService.authenticateUser(username, password);
    }
}