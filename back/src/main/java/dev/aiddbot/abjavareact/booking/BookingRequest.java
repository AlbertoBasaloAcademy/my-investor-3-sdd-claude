package dev.aiddbot.abjavareact.booking;

public record BookingRequest(
    Long launchId, String passengerName, String passengerEmail, String passengerPhone) {}
