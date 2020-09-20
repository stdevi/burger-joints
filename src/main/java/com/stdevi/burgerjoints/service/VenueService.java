package com.stdevi.burgerjoints.service;

import com.stdevi.burgerjoints.model.venue.Venue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

public interface VenueService {

    Flux<Venue> getVenuesNear(String place);

    Mono<Tuple2<Venue, List<String>>> getPhotoUrlsPerVenue(Venue venue);

    Flux<Venue> getVenuesWithPhotoNear(String place);
}
