package com.stdevi.burgerjoints.client;

import com.stdevi.burgerjoints.model.recognition.PhotoUrlsWrapper;
import com.stdevi.burgerjoints.model.recognition.RecognitionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PhotoRecognitionClient {

    private final WebClient webClient;

    public PhotoRecognitionClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://pplkdijj76.execute-api.eu-west-1.amazonaws.com/prod/recognize").build();
    }

    public Mono<RecognitionResponse> findFirstBurgerPhoto(PhotoUrlsWrapper photoUrlsWrapper) {
        return this.webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(photoUrlsWrapper), PhotoUrlsWrapper.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .bodyToMono(RecognitionResponse.class);
    }
}
