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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.archfirst.common.jaxb.DefaultSchemaOutputResolver;
import org.archfirst.common.quantity.DecimalQuantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * DecimalQuantityXmlAdapterTest
 *
 * @author Naresh Bhatia
 */
public class DecimalQuantityXmlAdapterTest {
    private static final Logger logger =
        LoggerFactory.getLogger(DecimalQuantityXmlAdapterTest.class);

    @Test
    public void testOrderMarshalUnmarshal() throws JAXBException, IOException {

        // Create an order
        Order order = new Order(new DecimalQuantity("10.2"));

        // Write it out as XML
        JAXBContext jaxbContext = JAXBContext.newInstance(Order.class);
        StringWriter writer = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(order, writer);
        String orderString = writer.toString();
        logger.debug("Order converted to XML:\n" + orderString);

        // Write out the schema (for debugging purposes)
        jaxbContext.generateSchema(new DefaultSchemaOutputResolver("target"));

        // Read it from XML
        Order orderRead =
            (Order)jaxbContext.createUnmarshaller().unmarshal(
                new StringReader(orderString));

        // Compare quantities
        Assert.assertEquals(orderRead.getQuantity(), order.getQuantity());

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "Order")
    public static class Order {

        @XmlElement(name = "Quantity", required = true)
        private DecimalQuantity quantity;

        public Order() {
        }

        public Order(DecimalQuantity quantity) {
            this.quantity = quantity;
        }

        public DecimalQuantity getQuantity() {
            return quantity;
        }
        public void setQuantity(DecimalQuantity quantity) {
            this.quantity = quantity;
        }
    }
}