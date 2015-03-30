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

import com.sun.xml.wss.impl.callback.PasswordValidationCallback;
import com.sun.xml.wss.impl.callback.PasswordValidationCallback.PasswordValidationException;
import com.sun.xml.wss.impl.callback.PasswordValidationCallback.Request;

/**
 * Validates passwords when WS-Security is turned on. This is done by specifying
 * UsernameToken in the Web Service configuration file. This feature is currently
 * not used because Silverlight clients cannot do WS-Security over HTTP.
 *
 * @author Naresh Bhatia
 */
public class PasswordValidator implements PasswordValidationCallback.PasswordValidator
{
    @Override
    public boolean validate(Request request) throws PasswordValidationException {

        // Get the username and password supplied by the user
        PasswordValidationCallback.PlainTextPasswordRequest passwordRequest
            = (PasswordValidationCallback.PlainTextPasswordRequest)request;
        String username = passwordRequest.getUsername();
        String password = passwordRequest.getPassword();

        try {
            return AuthenticationHelper.authenticate(username, password);
        }
        catch (AuthenticationException e) {
            throw new PasswordValidationException(e.getMessage());
        }
    }
}