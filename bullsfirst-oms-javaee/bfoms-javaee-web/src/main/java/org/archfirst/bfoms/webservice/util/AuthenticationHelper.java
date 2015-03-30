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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.archfirst.bfoms.domain.security.PasswordHashGenerator;

/**
 * AuthenticationHelper
 *
 * @author Naresh Bhatia
 */
public class AuthenticationHelper {
    
    private static String CONNECTION_POOL_JNDI_NAME =
        "bfoms_javaee_connection_pool";
    
    private static String READ_PASSWORD =
        "select passwordHash from Users where username = ?";
    
    private static String DB_ERROR = "Error accessing database";

    public static final boolean authenticate(String username, String password)
            throws AuthenticationException {

        String passwordHash = PasswordHashGenerator.generateHash(password);

        // Get the password hash stored in the database
        String passwordHashDb = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            Context context = new InitialContext();
            DataSource datasource =
                (DataSource)context.lookup(CONNECTION_POOL_JNDI_NAME);
            connection = datasource.getConnection();
            statement = connection.prepareStatement(READ_PASSWORD);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            // ResultSet should have 0 or 1 record
            while(resultSet.next()) {
                passwordHashDb = resultSet.getString("passwordHash");
            }
        }
        catch (NamingException e) {
            throw new AuthenticationException(DB_ERROR);
        }
        catch (SQLException e) {
            throw new AuthenticationException(DB_ERROR);
        }
        finally {
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {}
            if (statement != null) try { statement.close(); } catch (SQLException e) {}
            if (connection != null) try { connection.close(); } catch (SQLException e) {}
        }
        
        return passwordHash.equals(passwordHashDb);
    }
}