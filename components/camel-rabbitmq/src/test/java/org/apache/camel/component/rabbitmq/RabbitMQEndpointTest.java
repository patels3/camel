/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.rabbitmq;

import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

import com.rabbitmq.client.Envelope;

import org.apache.camel.Exchange;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RabbitMQEndpointTest {

    private Envelope envelope = Mockito.mock(Envelope.class);

    @Test
    public void testCreatingRabbitExchangeSetsHeaders() throws URISyntaxException {
        RabbitMQEndpoint endpoint =
                new RabbitMQEndpoint("rabbitmq:localhost/exchange", "localhost/exchange", new RabbitMQComponent());

        String routingKey = UUID.randomUUID().toString();
        String exchangeName = UUID.randomUUID().toString();
        long tag = UUID.randomUUID().toString().hashCode();

        Mockito.when(envelope.getRoutingKey()).thenReturn(routingKey);
        Mockito.when(envelope.getExchange()).thenReturn(exchangeName);
        Mockito.when(envelope.getDeliveryTag()).thenReturn(tag);

        Exchange exchange = endpoint.createRabbitExchange(envelope);
        assertEquals(exchangeName, exchange.getIn().getHeader(RabbitMQConstants.EXCHANGE_NAME));
        assertEquals(routingKey, exchange.getIn().getHeader(RabbitMQConstants.ROUTING_KEY));
        assertEquals(tag, exchange.getIn().getHeader(RabbitMQConstants.DELIVERY_TAG));
    }

    @Test
    public void creatingExecutorUsesThreadPoolSettings() throws Exception {

        RabbitMQEndpoint endpoint =
                new RabbitMQEndpoint("rabbitmq:localhost/exchange", "localhost/exchange", new RabbitMQComponent());
        endpoint.setThreadPoolSize(400);
        ThreadPoolExecutor executor = endpoint.createExecutor();

        assertEquals(400, executor.getCorePoolSize());
    }

    @Test
    public void assertSingleton() throws URISyntaxException {
        RabbitMQEndpoint endpoint =
                new RabbitMQEndpoint("rabbitmq:localhost/exchange", "localhost/exchange", new RabbitMQComponent());
        assertTrue(endpoint.isSingleton());
    }
}
