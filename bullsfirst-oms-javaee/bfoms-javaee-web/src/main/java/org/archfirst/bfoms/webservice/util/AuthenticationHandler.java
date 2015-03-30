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
package org.archfirst.bfoms.webservice.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * Authenticates incoming soap messages by picking up the username and password
 * http headers and verifying against the credentials stored in the database.
 * This handler is hooked into TradingWebService by specifying a handler chain.
 * The handler-chain file is located in the resources folder.
 *
 * @author Naresh Bhatia
 */
public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static String HEADERS_NOT_FOUND_ERROR = "Authentication headers not found";
    private static String AUTHENTICATION_FAILED_ERROR = "Authentication failed";

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        
        // Don't touch outgoing messages
        if ((Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))
            return true;

        // Process incoming message
        @SuppressWarnings("unchecked")
        Map<String, List<String>> http_headers =
            (Map<String, List<String>>)context.get(MessageContext.HTTP_REQUEST_HEADERS);
        List<String> usernameList = http_headers.get(USERNAME_KEY);
        List<String> passwordList = http_headers.get(PASSWORD_KEY);
        
        if (usernameList == null ||
            usernameList.size() != 1 ||
            passwordList == null ||
            passwordList.size() != 1) {
            throw new AuthenticationException(HEADERS_NOT_FOUND_ERROR);
        }

        if (AuthenticationHelper.authenticate(usernameList.get(0), passwordList.get(0)) == false)
            throw new AuthenticationException(AUTHENTICATION_FAILED_ERROR);
        
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }
}