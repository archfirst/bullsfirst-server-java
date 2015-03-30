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
package org.archfirst.bfoms.spec.security;

import javax.inject.Inject;

import org.archfirst.bfoms.domain.security.RegistrationRequest;
import org.archfirst.bfoms.domain.security.SecurityService;
import org.archfirst.bfoms.domain.security.UsernameExistsException;
import org.archfirst.common.springtest.AbstractTransactionalSpecTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * UniqueUsernameTest
 *
 * @author Naresh Bhatia
 */
@ContextConfiguration(locations={"classpath:/org/archfirst/bfoms/spec/applicationContext.xml"})
public class UniqueUsernameTest extends AbstractTransactionalSpecTest {
    
    @Inject
    private SecurityService securityService;

    public String registerUser(String username) {
        String result = "succeed";
        try {
            securityService.registerUser(
                    new RegistrationRequest("John", "Doe", username, "secret"));
        }
        catch (UsernameExistsException e) {
            result = "fail";
        }
        return result;
    }
}