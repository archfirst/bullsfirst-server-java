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
package org.archfirst.bfoms.web.home;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * HomePresentationModel
 *
 * @author Naresh Bhatia
 */
@Named("homePm")
@RequestScoped
public class HomePresentationModel {
    private String username;
    private String password;
    
    // ----- Commands -----
    public String login() {
        if (username.equals("john") && password.equals("doe")) {
            return "loggedIn";
        }
        else {
            //FacesMessages.instance().addToControl("loginfailed", "Login failed");
            return "loginfailed";
        }
    }

    // ----- Getters and Setters -----
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}