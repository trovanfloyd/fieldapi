package com.yara.fielddataapi.infra.api.v1;

import com.yara.fielddataapi.infra.api.v1.request.PolygonRequest;
import com.yara.fielddataapi.infra.api.v1.response.PolygonResponse;
import com.yara.fielddataapi.infra.api.v1.response.WeatherHistoryResponse;
import io.netty.channel.ChannelOption;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class AgroMonitoringClient {

    @Value("${com.yara.fielddataapi.agromonitoring.host}")
    private String HOST;

    public Mono<List<WeatherHistoryResponse>> getWeatherHistory() {
        WebClient client = getWebClient();
        return client.get().uri(HOST).retrieve().bodyToMono(new ParameterizedTypeReference<List<WeatherHistoryResponse>>() {});
    }

    public Mono<PolygonResponse>  createPolygon(PolygonRequest polygonRequest) {
        WebClient client = getWebClient();
        return client.post().uri("http://api.agromonitoring.com/agro/1.0/polygons").body(polygonRequest, PolygonRequest.class).retrieve().bodyToMono(PolygonResponse.class);
    }

    private WebClient getWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        WebClient client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        return client;
    }
}
