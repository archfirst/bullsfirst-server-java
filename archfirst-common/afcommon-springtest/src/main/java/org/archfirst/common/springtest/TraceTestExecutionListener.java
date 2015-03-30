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

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * TraceTestExecutionListener
 *
 * @author Naresh Bhatia
 */
public class TraceTestExecutionListener extends AbstractTestExecutionListener {
    private static final Logger logger =
        LoggerFactory.getLogger(TraceTestExecutionListener.class);

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        Method method = testContext.getTestMethod();
        logger.trace(">>> Entering method '{}' of class [{}]",
                method.getName(), method.getDeclaringClass().getName());
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        Method method = testContext.getTestMethod();
        logger.trace("<<< Exiting method '{}' of class [{}]",
                method.getName(), method.getDeclaringClass().getName());
    }
}