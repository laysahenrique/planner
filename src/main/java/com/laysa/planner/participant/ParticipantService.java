package com.laysa.planner.participant;

import com.laysa.planner.trip.Participant;
import com.laysa.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public void registerPartcipantToEvent(List<String> participantToInvate, Trip trip) {
        List<Participant> participants = participantToInvate.stream().map(email -> new Participant(email, trip)).toList();
        participantRepository.saveAll(participants);
    }

    public ParticipantCreateResponse registerPartcipantToEvent(String email, Trip trip) {
        Participant nreParticipant = new Participant(email, trip);
        this.participantRepository.save(nreParticipant);
        return new ParticipantCreateResponse(nreParticipant.getId());
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId) {
    }

    public void triggerConfirmationEmailToParticipants(String email) {
    }

    public List<ParticipantData> getAllParticipantsFromEvent(UUID tripId) {
        return this.participantRepository
                .findByTripId(tripId)
                .stream()
                .map(participant ->
                        new ParticipantData(
                                participant.getId(),
                                participant.getName(),
                                participant.getEmail(),
                                participant.getIsConfirmed()
                        )
                ).toList();
    }
}
