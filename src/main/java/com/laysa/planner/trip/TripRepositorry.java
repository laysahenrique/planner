package com.laysa.planner.trip;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripRepositorry extends JpaRepository<Trip, UUID> {
}
