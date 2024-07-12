package com.laysa.planner.trip;

import com.laysa.planner.participant.ParticipantCreateResponse;
import com.laysa.planner.participant.ParticipantData;
import com.laysa.planner.participant.ParticipantPayload;
import com.laysa.planner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private TripRepository tripRepositorry;

    @PostMapping
    public ResponseEntity<TripCreateResponse> criarTrips(@RequestBody TripRequestPayload payload) {
        Trip trip = new Trip(payload);
        this.tripRepositorry.save(trip);
        this.participantService.registerPartcipantToEvent(payload.emails_to_invite(), trip);
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

    @PutMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantPayload payload) {
        Optional<Trip> trip = this.tripRepositorry.findById(id);

        if (trip.isPresent()) {
            Trip newTrip = trip.get();
            ParticipantCreateResponse participantCreateResponse =
                    this.participantService.registerPartcipantToEvent(payload.email(), newTrip);

            if (newTrip.getIsConfirmed()) {
                this.participantService.triggerConfirmationEmailToParticipants(payload.email());
            }
            return ResponseEntity.ok(participantCreateResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id) {
        return ResponseEntity.ok(this.participantService.getAllParticipantsFromEvent(id));
    }
}
