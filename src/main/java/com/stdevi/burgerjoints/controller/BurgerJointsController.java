package com.stdevi.burgerjoints.controller;

import com.stdevi.burgerjoints.model.venue.Venue;
import com.stdevi.burgerjoints.service.BurgerJointsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/burgerjoints")
@AllArgsConstructor
public class BurgerJointsController {

    private final BurgerJointsService burgerJointsService;

    @GetMapping
    public Flux<Venue> getBurgerJointsNear(@RequestParam String place) {
        return burgerJointsService.getBurgerJointsNear(place);
    }
}
