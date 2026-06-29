package dev.aiddbot.abjavareact.rocket;

import java.time.LocalDate;

public record RocketRequest(
    String name,
    int capacity,
    int rangeKm,
    RocketStatus status,
    LocalDate lastMaintenanceDate,
    LocalDate nextMaintenanceDate) {}
