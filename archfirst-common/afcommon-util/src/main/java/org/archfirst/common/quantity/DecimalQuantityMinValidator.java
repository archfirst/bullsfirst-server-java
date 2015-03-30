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

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Minimum validator for DecimalQuantit.
 *
 * @author Naresh Bhatia
 */
public class DecimalQuantityMinValidator
implements ConstraintValidator<DecimalQuantityMin, DecimalQuantity> {
    
    public BigDecimal minValue; 
    
    public void initialize(DecimalQuantityMin constraintAnnotation) {
        minValue = new BigDecimal(constraintAnnotation.value());
    }

    public boolean isValid(
            DecimalQuantity object, ConstraintValidatorContext constraintContext) {

        if (object == null) return true;

        return (object.getValue().compareTo(minValue)) >= 0;
    }
}