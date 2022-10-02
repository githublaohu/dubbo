/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.rpc.rocketmq;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RocketMQProtocolServer.class})
public class RocketMQProtocolServerTest {

    private static RocketMQProtocolServer rocketMQProtocolServer;
    private static String ROCKETMQ_URL_TEMPLATE = "rocketmq://127.0.0.1:9876";

    private static URL serverUrl;

    @Before
    public void setUp() {
        // init
        rocketMQProtocolServer = PowerMockito.spy(new RocketMQProtocolServer());

        // set serverUrl
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("brokerName", "broker-a");
        paramMap.put("topic", RocketMQProtocolConstant.DUBBO_DEFAULT_PROTOCOL_TOPIC);
        paramMap.put("groupModel", "select");
        paramMap.put("group", "DEFAULT_GROUP");
        paramMap.put("version", "4.9.2");
        paramMap.put("corethreads", "1");
        paramMap.put("threads", "1");
        serverUrl = URL.valueOf(ROCKETMQ_URL_TEMPLATE + "?" + TestRocketMQUtils.mapToString(paramMap));
    }

    @After
    public void tearDown() {
        // release
        rocketMQProtocolServer = null;
    }

    @Test
    public void testCreateConsumer() throws Exception {
        Whitebox.setInternalState(rocketMQProtocolServer, "messageListenerConcurrently", Mockito.mock(MessageListenerConcurrently.class));
        rocketMQProtocolServer.reset(serverUrl);
        DefaultMQPushConsumer defaultMQPushConsumer = PowerMockito.mock(DefaultMQPushConsumer.class);
        PowerMockito.whenNew(DefaultMQPushConsumer.class).withAnyArguments().thenReturn(defaultMQPushConsumer);
        PowerMockito.doNothing().when(defaultMQPushConsumer).start();

        rocketMQProtocolServer.createConsumer();
        Assertions.assertNotNull(rocketMQProtocolServer.getDefaultMQPushConsumer());
    }

    @Test
    public void testReset() throws Exception {
        DefaultMQPushConsumer defaultMQPushConsumer = PowerMockito.mock(DefaultMQPushConsumer.class);
        PowerMockito.whenNew(DefaultMQPushConsumer.class).withArguments(String.class, String.class).thenReturn(defaultMQPushConsumer);
        PowerMockito.doNothing().when(rocketMQProtocolServer).createConsumer();
        Whitebox.setInternalState(rocketMQProtocolServer, "model", CommonConstants.PROVIDER);

        rocketMQProtocolServer.reset(serverUrl);
        Assertions.assertNotNull(rocketMQProtocolServer.getDefaultMQProducer());
    }

}
