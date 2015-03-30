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
package org.archfirst.common.springtest;

/**
 * BasicConcordionRunner
 *
 * @see <a href="http://gist.github.com/159807">Original Source</a>
 * @author Mark Derricutt
 */
import org.concordion.api.*;
import org.concordion.internal.ConcordionBuilder;

public class BasicConcordionRunner implements Runner {

    public RunnerResult execute(Resource resource, String href) throws Exception {

        Resource hrefResource = resource.getParent().getRelativeResource(href);
        String name = hrefResource.getPath().replaceFirst("/", "").replace("/", ".").replaceAll("\\.html$", "");
        Class<?> concordionClass;
        try {
            concordionClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            concordionClass = Class.forName(name + "Test");
        }

        ResultSummary resultSummary = new ConcordionBuilder()
                .build()
                .process(hrefResource, concordionClass.newInstance());

        Result result = Result.SUCCESS;
        if (resultSummary.getFailureCount() > 0) {
            result = Result.FAILURE;
        }
        if (resultSummary.getExceptionCount() > 0) {
            result = Result.EXCEPTION;
        }

        return new RunnerResult(result);
    }
}