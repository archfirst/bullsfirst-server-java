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
package org.archfirst.common.money;

import static java.math.BigDecimal.ZERO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Currency;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.archfirst.common.quantity.DecimalQuantity;
import org.archfirst.common.quantity.Percentage;

/**
 * <P>
 * Based on <tt>Money</tt> class published at
 * <a href="http://www.javapractices.com/topic/TopicAction.do?Id=13">
 *     www.javapractices.com
 * </a>.
 * Modifications include the following:
 * <ul>
 *   <li>JPA annotations have been added to make the class persistent.</li>
 *   <li>Private setters are provided for JPA to work correctly
 *       (the class should still be treated as immutable).</li>
 *   <li>Operations with {@link DecimalQuantity} have been introduced.</li>
 *   <li><tt>validateState()</tt> method has been modified to remove the
 *       the scale restriction of the currency. This allows arbitrary
 *       precision for Money values. This is needed for some applications,
 *       for example trading, where say a US stock price must be specified
 *       with 4 decimal precision.</li>
 *   <li>Scaling operations in <tt>times()</tt> methods have been commented
 *       out, again to get arbitrary precision amounts. A method called
 *       <tt>scaleToCurrency()</tt> has been introduced if scaling to currency
 *       is desired.</li>
 *   <li><tt>roundToInterval()</tt> method has been added to support financial
 *       applications.</li>
 * </ul>
 * 
 * <h2>Original Documentation</h2>
 * <P>Represent an amount of money in any currency.
 *
 * <P>This class assumes <em>decimal currency</em>, without funky divisions 
 * like 1/5 and so on. <tt>Money</tt> objects are immutable. Like {@link BigDecimal}, 
 * many operations return new <tt>Money</tt> objects. In addition, most operations 
 * involving more than one <tt>Money</tt> object will throw a 
 * <tt>MismatchedCurrencyException</tt> if the currencies don't match.
 * 
 * <h2>Decimal Places and Scale</h2>
 * Monetary amounts can be stored in the database in various ways. Let's take
 * the example of dollars. It may appear in the database in the following ways :
 * <ul>
 * <li>as <tt>123456.78</tt>, with the usual number of decimal places
 * associated with that currency.
 * <li>as <tt>123456</tt>, without any decimal places at all.
 * <li>as <tt>123</tt>, in units of thousands of dollars.
 * <li>in some other unit, such as millions or billions of dollars.
 * </ul>
 * 
 * <P>
 * The number of decimal places or style of units is referred to as the
 * <em>scale</em> by {@link java.math.BigDecimal}. This class's constructors
 * take a <tt>BigDecimal</tt>, so you need to understand it use of the idea
 * of scale.
 * 
 * <P>
 * The scale can be negative. Using the above examples:
 * <table border='1' cellspacing='0' cellpadding='3'>
 * <tr>
 *   <th>Number</th>
 *   <th>Scale</th>
 * </tr>
 * <tr>
 *   <td>123456.78</td>
 *   <td>2</td>
 * </tr>
 * <tr>
 *   <td>123456</td>
 *   <td>0</td>
 * </tr>
 * <tr>
 *   <td>123 (thousands)</td>
 *   <td>-3</td>
 * </tr>
 * </table>
 * 
 * <P>
 * Note that scale and rounding are two separate issues. In addition, rounding
 * is only necessary for multiplication and division operations. It doesn't
 * apply to addition and subtraction.
 * 
 * <h2>Operations and Scale</h2>
 * <P>
 * Operations can be performed on items having <em>different scale</em>. For
 * example, these operations are valid (using an <em>ad hoc</em> symbolic
 * notation):
 * 
 * <PRE>
 * 10.plus(1.23) =&gt; 11.23
 * 10.minus(1.23) =&gt; 8.77
 * 10.gt(1.23) =&gt; true
 * 10.eq(10.00) =&gt; true
 * </PRE>
 * 
 * This corresponds to typical user expectations. An important exception to this
 * rule is that {@link #equals(Object)} is sensitive to scale (while
 * {@link #eq(Money)} is not) . That is,
 * 
 * <PRE>
 *   10.equals(10.00) =&gt; false
 * </PRE>
 * 
 * <h2>Multiplication, Division and Extra Decimal Places</h2>
 * <P>
 * Operations involving multiplication and division are different, since the
 * result can have a scale which exceeds that expected for the given currency.
 * For example
 * 
 * <PRE>
 * ($10.00).times(0.1256) =&gt; $1.256
 * </PRE>
 * 
 * which has more than two decimals. In such cases,
 * <em>this class will always round 
 * to the expected number of decimal places for that currency.</em>
 * This is the simplest policy, and likely conforms to the expectations of most
 * end users.
 * 
 * <P>
 * This class takes either an <tt>int</tt> or a {@link BigDecimal} for its
 * multiplication and division methods. It doesn't take <tt>float</tt> or
 * <tt>double</tt> for those methods, since those types don't interact well
 * with <tt>BigDecimal</tt>. Instead, the <tt>BigDecimal</tt> class must be
 * used when the factor or divisor is a non-integer.
 * 
 * <P>
 * <em>The {@link #init(Currency, RoundingMode)} method must be called at least 
 * once before using the other members of this class.</em>
 * It establishes your desired defaults. Typically, it will be called once (and
 * only once) upon startup.
 * 
 * <P>
 * Various methods in this class have unusually terse names, such as {@link #lt}
 * and {@link #gt}. The intent is that such names will improve the legibility
 * of mathematical expressions. Example :
 * 
 * <PRE>
 * if (amount.lt(hundred)) {
 *     cost = amount.times(price);
 * }
 * </PRE>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Money")
@Embeddable
public final class Money implements Comparable<Money>, Serializable {
    private static final long serialVersionUID = 1L;
    
    /** java.util.Currency does not specify a length, so we do it here */
    public static final int CURRENCY_LENGTH = 3;
    
    /** The default currency */
    private static Currency defaultCurrency = Currency.getInstance("USD");

    /** The rounding mode used for calculations */
    private static RoundingMode roundingMode = RoundingMode.HALF_UP;

    /** The money amount. Never null. */
    @XmlElement(name = "Amount", required = true)
    private BigDecimal amount;

    /** The currency of the money, such as US Dollars or Euros. Never null. */
    @XmlElement(name = "Currency", required = true)
    @XmlJavaTypeAdapter(CurrencyAdapter.class)
    private Currency currency;

    /**
     * Set default values for currency and rounding style.
     * 
     * <em>Your application must call this method upon startup</em>. This
     * method should usually be called only once (upon startup).
     * 
     * <P>
     * The recommended rounding style is {@link RoundingMode#HALF_EVEN}, also
     * called <em>banker's rounding</em>; this rounding style introduces the
     * least bias.
     * 
     * <P>
     * Setting these defaults allow you to use the more terse constructors of
     * this class, which are much more convenient.
     * 
     * <P>
     * (In a servlet environment, each app has its own classloader. Calling this
     * method in one app will never affect the operation of a second app running
     * in the same servlet container. They are independent.)
     */
    public static void init(
            Currency defaultCurrency, RoundingMode roundingMode) {
        Money.defaultCurrency = defaultCurrency;
        Money.roundingMode = roundingMode;
    }
    
    // ----- Constructors -----
    public Money() {
        this.amount = BigDecimal.ZERO;
        this.currency = defaultCurrency;
    }

    /**
     * Full constructor.
     * 
     * @param aAmount is required, can be positive or negative. The number of
     *            decimals in the amount cannot <em>exceed</em> the maximum
     *            number of decimals for the given {@link Currency}. It's
     *            possible to create a <tt>Money</tt> object in terms of
     *            'thousands of dollars', for instance. Such an amount would
     *            have a scale of -3.
     * @param aCurrency is required.
     */
    public Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
        validateState();
    }
    
    /**
     * Constructor taking a String amount
     * @param amount
     * @param currency
     */
    public Money(String amount, Currency currency) {
        this(new BigDecimal(amount), currency);
    }

    /**
     * Constructor taking only the money amount.
     * 
     * <P>
     * The currency and rounding style both take default values.
     * 
     * @param aAmount is required, can be positive or negative.
     */
    public Money(BigDecimal amount) {
        this(amount, defaultCurrency);
    }

    /**
     * Constructor taking only a double money amount.
     * 
     * <P>
     * The currency and rounding style both take default values.
     * 
     * @param aAmount is required, can be positive or negative.
     */
    public Money(Double amount) {
        this(new BigDecimal(Double.toString(amount)), defaultCurrency);
    }

    /**
     * Constructor taking a String amount
     * @param amount
     */
    public Money(String amount) {
        this(new BigDecimal(amount), defaultCurrency);
    }

    // ----- Commands -----
    /**
     * Add <tt>aThat</tt> <tt>Money</tt> to this <tt>Money</tt>.
     * Currencies must match.
     */
    public Money plus(Money aThat) {
        checkCurrenciesMatch(aThat);
        return new Money(amount.add(aThat.amount), currency);
    }

    /**
     * Subtract <tt>aThat</tt> <tt>Money</tt> from this <tt>Money</tt>.
     * Currencies must match.
     */
    public Money minus(Money aThat) {
        checkCurrenciesMatch(aThat);
        return new Money(amount.subtract(aThat.amount), currency);
    }

    /**
     * Sum a collection of <tt>Money</tt> objects. Currencies must match. You
     * are encouraged to use database summary functions whenever possible,
     * instead of this method.
     * 
     * @param moneys collection of <tt>Money</tt> objects, all of the same
     *            currency. If the collection is empty, then a zero value is
     *            returned.
     * @param currencyIfEmpty is used only when <tt>aMoneys</tt> is empty;
     *            that way, this method can return a zero amount in the desired
     *            currency.
     */
    public static Money sum(
            Collection<Money> moneys, Currency currencyIfEmpty) {

        Money sum = new Money(ZERO, currencyIfEmpty);
        for (Money money : moneys) {
            sum = sum.plus(money);
        }
        return sum;
    }
    
    /**
     * Multiply this <tt>Money</tt> by an integral factor.
     * 
     * The scale of the returned <tt>Money</tt> is equal to the scale of
     * 'this' <tt>Money</tt>.
     */
    public Money times(int aFactor) {
        BigDecimal factor = new BigDecimal(aFactor);
        BigDecimal newAmount = amount.multiply(factor);
        return new Money(newAmount, currency);
    }

    /**
     * Multiply this <tt>Money</tt> by an non-integral factor (having a
     * decimal point).
     * 
     * <P>
     * The scale of the returned <tt>Money</tt> is equal to the scale of
     * 'this' <tt>Money</tt> (this operation is disabled).
     */
    public Money times(double aFactor) {
        BigDecimal newAmount = amount.multiply(asBigDecimal(aFactor));
        // newAmount = newAmount.setScale(getNumDecimalsForCurrency());
        return new Money(newAmount, currency);
    }

    /**
     * Multiply this <tt>Money</tt> by an non-integral factor (having a
     * decimal point).
     * 
     * <P>
     * The scale of the returned <tt>Money</tt> is equal to the scale of
     * 'this' <tt>Money</tt> plus the scale of the factor.
     */
    public Money times(BigDecimal aFactor) {
        BigDecimal newAmount = amount.multiply(aFactor);
        // newAmount = newAmount.setScale(getNumDecimalsForCurrency());
        return new Money(newAmount, currency);
    }

    /**
     * Multiply this <tt>Money</tt> by a DecimalQuantity.
     * 
     * <P>
     * The scale of the returned <tt>Money</tt> is equal to the scale of
     * 'this' <tt>Money</tt> plus the scale of the decimal quantity.
     */
    public Money times(DecimalQuantity aQuantity) {
        return times(aQuantity.getValue());
    }

    /**
     * Divide this <tt>Money</tt> by an integral divisor.
     * 
     * <P>
     * The scale of the returned <tt>Money</tt> is as specified.
     */
    public Money div(int aDivisor, int scale) {
        return div(new BigDecimal(aDivisor), scale);
    }

    /**
     * Divide this <tt>Money</tt> by an non-integral divisor.
     * 
     * <P>
     * The scale of the returned <tt>Money</tt> is as specified.
     */
    public Money div(double aDivisor, int scale) {
        return div(asBigDecimal(aDivisor), scale);
    }

    /**
     * Divide this <tt>Money</tt> by an non-integral divisor.
     * 
     * <P>
     * The scale of the returned <tt>Money</tt> is as specified.
     */
    public Money div(BigDecimal aDivisor, int scale) {
        BigDecimal newAmount = amount.divide(aDivisor, scale, roundingMode);
        return new Money(newAmount, currency);
    }

    /**
     * Divide this <tt>Money</tt> by a DecimalQuantity.
     * 
     * <P>
     * The scale of the returned <tt>Money</tt> is as specified.
     */
    public Money div(DecimalQuantity aDivisor, int scale) {
        return div(aDivisor.getValue(), scale);
    }
    
    /** Return the absolute value of the amount. */
    public Money abs() {
        return isPlus() ? this : times(-1);
    }

    /** Return the amount x (-1). */
    public Money negate() {
        return times(-1);
    }

    /**
     * Scales this money object to the expected number of decimal places for
     * this money's currency. For example,
     * <code>
     * $100     -> $100.00
     * $100.123 -> $100.12
     * </code>
     * 
     * @return Money value scaled to currency
     */
    public Money scaleToCurrency() {
        return setScale(getNumDecimalsForCurrency());
    }
    
    public Money setScale(int newScale) {
        BigDecimal newAmount = amount.setScale(newScale, roundingMode);
        return new Money(newAmount, currency);
    }
    
    /**
     * Sets the maximum scale of this money object to the specified value.
     * The minimum scale is maintained as the expected number of decimal
     * places for this money's currency. For example, assuming maxScale = 4,
     * following conversions will be performed:
     * <code>
     * $100       -> $100.00
     * $100.1     -> $100.10
     * $100.10    -> $100.10
     * $100.100   -> $100.10
     * $100.101   -> $100.101
     * $100.1001  -> $100.1001
     * $100.10001 -> $100.0000
     * </code>
     * 
     * @param maxScale
     * @return
     */
    public Money setMaxScale(int maxScale) {
        BigDecimal newAmount = amount.stripTrailingZeros();
        if (newAmount.scale() < getNumDecimalsForCurrency()) {
            newAmount =
                newAmount.setScale(getNumDecimalsForCurrency(), roundingMode);
        }
        else if (newAmount.scale() > maxScale) {
            newAmount = newAmount.setScale(maxScale, roundingMode);
        }
        return new Money(newAmount, currency);
    }
    
    /**
     * Rounds this money amount to the specified interval. Useful in financial
     * markets where a stock price needs to be rounded to the nearest tick size.
     * For example:
     * <code>
     * Price  Tick Size -> Rounded Price
     * 100.1    0.25    ->    100.00
     * 100.2    0.25    ->    100.25
     * 100.1    0.125   ->    100.125
     * 100.2    0.125   ->    100.250
     * </code>
     * 
     * (Based on <a href="http://stackoverflow.com/questions/641848">this</a>
     * discussion at stackoverflow.com.)
     * 
     * @param interval for rounding
     * @return Money with rounded amount
     */
    public Money roundToInterval(BigDecimal interval) {
        BigDecimal newAmount =
            amount.divide(interval).setScale(0, RoundingMode.HALF_UP).multiply(interval);
        return new Money(newAmount, currency);
    }

    // ----- Queries -----
    /**
     * Return <tt>true</tt> only if <tt>aThat</tt> <tt>Money</tt> has the
     * same currency as this <tt>Money</tt>.
     */
    @Transient
    public boolean isSameCurrencyAs(Money aThat) {
        boolean result = false;
        if (aThat != null) {
            result = this.currency.equals(aThat.currency);
        }
        return result;
    }

    /** Return <tt>true</tt> only if the amount is positive. */
    @Transient
    public boolean isPlus() {
        return amount.compareTo(ZERO) > 0;
    }

    /** Return <tt>true</tt> only if the amount is negative. */
    @Transient
    public boolean isMinus() {
        return amount.compareTo(ZERO) < 0;
    }

    /** Return <tt>true</tt> only if the amount is zero. */
    @Transient
    public boolean isZero() {
        return amount.compareTo(ZERO) == 0;
    }

    /**
     * Equals (insensitive to scale).
     * 
     * <P>
     * Return <tt>true</tt> only if the amounts are equal. Currencies must
     * match. This method is <em>not</em> synonymous with the <tt>equals</tt>
     * method.
     */
    public boolean eq(Money aThat) {
        checkCurrenciesMatch(aThat);
        return compareAmount(aThat) == 0;
    }

    /**
     * Greater than.
     * 
     * <P>
     * Return <tt>true</tt> only if 'this' amount is greater than 'that'
     * amount. Currencies must match.
     */
    public boolean gt(Money aThat) {
        checkCurrenciesMatch(aThat);
        return compareAmount(aThat) > 0;
    }

    /**
     * Greater than or equal to.
     * 
     * <P>
     * Return <tt>true</tt> only if 'this' amount is greater than or equal to
     * 'that' amount. Currencies must match.
     */
    public boolean gteq(Money aThat) {
        checkCurrenciesMatch(aThat);
        return compareAmount(aThat) >= 0;
    }

    /**
     * Less than.
     * 
     * <P>
     * Return <tt>true</tt> only if 'this' amount is less than 'that' amount.
     * Currencies must match.
     */
    public boolean lt(Money aThat) {
        checkCurrenciesMatch(aThat);
        return compareAmount(aThat) < 0;
    }

    /**
     * Less than or equal to.
     * 
     * <P>
     * Return <tt>true</tt> only if 'this' amount is less than or equal to
     * 'that' amount. Currencies must match.
     */
    public boolean lteq(Money aThat) {
        checkCurrenciesMatch(aThat);
        return compareAmount(aThat) <= 0;
    }

    /**
     * Get percentage of this compared to that. For example,
     * $6.getPercentage($12, 2) = 50.00%
     * @param aThat
     * @param fractionalDigits
     * @return
     */
    @Transient
    public Percentage getPercentage(Money aThat, int fractionalDigits) {
        checkCurrenciesMatch(aThat);
        return new Percentage(amount, aThat.getAmount(), fractionalDigits);
    }

    /**
     * Returns {@link #getAmount()}.getPlainString() + space +
     * {@link #getCurrency()}.getSymbol().
     * 
     * <P>
     * The return value uses the runtime's <em>default locale</em>, and will
     * not always be suitable for display to an end user.
     */
    public String toString() {
        // This is the original implementation
        // return amount.toPlainString() + " " + currency.getSymbol();

        // We will use NumberFormat instead
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setCurrency(currency);
        formatter.setMinimumFractionDigits(amount.scale());
        return formatter.format(amount);
    }

    /**
     * Like {@link BigDecimal#equals(java.lang.Object)}, this <tt>equals</tt>
     * method is also sensitive to scale.
     * 
     * For example, <tt>10</tt> is <em>not</em> equal to <tt>10.00</tt>
     * The {@link #eq(Money)} method, on the other hand, is <em>not</em>
     * sensitive to scale.
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Money)) {
            return false;
        }
        Money that = (Money)object;
        // the object fields are never null :
        boolean result = (this.amount.equals(that.amount));
        result = result && (this.currency.equals(that.currency));
        return result;
    }

    public int hashCode() {
        return this.amount.hashCode() + this.currency.hashCode();
    }

    public int compareTo(Money that) {
        if (this == that) {
            return 0;
        }

        // the object fields are never null
        int comparison = this.amount.compareTo(that.amount);
        if (comparison != 0) {
            return comparison;
        }

        comparison = this.currency.getCurrencyCode().compareTo(
            that.currency.getCurrencyCode());
        if (comparison != 0) {
            return comparison;
        }

        return 0;
    }

    @Transient
    private int getNumDecimalsForCurrency() {
        return currency.getDefaultFractionDigits();
    }

    /** Ignores scale: 0 same as 0.00 */
    private int compareAmount(Money aThat) {
        return this.amount.compareTo(aThat.amount);
    }

    private BigDecimal asBigDecimal(double aDouble) {
        String asString = Double.toString(aDouble);
        return new BigDecimal(asString);
    }

    private void validateState() {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
//        if (amount.scale() > getNumDecimalsForCurrency()) {
//            throw new IllegalArgumentException("Number of decimals is "
//                    + amount.scale() + ", but currency only takes "
//                    + getNumDecimalsForCurrency() + " decimals.");
//        }
    }

    private void checkCurrenciesMatch(Money aThat) {
        if (!this.currency.equals(aThat.getCurrency())) {
            throw new MismatchedCurrencyException(aThat.getCurrency()
                    + " doesn't match the expected currency : " + currency);
        }
    }

    // ----- Getters and Setters -----
    /** Returns the amount */
    public BigDecimal getAmount() {
        return amount;
    }
    private void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /** Returns the currency */
    public Currency getCurrency() {
        return currency;
    }
    private void setCurrency(Currency currency) {
        this.currency = currency;
    }

    // ----- Inner Classes -----
    /**
     * Thrown when a set of <tt>Money</tt> objects do not have matching
     * currencies. For example, adding together Euros and Dollars does not make
     * any sense.
     */
    public static final class MismatchedCurrencyException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        MismatchedCurrencyException(String aMessage) {
            super(aMessage);
        }
    }
}