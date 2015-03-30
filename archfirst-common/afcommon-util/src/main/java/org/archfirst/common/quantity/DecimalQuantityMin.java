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
package org.archfirst.common.quantity;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Constraint definition for the minimum value of a DecimalQuantity.
 *
 * @author Naresh Bhatia
 */
@Documented
@Constraint(validatedBy = DecimalQuantityMinValidator.class)
@Target( {METHOD, FIELD} )
@Retention(RUNTIME)
public @interface DecimalQuantityMin {

    String message() default "{javax.validation.constraints.Min.message}";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};

    /**
    * The <code>String</code> representation of the minimum value according to
    * the <code>BigDecimal</code> string representation. Will be used as the
    * constraint parameter, e.g. @DecimalQuantityMin(value="1.00")
    * 
    * @return value the element must be higher or equal to
    */
    String value();
}