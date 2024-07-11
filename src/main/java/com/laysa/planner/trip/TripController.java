package com.laysa.planner.trip;

import com.laysa.planner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private TripRepositorry tripRepositorry;

    @PostMapping
    public ResponseEntity<TripCreateResponse> criarTrips(@RequestBody TripRequestPayload payload) {
        Trip trip = new Trip(payload);
        this.tripRepositorry.save(trip);
        this.participantService.registerPartcipantToEvent(payload.emails_to_invite(), trip.getId());
        return ResponseEntity.ok(new TripCreateResponse(trip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDatails(@PathVariable UUID id) {
        Optional<Trip> trip = this.tripRepositorry.findById(id);
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
        Optional<Trip> trip = this.tripRepositorry.findById(id);
        if (trip.isPresent()) {
            Trip newTrip = trip.get();
            newTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            newTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            newTrip.setDestination(payload.destination());
            this.tripRepositorry.save(newTrip);
            return ResponseEntity.ok(newTrip);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
        Optional<Trip> trip = this.tripRepositorry.findById(id);
        if (trip.isPresent()) {
            Trip newTrip = trip.get();
            newTrip.setIsConfirmed(true);
            this.tripRepositorry.save(newTrip);
            this.participantService.triggerConfirmationEmailToParticipants(id);
            return ResponseEntity.ok(newTrip);
        }
        return ResponseEntity.notFound().build();
    }


}
