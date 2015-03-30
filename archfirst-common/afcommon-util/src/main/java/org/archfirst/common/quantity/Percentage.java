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
import java.text.NumberFormat;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A way to express how large or small one quantity is relative to another
 * quantity. For example, 6 is 50% of 12. Stored internally as 0.50.
 *
 * @author Naresh Bhatia
 */
@XmlJavaTypeAdapter(PercentageAdapter.class)
@Embeddable
public class Percentage implements Comparable<Percentage>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final Percentage ZERO = new Percentage();

    /** The rounding mode used for calculations */
    private static RoundingMode roundingMode = RoundingMode.HALF_UP;

    private BigDecimal value;

    // ----- Constructors -----
    /** Creates 0% */
    public Percentage() {
        this.value = BigDecimal.ZERO;
    }
    
    /** quantity1=6, quantity2=12, fractionalDigits=2 creates 50.00% */
    public Percentage(
            BigDecimal quantity1,
            BigDecimal quantity2,
            int fractionalDigits) {
        this.value = (quantity2.compareTo(BigDecimal.ZERO) == 0) ?
                BigDecimal.ZERO.setScale(fractionalDigits + 2) :
                quantity1.divide(quantity2, fractionalDigits + 2, roundingMode);
    }

    public Percentage(
            String quantity1,
            String quantity2,
            int fractionalDigits) {
        this(new BigDecimal(quantity1), new BigDecimal(quantity2), fractionalDigits);
    }

    public Percentage(
            DecimalQuantity quantity1,
            DecimalQuantity quantity2,
            int fractionalDigits) {
        this(quantity1.getValue(), quantity2.getValue(), fractionalDigits);
    }

    // ----- Getters and Setters -----
    public BigDecimal getValue() {
        return value;
    }
    private void setValue(BigDecimal value) {
        this.value = value;
    }

    // ----- Read-Only Operations -----
    /** Considers two percentages as equal only if they are equal in value and scale */
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

    /**
     * Considers two percentages as equal if they are equal in value regardless
     * of scale (unlike equal() which requires the scale to be the same too)
     */
    public boolean eq(DecimalQuantity that) {
        return this.value.compareTo(that.getValue()) == 0;
    }

    /**
     * Considers two quantities as equal if they are equal in value
     * regardless of scale
     */
    @Override
    public int compareTo(Percentage that) {
        return this.value.compareTo(that.getValue());
    }

    @Override
    public String toString() {
        NumberFormat formatter = NumberFormat.getPercentInstance();
        formatter.setMinimumFractionDigits(value.scale() - 2);
        return formatter.format(value);
    }
}