package dev.aiddbot.abjavareact.rocket;

import java.time.LocalDate;

public record RocketResponse(
    Long id,
    String name,
    int capacity,
    int rangeKm,
    String status,
    LocalDate lastMaintenanceDate,
    LocalDate nextMaintenanceDate) {}
