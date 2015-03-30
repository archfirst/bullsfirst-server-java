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
package org.archfirst.bfoms.domain.security;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SecurityService
 *
 * @author Naresh Bhatia
 */
public class SecurityService {
    private static final Logger logger =
        LoggerFactory.getLogger(SecurityService.class);
    private static final String GROUP_USER = "user";

    // ----- Commands -----
    public void registerUser(RegistrationRequest request) throws UsernameExistsException {
        // Check if username already exists
        // This is not a foolproof check - someone else can get
        // the username before this user
        User existingUser = userRepository.findUser(request.getUsername());
        if (existingUser != null) {
            logger.warn("Username {} already exists", existingUser.getUsername());
            throw new UsernameExistsException(existingUser.getUsername());
        }

        // Create the user and persist it
        Person person = new Person(
                request.getFirstName(), request.getLastName());
        User user = new User(request.getUsername(), request.getPassword(), person);
        UserGroup userGroup = new UserGroup(request.getUsername(), GROUP_USER);
        logger.info("Creating user {}", user.getUsername());
        userRepository.persist(user);      
        userRepository.persist(userGroup);      
        logger.info("Created user {} with id = {}", user.getUsername(), user.getId());
    }
    
    // ----- Queries -----
    public User getUser(String username) {
        return userRepository.findUser(username);
    }

    public AuthenticationResponse authenticateUser(String username, String password) {
        // Get the user from the database
        User user = userRepository.findUser(username);
        if (user == null) {
            return new AuthenticationResponse(false, null);
        }
        
        // Validate password
        if (user.isPasswordValid(password)) {
            return new AuthenticationResponse(true, user);
        }
        else {
            return new AuthenticationResponse(false, null);
        }
    }

    // ----- Attributes -----
    @Inject
    private UserRepository userRepository;
}