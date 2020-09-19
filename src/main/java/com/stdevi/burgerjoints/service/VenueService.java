package com.stdevi.burgerjoints.service;

import com.stdevi.burgerjoints.model.venue.Venue;
import reactor.core.publisher.Flux;

public interface VenueService {

    Flux<Venue> getBurgerJointsNear(String place);
}
