package com.stdevi.burgerjoints.service;

import com.stdevi.burgerjoints.client.PhotoRecognitionClient;
import com.stdevi.burgerjoints.client.VenueClient;
import com.stdevi.burgerjoints.model.photo.PhotoResponse;
import com.stdevi.burgerjoints.model.recognition.PhotoUrlsWrapper;
import com.stdevi.burgerjoints.model.recognition.RecognitionResponse;
import com.stdevi.burgerjoints.model.venue.Venue;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BurgerJointsService implements VenueService {

    private static final String PHOTO_WIDTH = "450";
    private static final String PHOTO_HEIGHT = "450";

    private final VenueClient venueClient;
    private final PhotoRecognitionClient photoRecognitionClient;

    @Override
    public Flux<Venue> getVenuesNear(String place) {
        return venueClient.searchVenuesNear(place).flatMapMany(venuesSearchResponse ->
                Flux.fromIterable(venuesSearchResponse.getVenuesWrapper().getVenues()));
    }

    @Override
    public Mono<Tuple2<Venue, List<String>>> getPhotoUrlsPerVenue(Venue venue) {
        Mono<PhotoResponse> photoSearchResponse = venueClient.searchVenuesPhotos(venue.getId());
        Mono<List<String>> photos = photoSearchResponse.flatMap(response -> Mono.just(
                response.getPhotosWrapper().getPhotos().getItems().stream()
                        .sorted((o1, o2) -> Long.compare(o2.getCreatedAt(), o1.getCreatedAt()))
                        .map(photo -> photo.getUrl(PHOTO_WIDTH, PHOTO_HEIGHT))
                        .collect(Collectors.toList())
        ));

        return Mono.just(venue).zipWith(photos);
    }

    @Override
    public Flux<Venue> getVenuesWithPhotoNear(String place) {
        return getVenuesNear(place).flatMap(this::getPhotoUrlsPerVenue).flatMap(this::getVenueWithBurgerPhoto);
    }

    public Mono<Venue> getVenueWithBurgerPhoto(Tuple2<Venue, List<String>> venuePhotoUrls) {
        Venue venue = venuePhotoUrls.getT1();
        List<String> urls = venuePhotoUrls.getT2();

        Mono<RecognitionResponse> burgerPhotoResponse = photoRecognitionClient.findFirstBurgerPhoto(new PhotoUrlsWrapper(urls));

        return burgerPhotoResponse.zipWith(Mono.just(venue), (recognitionResponse, zipVenue) -> {
            zipVenue.setBurgerPhotoUrl(recognitionResponse.getUrlWithBurger());
            return zipVenue;
        });
    }
}
