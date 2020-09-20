package com.stdevi.burgerjoints.service;

import com.stdevi.burgerjoints.client.PhotoRecognitionClient;
import com.stdevi.burgerjoints.client.VenueClient;
import com.stdevi.burgerjoints.model.venue.Venue;
import com.stdevi.burgerjoints.utilities.ClientException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@ExtendWith(MockitoExtension.class)
class BurgerJointsServiceTest {

    private BurgerJointsService burgerJointsService;

    @Test
    void getVenuesNear_WhenGivenTwoVenues_thenShouldReturnTwoVenues() {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(clientRequest ->
                Mono.just(ClientResponse.create(HttpStatus.OK)
                        .header("content-type", "application/json")
                        .body(buildBodyWithTwoVenues())
                        .build())
        );

        VenueClient venueClient = new VenueClient(builder, null);
        burgerJointsService = new BurgerJointsService(venueClient, null);

        Flux<Venue> venueFlux = burgerJointsService.getVenuesNear("Place");

        StepVerifier.create(venueFlux.log())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getVenuesNear_WhenGivenTwoVenues_thenFirstVenueShouldHaveCorrectData() {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(clientRequest ->
                Mono.just(ClientResponse.create(HttpStatus.OK)
                        .header("content-type", "application/json")
                        .body(buildBodyWithTwoVenues())
                        .build())
        );

        VenueClient venueClient = new VenueClient(builder, null);
        burgerJointsService = new BurgerJointsService(venueClient, null);

        Flux<Venue> venueFlux = burgerJointsService.getVenuesNear("Place");

        StepVerifier.create(venueFlux.log())
                .assertNext(venue -> {
                    assertEquals("1", venue.getId());
                    assertEquals("joint1", venue.getName());
                    assertNull(venue.getBurgerPhotoUrl());
                })
                .assertNext(venue -> {
                    assertEquals("2", venue.getId());
                    assertEquals("joint2", venue.getName());
                    assertNull(venue.getBurgerPhotoUrl());
                })
                .verifyComplete();
    }

    @Test
    void getVenuesNear_WhenClientReturnStatus400_thenThrowClientException() {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(clientRequest ->
                Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST)
                        .header("content-type", "application/json")
                        .body(buildVenueSearchBadRequestBody())
                        .build())
        );

        VenueClient venueClient = new VenueClient(builder, null);
        burgerJointsService = new BurgerJointsService(venueClient, null);

        Flux<Venue> venueFlux = burgerJointsService.getVenuesNear("Place");

