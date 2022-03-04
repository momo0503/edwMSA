package com.example.apigatewayservice.Filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter(){
        super(Config.class);
    }

    public static class Config{
        //Put the Configuration properties
    }

    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("Custom PRE filter : request id -> {}",request.getId());//{}안에 첫번째값 request.getId()가 쏙 들어감.


            //Custom post filter , 필터의 매개변수로써 exchange객체 , 그리고나서 필터가 잘 정의된 다음에 반환값으로 ~~주겠다. then(Mono~
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST filter : response code -> {}",response.getStatusCode());
            }));
        };
    }

    //GatewayFilter라는 오브젝트,빈을 등록할건데 prefilter와 postfilter를 나눠서 작업합니다.
    // 람다식의 인자값으로 exhange객체, chain객체 두가지를 받고  exchange로부터 request,response 얻을수 있어요
    // post filter에서는 chain 에다가 postfilter exchange 추가하고 Mono라는  웹플럭스라고 스프링 5에서 추가하는데,기존의 동기방식이 아닌
    //비동기 방식으로 서버를 지원할때 단일값 전달할때 Mono타입으로 전달하시면됩니다.


}
