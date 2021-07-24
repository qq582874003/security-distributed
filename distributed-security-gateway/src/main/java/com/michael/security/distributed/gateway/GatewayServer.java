package com.michael.security.distributed.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * desc
 *
 * @author wangce 2021-07-09
 * @since 1.0.0
 */
@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class GatewayServer {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServer.class, args);
    }
}