        StepVerifier.create(venueFlux.log()).expectErrorSatisfies(throwable -> {
            ClientException clientException = (ClientException) throwable;
            assertEquals(HttpStatus.BAD_REQUEST, clientException.getStatus());
            assertEquals("Couldn't geocode param near: testPlace", clientException.getMessage());
        }).verify();
    }

    @Test
    void getPhotoUrlsPerVenue_WhenGivenVenue_thenShouldReturnVenueAndPhotoUrls() {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(clientRequest ->
                Mono.just(ClientResponse.create(HttpStatus.OK)
                        .header("content-type", "application/json")
                        .body(buildVenuePhotosResponse())
                        .build())
        );

        VenueClient venueClient = new VenueClient(builder, null);
        burgerJointsService = new BurgerJointsService(venueClient, null);

        Venue venue = new Venue("42", "BurgerJoint", null);
        Mono<Tuple2<Venue, List<String>>> photoUrlsPerVenue = burgerJointsService.getPhotoUrlsPerVenue(venue);

        StepVerifier.create(photoUrlsPerVenue.log()).assertNext(tuple -> {
            assertEquals("42", tuple.getT1().getId());
            assertEquals(2, tuple.getT2().size());
            assertEquals("https://fastly.4sqi.net/img/general/450x450/01.jpg", tuple.getT2().get(0));
        }).verifyComplete();
    }

    @Test
    void getPhotoUrlsPerVenue_WhenClientReturnStatus400_thenThrowClientException() {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(clientRequest ->
                Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST)
                        .header("content-type", "application/json")
                        .body(buildPhotoUrlSearchBadRequestBody())
                        .build())
        );

        VenueClient venueClient = new VenueClient(builder, null);
        burgerJointsService = new BurgerJointsService(venueClient, null);

        Venue venue = new Venue("rrr", "BurgerJoint", null);

        Mono<Tuple2<Venue, List<String>>> photoUrlsPerVenue = burgerJointsService.getPhotoUrlsPerVenue(venue);

        StepVerifier.create(photoUrlsPerVenue.log()).expectErrorSatisfies(throwable -> {
            ClientException clientException = (ClientException) throwable;
            assertEquals(HttpStatus.BAD_REQUEST, clientException.getStatus());
            assertEquals("Value rrr is invalid for venue id", clientException.getMessage());
        }).verify();
    }

    @Test
    void getVenueWithBurgerPhoto_WhenGivenRecognizedBurgerPhoto_thenShouldReturnVenueWithPhotoUrl() {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(clientRequest ->
                Mono.just(ClientResponse.create(HttpStatus.OK)
                        .header("content-type", "application/json")
                        .body(buildUrlWithBurgerResponse())
                        .build())
        );

        PhotoRecognitionClient photoRecognitionClient = new PhotoRecognitionClient(builder, null);
        burgerJointsService = new BurgerJointsService(null, photoRecognitionClient);

        Venue venue = new Venue("42", "BurgerJoint", null);
        List<String> photoUrls = new ArrayList<>();
        photoUrls.add("https://fastly.4sqi.net/img/general/450x450/01.jpg");
        photoUrls.add("https://fastly.4sqi.net/img/general/450x450/burger.jpg");
        photoUrls.add("https://fastly.4sqi.net/img/general/450x450/02.jpg");

        Tuple2<Venue, List<String>> tuple = Mono.just(venue).zipWith(Mono.just(photoUrls)).block();
        Mono<Venue> venueWithBurgerPhoto = burgerJointsService.getVenueWithBurgerPhoto(tuple);

        StepVerifier.create(venueWithBurgerPhoto.log()).assertNext(assertVenue -> {
            assertEquals("42", assertVenue.getId());
            assertEquals("BurgerJoint", assertVenue.getName());
            assertEquals("https://fastly.4sqi.net/img/general/450x450/burger.jpg", assertVenue.getBurgerPhotoUrl());
        }).verifyComplete();
    }

    @Test
    void getVenueWithBurgerPhoto_WhenGivenRecognitionError_thenShouldReturnVenueWithoutPhotoUrl() {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(clientRequest ->
                Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST)
                        .header("content-type", "application/json")
                        .body(buildNoBurgerResponse())
                        .build())
        );

        PhotoRecognitionClient photoRecognitionClient = new PhotoRecognitionClient(builder, null);
        burgerJointsService = new BurgerJointsService(null, photoRecognitionClient);

        Venue venue = new Venue("42", "BurgerJoint", null);
        List<String> photoUrls = new ArrayList<>();
        photoUrls.add("https://fastly.4sqi.net/img/general/450x450/01.jpg");
        photoUrls.add("https://fastly.4sqi.net/img/general/450x450/02.jpg");

        Tuple2<Venue, List<String>> tuple = Mono.just(venue).zipWith(Mono.just(photoUrls)).block();
        Mono<Venue> venueWithBurgerPhoto = burgerJointsService.getVenueWithBurgerPhoto(tuple);

        StepVerifier.create(venueWithBurgerPhoto).assertNext(assertVenue -> {
            assertEquals("42", assertVenue.getId());
            assertEquals("BurgerJoint", assertVenue.getName());
            assertNull(assertVenue.getBurgerPhotoUrl());
        }).verifyComplete();
    }

    @Test
    void getVenueWithBurgerPhoto_WhenGivenInvalidBody_thenShouldReturnVenueWithoutPhotoUrl() {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(clientRequest ->
                Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST)
                        .header("content-type", "application/json")
                        .body(buildInvalidBurgerUrlsResponse())
                        .build())
        );

        PhotoRecognitionClient photoRecognitionClient = new PhotoRecognitionClient(builder, null);
        burgerJointsService = new BurgerJointsService(null, photoRecognitionClient);

        Venue venue = new Venue("42", "BurgerJoint", null);
        List<String> photoUrls = new ArrayList<>();

        Tuple2<Venue, List<String>> tuple = Mono.just(venue).zipWith(Mono.just(photoUrls)).block();
        Mono<Venue> venueWithBurgerPhoto = burgerJointsService.getVenueWithBurgerPhoto(tuple);

        StepVerifier.create(venueWithBurgerPhoto.log()).assertNext(asssertVenue -> {
            assertEquals("42", asssertVenue.getId());
            assertEquals("BurgerJoint", asssertVenue.getName());
            assertNull(asssertVenue.getBurgerPhotoUrl());
        }).verifyComplete();
    }

    @Test
    void getVenuesWithPhotoNear_WhenGivenPlace_thenShouldReturnVenueWithoutBurgerPhoto() {
        WebClient.Builder venueBuilder = WebClient.builder().exchangeFunction(clientRequest -> {
                    if (clientRequest.url().toString().equals("/search?near=Tartu&categoryId&v&client_id&client_secret")) {
                        return Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("content-type", "application/json")
                                .body(buildBodyWithTwoVenues())
                                .build());
                    } else {
                        return Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("content-type", "application/json")
                                .body(buildVenuePhotosResponse())
                                .build());
                    }
                }
        );

        WebClient.Builder photoRecognitionBuilder = WebClient.builder().exchangeFunction(clientRequest ->
                Mono.just(ClientResponse.create(HttpStatus.OK)
                        .header("content-type", "application/json")
                        .body(buildUrlWithBurgerResponse())
                        .build())
        );

        VenueClient venueClient = new VenueClient(venueBuilder, null);
        PhotoRecognitionClient photoRecognitionClient = new PhotoRecognitionClient(photoRecognitionBuilder, null);
        burgerJointsService = new BurgerJointsService(venueClient, photoRecognitionClient);

        Flux<Venue> tartuVenues = burgerJointsService.getVenuesWithPhotoNear("Tartu");

        StepVerifier.create(tartuVenues.log())
                .assertNext(venue -> {
                    assertEquals("1", venue.getId());
                    assertEquals("joint1", venue.getName());
                    assertEquals("https://fastly.4sqi.net/img/general/450x450/burger.jpg", venue.getBurgerPhotoUrl());
                })
                .assertNext(venue -> {
                    assertEquals("2", venue.getId());
                    assertEquals("joint2", venue.getName());
                    assertEquals("https://fastly.4sqi.net/img/general/450x450/burger.jpg", venue.getBurgerPhotoUrl());
                })
                .verifyComplete();
    }

    private String buildBodyWithTwoVenues() {
        JSONObject body = new JSONObject();

        try {
            JSONObject meta = new JSONObject();
            meta.put("code", 200);

            JSONObject response = new JSONObject();
            JSONArray venues = new JSONArray();

            JSONObject burgerJoint1 = new JSONObject();
            burgerJoint1.put("id", "1");
            burgerJoint1.put("name", "joint1");

            JSONObject burgerJoint2 = new JSONObject();
            burgerJoint2.put("id", "2");
            burgerJoint2.put("name", "joint2");

            venues.put(burgerJoint1);
            venues.put(burgerJoint2);

            response.put("venues", venues);

            body.put("meta", meta);
            body.put("response", response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return body.toString();
    }

    private String buildVenueSearchBadRequestBody() {
        JSONObject body = new JSONObject();

        try {
            JSONObject meta = new JSONObject();
            meta.put("code", 400);
            meta.put("errorType", "failed_geocode");
            meta.put("errorDetail", "Couldn't geocode param near: testPlace");

            JSONObject response = new JSONObject();

            body.put("meta", meta);
            body.put("response", response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return body.toString();
    }

    private String buildVenuePhotosResponse() {
        JSONObject body = new JSONObject();

        try {
            JSONObject meta = new JSONObject();
            meta.put("code", 200);

            JSONObject response = new JSONObject();
            JSONObject photos = new JSONObject();
            JSONArray items = new JSONArray();

            JSONObject item1 = new JSONObject();
            item1.put("createdAt", 1597503995);
            item1.put("prefix", "https://fastly.4sqi.net/img/general/");
            item1.put("suffix", "/01.jpg");

            JSONObject item2 = new JSONObject();
            item2.put("createdAt", 1597503889);
            item2.put("prefix", "https://fastly.4sqi.net/img/general/");
            item2.put("suffix", "/00.jpg");

            items.put(item1);
            items.put(item2);

            photos.put("items", items);
            response.put("photos", photos);

            body.put("meta", meta);
            body.put("response", response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return body.toString();
    }

    private String buildPhotoUrlSearchBadRequestBody() {
        JSONObject body = new JSONObject();

        try {
            JSONObject meta = new JSONObject();
            meta.put("code", 400);
            meta.put("errorType", "param_error");
            meta.put("errorDetail", "Value rrr is invalid for venue id");

            JSONObject response = new JSONObject();

            body.put("meta", meta);
            body.put("response", response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return body.toString();
    }

    private String buildUrlWithBurgerResponse() {
        JSONObject body = new JSONObject();

        try {
            body.put("urlWithBurger", "https://fastly.4sqi.net/img/general/450x450/burger.jpg");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return body.toString();
    }

    private String buildNoBurgerResponse() {
        JSONObject body = new JSONObject();

        try {
            body.put("error", "No Burger For You");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return body.toString();
    }

    private String buildInvalidBurgerUrlsResponse() {
        JSONObject body = new JSONObject();

        try {
            body.put("error", "Invalid body");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return body.toString();
    }
}