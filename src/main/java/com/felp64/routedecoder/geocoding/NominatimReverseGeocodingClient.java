package com.felp64.routedecoder.geocoding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class NominatimReverseGeocodingClient implements ReverseGeocodingClient {

    private final RestClient restClient;

    public NominatimReverseGeocodingClient(
            RestClient.Builder restClientBuilder,
            @Value("${geocoding.nominatim.base-url:https://nominatim.openstreetmap.org}") String baseUrl,
            @Value("${geocoding.nominatim.user-agent:RouteDecoder/1.0}") String userAgent
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .build();
    }

    @Override
    public String resolveAddress(String latitude, String longitude) {
        Map<?, ?> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse")
                        .queryParam("format", "jsonv2")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .build())
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("display_name") == null) {
            return "ADDRESS_NOT_FOUND";
        }
        return response.get("display_name").toString();
    }
}
