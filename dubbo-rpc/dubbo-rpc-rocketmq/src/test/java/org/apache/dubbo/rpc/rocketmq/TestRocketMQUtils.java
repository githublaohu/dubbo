package org.apache.dubbo.rpc.rocketmq;

import java.util.Map;

public class TestRocketMQUtils {
    public static String mapToString(Map<String, String> paramMap) {
        StringBuilder parameters = new StringBuilder();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            parameters.append(entry.getKey());
            parameters.append("=");
            parameters.append(entry.getValue());
            parameters.append("&;");
        }
        return parameters.toString();
    }

}
