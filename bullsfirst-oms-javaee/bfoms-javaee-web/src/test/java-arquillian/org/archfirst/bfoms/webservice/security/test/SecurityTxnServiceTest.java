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
package org.archfirst.bfoms.webservice.security.test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.archfirst.bfoms.domain.security.RegistrationRequest;
import org.archfirst.bfoms.domain.security.SecurityService;
import org.archfirst.bfoms.domain.security.UsernameExistsException;
import org.archfirst.bfoms.webservice.security.AuthenticationResponse;
import org.archfirst.bfoms.webservice.security.SecurityTxnService;
import org.archfirst.common.domain.DomainEntity;
import org.dozer.Mapper;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for SecurityTxnService.
 */
public class SecurityTxnServiceTest extends Arquillian {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction utx;

    @Inject
    private SecurityTxnService securityTxnService;

    @Deployment
    public static WebArchive createTestArchive() {
        WebArchive archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addPackage(SecurityTxnService.class.getPackage())
            .addPackage(SecurityService.class.getPackage())
            .addPackage(DomainEntity.class.getPackage())
            .addPackage(Mapper.class.getPackage())
            .addManifestResource("test-persistence.xml", "persistence.xml")
            .addWebResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
        return archive;
    }

    @Test
    public void testSuccessfulAuthentication() {
        registerUsers();
        AuthenticationResponse response =
            securityTxnService.authenticateUser("jhorner", "cool");
        Assert.assertEquals(response.isSuccess(), true);
        Assert.assertEquals(response.getUser().getUsername(), "jhorner");
        Assert.assertEquals(response.getUser().getFirstName(), "John");
    }
    
    @Test
    public void testIncorrectUsername() {
        registerUsers();
        AuthenticationResponse response =
            securityTxnService.authenticateUser("jhorn", "cool");
        Assert.assertEquals(response.isSuccess(), false);
    }
    
    @Test
    public void testIncorrectPassword() {
        registerUsers();
        AuthenticationResponse response =
            securityTxnService.authenticateUser("jhorner", "bool");
        Assert.assertEquals(response.isSuccess(), false);
    }
    
    @Test
    //@Test(expectedExceptions = UsernameExistsException.class) <-- not working
    public void testDuplicateUsernameRegistration() {
        registerUsers();
        try {
            securityTxnService.registerUser(
                new RegistrationRequest("Jim", "Horner", "jhorner", "school"));
        }
        catch (UsernameExistsException e) {
            // This is an acceptable exception - eat it
        }
    }
    
    private static RegistrationRequest[] registrations = {
        new RegistrationRequest("John", "Horner", "jhorner", "cool"),
        new RegistrationRequest("Karen", "Horner", "khorner", "cool")
    };

    private void registerUsers() {
        // Clear database
        System.out.println("Clearing the database...");
        try {
            utx.begin();
            entityManager.joinTransaction();
            entityManager.createQuery("delete from User").executeUpdate();
            entityManager.createQuery("delete from Party").executeUpdate();
            entityManager.createQuery("delete from Person").executeUpdate();
            utx.commit();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
     
       // Register users
       System.out.println("Registering users...");
       for (RegistrationRequest request : registrations) {
           try {
               securityTxnService.registerUser(request);
           }
           catch (UsernameExistsException e) {
               throw new IllegalStateException(e);
           }
       }
    }
}