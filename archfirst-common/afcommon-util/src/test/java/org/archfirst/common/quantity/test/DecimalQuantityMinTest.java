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
package org.archfirst.common.quantity.test;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.archfirst.common.quantity.DecimalQuantity;
import org.archfirst.common.quantity.DecimalQuantityMin;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * DecimalQuantityMinTest
 *
 * @author Naresh Bhatia
 */
public class DecimalQuantityMinTest {

    private static Validator validator;
    private static String VIOLATION_MESSAGE =
        "must be greater than or equal to 1.00";

    @BeforeClass
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testOrderIsTooSmall() {
        Order order = new Order(new DecimalQuantity());

        Set<ConstraintViolation<Order>> constraintViolations =
            validator.validate(order);

        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(
            constraintViolations.iterator().next().getMessage(),
            VIOLATION_MESSAGE);
    }

    @Test
    public void testOrderIsCorrectSize() {
        Order order = new Order(new DecimalQuantity("1"));

        Set<ConstraintViolation<Order>> constraintViolations =
            validator.validate(order);

        Assert.assertEquals(constraintViolations.size(), 0);
    }

    public class Order {

        private DecimalQuantity quantity;

        public Order(DecimalQuantity quantity) {
            this.quantity = quantity;
        }

        @DecimalQuantityMin(value="1.00")
        public DecimalQuantity getQuantity() {
            return quantity;
        }
    }
}