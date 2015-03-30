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
package org.archfirst.common.jaxb;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * Controls where a JAXB implementation puts the generated schema files.
 *
 * @author Naresh Bhatia
 */
public class DefaultSchemaOutputResolver extends SchemaOutputResolver {
    
    private File baseDir;
    
    public DefaultSchemaOutputResolver(String baseDirPath) {
        baseDir = new File(baseDirPath);
    }

    @Override
    public Result createOutput(String namespaceUri, String suggestedFileName)
            throws IOException {
        return new StreamResult(new File(baseDir, suggestedFileName));
    }
}