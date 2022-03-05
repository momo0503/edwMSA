package com.example.apigatewayservice.Filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter(){
        super(LoggingFilter.Config.class);
    }

    @Data
    public static class Config{
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }


    //apply는 실제 반환시킬 값은 GatewayFilter라는 값을 반환시켜주면 됩니다.
    @Override
    public GatewayFilter apply(LoggingFilter.Config config) {
        //두번 째인자로 Ordered.HIGHEST_PRECEDENCE 필터의 우선순위 정할수 있는거, HIGHEST로 줘서 실행 우선순위가 1
        //GLobalFilter아 CustomFilter보다 먼저 실행
        GatewayFilter filter = new OrderedGatewayFilter((exchange,chain) ->{
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("Logging filter baseMessage: {}",config.getBaseMessage());//{}안에 첫번째값 인자값 들어감.


            if(config.isPreLogger()){

                log.info("Logging PRE Filter : request Id -> {}",request.getId());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.isPostLogger()){
                    log.info("Logging POST Filter : response Code {}", response.getStatusCode());
                }
            }));

        }, Ordered.HIGHEST_PRECEDENCE);
        return filter;

    }


}
//    OrderedgatewayFilter를 컨트롤누르고 들어가보면
//        생성자 파라미터로 gatewayFilter랑 order 순서있고
//
//        filter라는 메서드가 있다. 필터가 해야하는 역할이 들어가있음
//        filter 메서드는 ServerWebExchange와 GatewayFilterChain 클래스를 담고있다.
//        현재 스프링 WebFlux 사용하고 있는데 기존의 스프링에서 MVC 패턴이용한 서블릿 리퀘스트
//        리스폰스로 웹프로그램이 했는데 스프링 5.0부터 새롭게 도입된 webflux라는 기능을 사용하면
//        이전에 MVC사용할수있지만 flux는 httpService과 request 지원하지않고
//        serverRequest response 사용하는데 그것을 사용하는 것을 도와주는데
//        ServerWebExchange 에요 , 이것으로부터 우리가 필요한 request , response 얻어올거고
//        GatewayFilterChain 의역할은 다양한 필터들 작업
