package com.stdevi.burgerjoints.client;

import com.stdevi.burgerjoints.model.photo.PhotoResponse;
import com.stdevi.burgerjoints.model.venue.VenuesSearchResponse;
import com.stdevi.burgerjoints.utilities.ClientException;
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

    @Value("${foursquare.client.oauth_token}")
    private String oauth_token;

    @Value("${foursquare.api.search.category}")
    private String categoryId;

    @Value("${foursquare.api.search.v}")
    private String v;

    public VenueClient(WebClient.Builder webClientBuilder, @Value("${foursquare.api.baseUrl}") String baseURL) {
        this.webClient = webClientBuilder.baseUrl(baseURL).build();
    }

    public Mono<VenuesSearchResponse> searchVenuesNear(String place) {
        return this.webClient.get().uri(uriBuilder -> uriBuilder
                .path("/search")
                .queryParam("near", place)
                .queryParam("categoryId", categoryId)
                .queryParam("v", v)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .build())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(), clientResponse ->
                        clientResponse.bodyToMono(VenuesSearchResponse.class).flatMap(error ->
                                Mono.error(new ClientException(error.getMeta().getErrorDetail(), error.getMeta().getHttpStatus()))))
                .bodyToMono(VenuesSearchResponse.class);
    }

    public Mono<PhotoResponse> searchVenuesPhotos(String venueId) {
        return this.webClient.get().uri(uriBuilder -> uriBuilder
                .path(String.format("/%s/photos", venueId))
                .queryParam("v", v)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("oauth_token", oauth_token)
                .build())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(), clientResponse ->
                        clientResponse.bodyToMono(VenuesSearchResponse.class).flatMap(error ->
                                Mono.error(new ClientException(error.getMeta().getErrorDetail(), error.getMeta().getHttpStatus()))))
                .bodyToMono(PhotoResponse.class);
    }
}
