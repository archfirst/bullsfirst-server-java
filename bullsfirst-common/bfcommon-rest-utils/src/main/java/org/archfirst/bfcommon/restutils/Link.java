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
package org.archfirst.bfcommon.restutils;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

/**
 * Link to a resource
 *
 * @author Naresh Bhatia
 */
public class Link {
    // ----- Constructors -----
    public Link() {
    }

    public Link(UriInfo uriInfo, String id) {
        this.rel = "self";
        this.id = id;
        this.uri = uriInfo.getAbsolutePathBuilder()
            .path(id)
            .build();
    }

    // ----- Attributes -----
    private String rel;
    private String id;
    private URI uri;

    // ----- Getters and Setters -----
    public String getRel() {
        return rel;
    }
    public void setRel(String rel) {
        this.rel = rel;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public URI getUri() {
        return uri;
    }
    public void setUri(URI uri) {
        this.uri = uri;
    }
}