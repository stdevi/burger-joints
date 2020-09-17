package com.stdevi.burgerjoints.client;

import com.stdevi.burgerjoints.model.VenuesSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class VenueClient {

    private final WebClient webClient;

    @Value("${foursquare.client.clientId}")
    private String clientId;

    @Value("${foursquare.client.clientSecret}")
    private String clientSecret;

    @Value("${foursquare.api.search.category}")
    private String categoryId;

    @Value("${foursquare.api.search.v}")
    private String v;

    public VenueClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.foursquare.com/v2/venues").build();
    }

    public Mono<VenuesSearchResponse> searchVenuesNear(String place) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("near", place)
                        .queryParam("categoryId", categoryId)
                        .queryParam("v", v)
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .build())
                .retrieve()
                .bodyToMono(VenuesSearchResponse.class);
    }
}
