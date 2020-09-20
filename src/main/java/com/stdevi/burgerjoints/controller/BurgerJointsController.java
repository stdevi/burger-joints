package com.stdevi.burgerjoints.controller;

import com.stdevi.burgerjoints.model.venue.Venue;
import com.stdevi.burgerjoints.service.VenueService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/burgerjoints")
@AllArgsConstructor
public class BurgerJointsController {

    private final VenueService venueService;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping
    public Flux<Venue> getBurgerJointsNear(@RequestParam String place) {
        return venueService.getVenuesWithPhotoNear(place);
    }
}
