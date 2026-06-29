package dev.aiddbot.abjavareact.rocket;

import java.time.LocalDate;

public record RocketRequest(
    String name,
    int capacity,
    RocketRange range,
    RocketStatus status,
    LocalDate lastMaintenanceDate,
    LocalDate nextMaintenanceDate) {}
