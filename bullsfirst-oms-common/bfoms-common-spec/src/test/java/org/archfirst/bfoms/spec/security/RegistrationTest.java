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

import org.archfirst.bfoms.domain.security.AuthenticationResponse;
import org.archfirst.bfoms.domain.security.Person;
import org.archfirst.bfoms.domain.security.RegistrationRequest;
import org.archfirst.bfoms.domain.security.SecurityService;
import org.archfirst.common.springtest.AbstractTransactionalSpecTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * RegistrationTest
 *
 * @author Naresh Bhatia
 */
@ContextConfiguration(locations={"classpath:/org/archfirst/bfoms/spec/applicationContext.xml"})
public class RegistrationTest extends AbstractTransactionalSpecTest {
    
    @Inject
    private SecurityService securityService;

    public void registerUser(
            String firstName,
            String lastName,
            String username,
            String password) throws Exception {
        securityService.registerUser(
                new RegistrationRequest(firstName, lastName, username, password));
    }
    
    public Result authenticateUser(
            String username,
            String password) {
        AuthenticationResponse response =
            securityService.authenticateUser(username, password);
        if (response.isSuccess()) {
            Person person = response.getUser().getPerson();
            return new Result(true, person.getFirstName(), person.getLastName());
        }
        else {
            return new Result(false, "", "");
        }
    }
    
    public class Result {
        private boolean success;
        private String firstName;
        private String lastName;

        public Result(boolean success, String firstName, String lastName) {
            this.success = success;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }
}