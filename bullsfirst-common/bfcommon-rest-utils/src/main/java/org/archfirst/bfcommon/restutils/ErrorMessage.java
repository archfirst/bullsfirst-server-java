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

/**
 * ErrorMessage
 *
 * @author Naresh Bhatia
 */
public class ErrorMessage {
    // ----- Constructors -----
    public ErrorMessage() {
    }

    public ErrorMessage(String detail) {
        this.detail = detail;
    }

    // ----- Attributes -----
    private String detail;

    // ----- Getters and Setters -----
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
}