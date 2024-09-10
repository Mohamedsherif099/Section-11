package com.eazybytes.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

    private Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Autowired
    private FilterUtility filterUtility;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        if (isCorrelationPresent(requestHeaders)){
            logger.debug("eazybank-correlation-id found in RequestTraceFilter: {}",
                    filterUtility.getCorrelationId(requestHeaders));
        }
        else {
            String correlationID = generateCorrelationID();
            exchange = filterUtility.setCorrelationId(exchange,correlationID);
            logger.debug("eazybank-correlation-id generated in RequestTraceFilter: {}",
                    correlationID);

        }

        return chain.filter(exchange);
    }

    private boolean isCorrelationPresent(HttpHeaders requestHeaders){
        if (filterUtility.getCorrelationId(requestHeaders)!=null){
            return true;
        }
        return false;

    }

    private String generateCorrelationID(){
        return UUID.randomUUID().toString();
    }
}
