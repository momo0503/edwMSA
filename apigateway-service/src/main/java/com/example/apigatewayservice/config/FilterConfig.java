package com.example.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {

   // @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r -> r.path("/first-service/**")//사용자로부터 first-service 요청들어온다.
                        // 그럼 uri로 이동할텐데 중간에 request filter와 response filter헤더값에 각각 뭔가를 추가하는 작업임
                        .filters(f -> f.addRequestHeader("first-request","first-request-header")
                                .addResponseHeader("first-response","first-response-header"))
                        .uri("http://localhost:8081"))
                .route(r -> r.path("/second-service/**")
                        .filters(f -> f.addRequestHeader("second-request","second-request-header")
                                .addResponseHeader("second-response","second-response-header"))
                        .uri("http://localhost:8082"))
                .build();
    }

    //체이닝 가능하니까 바로 이어서 2 second-service 관련 route하나 추가.
    //application.yml에서 해야할걸 자바코드로 제어해봤다.
    //@만 뺴줘도 spring boot 에서 인식되지 않는다

}
