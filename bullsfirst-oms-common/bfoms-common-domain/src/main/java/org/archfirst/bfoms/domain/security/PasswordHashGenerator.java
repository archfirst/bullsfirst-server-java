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

import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

/**
 * Password hashing utility adapted from
 *     org.jboss.seam.security.management.PasswordHash
 *
 * @author Naresh
 */
public class PasswordHashGenerator {
    public static final String ALGORITHM_MD5 = "MD5";
    public static final String ALGORITHM_SHA = "SHA";
       
    private static final String DEFAULT_ALGORITHM = ALGORITHM_MD5;
    
    public static final String generateHash(String password) {
        return generateHash(password, DEFAULT_ALGORITHM);
    }
    
    public static final String generateHash(String password, String algorithm) {
        return generateSaltedHash(password, null, algorithm);
    }
    
    public static final String generateSaltedHash(String password, String saltPhrase) {
        return generateSaltedHash(password, saltPhrase, DEFAULT_ALGORITHM);
    }
    
    public static final String generateSaltedHash(String password, String saltPhrase, String algorithm) {
        try {        
            MessageDigest md = MessageDigest.getInstance(algorithm);
                   
            if (saltPhrase != null) {
                md.update(saltPhrase.getBytes());
                byte[] salt = md.digest();
             
                md.reset();
                md.update(password.getBytes());
                md.update(salt);
            }
            else {
                md.update(password.getBytes());
            }
          
            byte[] raw = md.digest();
            return DatatypeConverter.printBase64Binary(raw);
        } 
        catch (Exception e) {
            throw new RuntimeException(e);        
        } 
    }
}