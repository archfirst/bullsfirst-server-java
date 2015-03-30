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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A decimal quantity.
 * 
 * This is almost an immutable object - I tried to remove the setter and use
 * field-based access, but then Hibernate gives following error:
 * 
 * org.hibernate.PropertyNotFoundException: Could not find a setter for
 * property value in class org.archfirst.util.quantity.DecimalQuantity
 * 
 * For now, just don't use the setter.
 * 
 * @author Naresh Bhatia
 */
@XmlJavaTypeAdapter(DecimalQuantityAdapter.class)
@Embeddable
public class DecimalQuantity implements Comparable<DecimalQuantity>, Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final DecimalQuantity ZERO = new DecimalQuantity();

    /** The rounding mode used for calculations */
    private static RoundingMode roundingMode = RoundingMode.HALF_UP;

    private BigDecimal value;

    // ----- Constructors -----
    public DecimalQuantity() {
        this.value = BigDecimal.ZERO;
    }

    public DecimalQuantity(String value) {
        this.value = new BigDecimal(value);
    }

    public DecimalQuantity(BigDecimal value) {
        this.value = value;
    }

    public DecimalQuantity(long value) {
        this.value = new BigDecimal(value);
    }

    public DecimalQuantity(double value) {
        // BigDecimal(String) is the preferred constructor 
        this.value = new BigDecimal(Double.toString(value));
    }

    // ----- Operations -----
    public DecimalQuantity plus(DecimalQuantity that) {
        return new DecimalQuantity(value.add(that.getValue()));
    }

    public DecimalQuantity minus(DecimalQuantity that) {
        return new DecimalQuantity(value.subtract(that.getValue()));
    }

    public DecimalQuantity multiply(long multiplicand) {
        return new DecimalQuantity(value.multiply(new BigDecimal(multiplicand)));
    }

    public DecimalQuantity multiply(BigDecimal multiplicand) {
        return new DecimalQuantity(value.multiply(multiplicand));
    }

    public DecimalQuantity divide(long divisor) {
        BigDecimal bdDivisor = new BigDecimal(divisor);
        BigDecimal newValue = value.divide(bdDivisor, roundingMode);
        return new DecimalQuantity(newValue);
    }

    public DecimalQuantity divide(BigDecimal divisor) {
        BigDecimal newValue = value.divide(divisor, roundingMode);
        return new DecimalQuantity(newValue);
    }
    
    public DecimalQuantity abs() {
        return new DecimalQuantity(value.abs());
    }
    
    public DecimalQuantity negate() {
        return this.multiply(-1L);
    }
    
    public DecimalQuantity setScale(int newScale) {
        return new DecimalQuantity(
                value.stripTrailingZeros().setScale(newScale, roundingMode));
    }
    
    // ----- Getters and Setters -----
    public BigDecimal getValue() {
        return value;
    }
    private void setValue(BigDecimal value) {
        this.value = value;
    }

    // ----- Read-Only Operations -----
    public long longValue() {
        return value.longValue();
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public DecimalQuantity min(DecimalQuantity that) {
        return (this.compareTo(that) < 0) ? this : that;
    }

    public DecimalQuantity max(DecimalQuantity that) {
        return (this.compareTo(that) > 0) ? this : that;
    }

    /** Considers two quantities as equal only if they are equal in value and scale */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DecimalQuantity)) {
            return false;
        }
        final DecimalQuantity that = (DecimalQuantity)object;
        return this.value.equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /** Return true only if the quantity is positive. */
    @Transient
    public boolean isPlus() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    /** Return true only if the quantity is negative. */
    @Transient
    public boolean isMinus() {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }

    /** Return true only if the quantity is zero. */
    @Transient
    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Considers two quantities as equal if they are equal in value regardless
     * of scale (unlike equal() which requires the scale to be the same too)
     */
    public boolean eq(DecimalQuantity that) {
        return this.value.compareTo(that.getValue()) == 0;
    }

    /**
     * Return true only if 'this' quantity is greater than 'that' quantity.
     */
    public boolean gt(DecimalQuantity that) {
        return this.value.compareTo(that.getValue()) > 0;
    }

    /**
     * Return true only if 'this' quantity is greater than or equal to
     * 'that' quantity.
     */
    public boolean gteq(DecimalQuantity that) {
        return this.value.compareTo(that.getValue()) >= 0;
    }

    /**
     * Return true only if 'this' quantity is less than 'that' quantity.
     */
    public boolean lt(DecimalQuantity that) {
        return this.value.compareTo(that.getValue()) < 0;
    }

    /**
     * Return true only if 'this' quantity is less than or equal to
     * 'that' quantity.
     */
    public boolean lteq(DecimalQuantity that) {
        return this.value.compareTo(that.getValue()) <= 0;
    }

    /**
     * Considers two quantities as equal if they are equal in value
     * regardless of scale
     */
    @Override
    public int compareTo(DecimalQuantity that) {
        return this.value.compareTo(that.getValue());
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}