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
import org.apache.dubbo.rpc.Invoker;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RocketMQProtocol.class})
public class RocketMQProtocolTest {

    private static RocketMQProtocolServer rocketMQProtocolServer;
    private static RocketMQProtocol rocketMQProtocol;
    private static String ROCKETMQ_URL_TEMPLATE = "rocketmq://127.0.0.1:9876";
    private static URL registryUrl;


    @Before
    public void setUp() {
        // init
        rocketMQProtocol = PowerMockito.spy(new RocketMQProtocol());
        rocketMQProtocolServer = PowerMockito.spy(new RocketMQProtocolServer());
    }

    @After
    public void tearDown() {
        // release
        rocketMQProtocol = null;
        rocketMQProtocolServer = null;
    }

    @Test
    public void testExport() throws Exception {
        // set serverUrl
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("brokerName", "broker-a");
        paramMap.put("topic", RocketMQProtocolConstant.DUBBO_DEFAULT_PROTOCOL_TOPIC);
        paramMap.put("groupModel", "select");
        paramMap.put("group", "DEFAULT_GROUP");
        paramMap.put("version", "4.9.2");
        paramMap.put("corethreads", "1");
        paramMap.put("threads", "1");
        registryUrl = URL.valueOf(ROCKETMQ_URL_TEMPLATE + "?" + TestRocketMQUtils.mapToString(paramMap));

        Invoker<RocketMQInvoker> invoker = PowerMockito.mock(Invoker.class);
        PowerMockito.when(invoker.getUrl()).thenReturn(registryUrl);
        PowerMockito.when(invoker.getInterface()).thenReturn(RocketMQInvoker.class);

        PowerMockito.doReturn(rocketMQProtocolServer).when(rocketMQProtocol, "openServer", registryUrl, "provider");
        PowerMockito.doReturn(Mockito.mock(DefaultMQPushConsumer.class)).when(rocketMQProtocolServer, "getDefaultMQPushConsumer");

        PowerMockito.when(rocketMQProtocol.export(invoker)).thenCallRealMethod();
    }

}
