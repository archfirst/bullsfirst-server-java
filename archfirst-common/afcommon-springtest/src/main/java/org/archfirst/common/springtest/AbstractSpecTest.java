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

import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionBuilder;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * AbstractSpecTest
 *
 * @author Naresh Bhatia
 */
public abstract class AbstractSpecTest extends AbstractTest {

    @BeforeTest
    public void concordionSetup() {
        System.setProperty(
                "concordion.runner.concordion",
                BasicConcordionRunner.class.getName());
    }

    /**
     * Runs a Concordion specification test.
     * @throws Exception
     */
    @Test
    public void run() throws Exception {
        ResultSummary resultSummary = new ConcordionBuilder().build().process(this);
        resultSummary.print(System.out, this);
        resultSummary.assertIsSatisfied(this);
    }
}