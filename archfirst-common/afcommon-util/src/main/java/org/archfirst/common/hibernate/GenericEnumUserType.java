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
package org.archfirst.common.hibernate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.AbstractStandardBasicType;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 * <pre>
 * ---------------------------------------------------------------------------------------
 * Adapted from http://www.hibernate.org/272.html.
 * I have applied the various fixes suggested in the thread plus some of my requirements -
 * so this code does not exactly match any single version presented in the thread.
 * 
 * Naresh Bhatia
 * ---------------------------------------------------------------------------------------
 * </pre>
 * 
 * <p>Implements a generic enum user type identified by a single identifier column.</p>
 * 
 * <p><ul>
 *   <li>The enum type being represented by the certain user type must be set by using the
 *       'enumClass' property.</li>
 *   <li>The identifier representing a enum value is retrieved by the identifierMethod. The
 *       name of the identifier method can be specified by the 'identifierMethod' property and
 *       by default the name() method is used.</li>
 *   <li>The identifier type is automatically determined by the return-type of the
 *       identifierMethod.</li>
 *   <li>The valueOfMethod is the name of the static factory method returning the
 *       enumeration object being represented by the given identifier. The valueOfMethod's name
 *       can be specified by setting the 'valueOfMethod' property. The default valueOfMethod's
 *       name is 'valueOf'.
 *   </li>
 * </ul></p>
 * 
 * <p>Example of an enum type represented by an int value:</p>
 * 
 * <code>
 * public enum SimpleNumber {
 *   Unknown(-1), Zero(0), One(1), Two(2), Three(3);
 *   int value;
 *   private SimpleNumber(int value) {
 *       this.value = value;
 *   }
 *
 *   public int toInt() { return value; }
 *   public SimpleNumber fromInt(int value) {
 *         switch(value) {
 *         case 0: return Zero;
 *         case 1: return One;
 *         case 2: return Two;
 *         case 3: return Three;
 *         default: return Unknown;
 *     }
 *   }
 * }
 * </code>
 * 
 * <p>The Mapping would look like this:</p>
 * 
 * <code>
 *    <typedef name="SimpleNumber" class="GenericEnumUserType">
 *        <param name="enumClass">SimpleNumber</param>
 *        <param name="identifierMethod">toInt</param>
 *        <param name="valueOfMethod">fromInt</param>
 *    </typedef>
 *    
 *    <class ...>
 *        ...
 *        <property name="number" column="number" type="SimpleNumber"/>
 *    </class>
 * </code>
 * 
 * If the enum class implements toIdentifier() and fromIdentifier() methods
 * then, the mapping can be simplified as follows:
 * 
 * <code>
 *    <typedef name="SimpleNumber" class="GenericEnumUserType">
 *        <param name="enumClass">SimpleNumber</param>
 *    </typedef>
 * </code>
 * 
 * @author Martin Kersten
 * @since 05.05.2005
 */
public class GenericEnumUserType implements UserType, ParameterizedType {
    private static final String DEFAULT_TO_IDENTIFIER_METHOD_NAME = "toIdentifier";
    private static final String DEFAULT_FROM_IDENTIFIER_METHOD_NAME = "fromIdentifier";

    @SuppressWarnings("rawtypes")
    private Class<? extends Enum> enumClass;
    private Class<?> identifierType;
    private Method identifierMethod;
    private Method valueOfMethod;
    private AbstractStandardBasicType<? extends Object> type;
    private int[] sqlTypes;

    @SuppressWarnings({ "unchecked"})
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClass");
        try {
            enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
        } catch (ClassNotFoundException cfne) {
            throw new HibernateException("Enum class not found", cfne);
        }

        String identifierMethodName = parameters.getProperty("identifierMethod",
                DEFAULT_TO_IDENTIFIER_METHOD_NAME);

        try {
            identifierMethod = enumClass.getMethod(identifierMethodName, new Class[0]);
            identifierType = identifierMethod.getReturnType();
        } catch (Exception e) {
            throw new HibernateException("Failed to obtain identifier method", e);
        }

        type = (AbstractSingleColumnStandardBasicType<? extends Object>)
            new TypeResolver().heuristicType(identifierType.getName(), parameters);

        if (type == null)
            throw new HibernateException("Unsupported identifier type "
                    + identifierType.getName());

        sqlTypes = new int[] { ((AbstractSingleColumnStandardBasicType<?>)type).sqlType() };

        String valueOfMethodName = parameters.getProperty("valueOfMethod",
                DEFAULT_FROM_IDENTIFIER_METHOD_NAME);

        try {
            valueOfMethod = enumClass.getMethod(valueOfMethodName,
                    new Class[] { identifierType });
        } catch (Exception e) {
            throw new HibernateException("Failed to obtain valueOf method", e);
        }
    }

    @SuppressWarnings("rawtypes")
    public Class<? extends Enum> returnedClass() {
        return enumClass;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws HibernateException, SQLException {
        Object identifier = type.get(rs, names[0], null);
        if (rs.wasNull()) {
            return null;
        }

        try {
            return valueOfMethod.invoke(enumClass, new Object[] { identifier });
        } catch (Exception e) {
            throw new HibernateException("Exception while invoking valueOf method '"
                    + valueOfMethod.getName() + "' of " + "enumeration class '"
                    + enumClass + "'", e);
        }
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index)
            throws HibernateException, SQLException {
        try {
            if (value == null) {
                st.setNull(index, ((AbstractSingleColumnStandardBasicType<?>) type).sqlType());
            } else {
                Object identifier = identifierMethod.invoke(value, new Object[0]);
                type.nullSafeSet(st, identifier, index, null);
            }
        } catch (Exception e) {
            throw new HibernateException("Exception while invoking identifierMethod '"
                    + identifierMethod.getName() + "' of " + "enumeration class '"
                    + enumClass + "'", e);
        }
    }

    public int[] sqlTypes() {
        return sqlTypes;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }
}