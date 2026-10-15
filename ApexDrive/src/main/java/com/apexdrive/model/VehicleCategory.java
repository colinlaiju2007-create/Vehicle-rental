package com.apexdrive.model;

/**
 * Enumeration representing distinct vehicle classifications.
 * Demonstrates type safety and domain modeling in Java OOP.
 */
public enum VehicleCategory {
    SEDAN("Sedan"),
    SUV("SUV"),
    LUXURY("Luxury"),
    SPORTS("Sports Performance"),
    ELECTRIC("Electric Vehicle");

    private final String displayName;

    VehicleCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
