package com.apexdrive.model;

/**
 * Interface defining contract for rental calculation and fleet perks.
 * Demonstrates the OOP concept of Abstraction and Polymorphism.
 */
public interface RentalCalculable {

    /**
     * Calculates the total rental price given the duration in days.
     * Overridden polymorphically by specialized vehicle subclasses.
     *
     * @param rentalDays Total duration of rental (minimum 1)
     * @return Calculated total cost in USD / currency
     */
    double calculateRentalCost(long rentalDays);

    /**
     * Returns special perks or features specific to this vehicle type.
     *
     * @return Description of perks
     */
    String getSpecialPerks();
}
