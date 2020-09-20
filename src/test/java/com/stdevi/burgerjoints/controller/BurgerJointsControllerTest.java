package com.stdevi.burgerjoints.controller;

import com.stdevi.burgerjoints.model.venue.Venue;
import com.stdevi.burgerjoints.service.VenueService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

class BurgerJointsControllerTest {

    @Test
    void getBurgerJointsNear_WhenGivenNearPlace_ReturnVenuesWithPhotos() {
        Venue v1 = new Venue("1", "Venue1", "https://fastly.4sqi.net/img/general/450x450/01.jpg");
        Venue v2 = new Venue("2", "Venue2", "https://fastly.4sqi.net/img/general/450x450/02.jpg");
        Venue v3 = new Venue("3", "Venue3", "https://fastly.4sqi.net/img/general/450x450/03.jpg");

        List<Venue> venues = new ArrayList<>();
        venues.add(v1);
        venues.add(v2);
        venues.add(v3);

        Flux<Venue> venueFlux = Flux.fromIterable(venues);

        VenueService venueService = mock(VenueService.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(venueService.getVenuesWithPhotoNear("Tartu")).thenReturn(venueFlux);

        BurgerJointsController controller = new BurgerJointsController(venueService);

        Flux<Venue> tartuVenues = controller.getBurgerJointsNear("Tartu");

        StepVerifier.create(tartuVenues.log())
                .expectNext(v1)
                .expectNext(v2)
                .expectNext(v3)
                .verifyComplete();
    }
}