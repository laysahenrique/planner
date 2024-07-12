package com.laysa.planner.participant;

import com.laysa.planner.trip.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantPayload participantPayload) {
        Optional<Participant> participant = this.participantRepository.findById(id);
        if (participant.isPresent()) {
            Participant newParticipant = participant.get();
            newParticipant.setIsConfirmed(true);
            newParticipant.setName(participantPayload.name());
            participantRepository.save(newParticipant);
            return ResponseEntity.ok(participant.get());
        }
        return ResponseEntity.notFound().build();
    }

}
