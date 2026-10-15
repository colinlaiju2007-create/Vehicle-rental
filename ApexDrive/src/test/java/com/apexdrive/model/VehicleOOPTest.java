package com.apexdrive.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests demonstrating and validating Java OOP Principles:
 * - Encapsulation
 * - Inheritance
 * - Polymorphism
 * - Abstraction
 */
class VehicleOOPTest {

    @Test
    @DisplayName("OOP Encapsulation: State integrity and defensive setters")
    void testEncapsulationAndValidation() {
        Vehicle vehicle = new Vehicle();
        vehicle.setName("Apex Test Sedan");
        vehicle.setDailyPrice(150.0);
        vehicle.setCategory(VehicleCategory.SEDAN);

        assertEquals("Apex Test Sedan", vehicle.getName());
        assertEquals(150.0, vehicle.getDailyPrice());
        assertEquals(VehicleCategory.SEDAN, vehicle.getCategory());

        // Negative price rejection (Encapsulation invariant)
        assertThrows(IllegalArgumentException.class, () -> vehicle.setDailyPrice(-50.0));
    }

    @Test
    @DisplayName("OOP Polymorphism: Standard vehicle rental cost and weekly discount")
    void testStandardVehiclePolymorphicCost() {
        Vehicle sedan = new Vehicle("BMW 760i", VehicleCategory.SEDAN, 200.0,
                "TwinPower Turbo", "Auto", 5, "Hybrid", "test.jpg", true, 300.0);

        // 3 days: 200 * 3 = 600.0
        assertEquals(600.0, sedan.calculateRentalCost(3));

        // 7 days: 200 * 7 = 1400.0 - 5% discount = 1330.0
        assertEquals(1330.0, sedan.calculateRentalCost(7));
    }

    @Test
    @DisplayName("OOP Polymorphism & Inheritance: LuxuryVehicle VIP concierge & discount")
    void testLuxuryVehiclePolymorphicCost() {
        LuxuryVehicle maybach = new LuxuryVehicle(
                "Mercedes-Maybach S 680", 500.0, "V12 Biturbo",
                "Automatic", 4, "Petrol", "maybach.jpg", true, 50.0, true
        );

        // Inherited methods
        assertEquals(VehicleCategory.LUXURY, maybach.getCategory());
        assertEquals("LUXURY", maybach.getVehicleType());
        assertTrue(maybach.getChauffeurAvailable());

        // Polymorphic cost calculation:
        // 2 days: (500 * 2) + 50 concierge = 1050.0
        assertEquals(1050.0, maybach.calculateRentalCost(2));

        // 5 days: (500 * 5 * 0.90) + 50 concierge = 2250 + 50 = 2300.0
        assertEquals(2300.0, maybach.calculateRentalCost(5));
    }

    @Test
    @DisplayName("OOP Polymorphism & Inheritance: SportsVehicle telemetry & track insurance")
    void testSportsVehiclePolymorphicCost() {
        SportsVehicle porsche = new SportsVehicle(
                "Porsche 911 GT3 RS", 400.0, "Flat-6",
                "PDK", 2, "Petrol", "porsche.jpg", true, 312, true, 40.0
        );

        assertEquals("SPORTS", porsche.getVehicleType());
        assertEquals(312, porsche.getTopSpeedKph());

        // 2 days: (400 * 2) + 40 insurance = 840.0
        assertEquals(840.0, porsche.calculateRentalCost(2));

        // 4 days: (400 * 4 * 0.92) + 40 = 1472 + 40 = 1512.0
        assertEquals(1512.0, porsche.calculateRentalCost(4));
    }

    @Test
    @DisplayName("OOP Polymorphism & Inheritance: ElectricVehicle green rebate & perks")
    void testElectricVehiclePolymorphicCost() {
        ElectricVehicle tesla = new ElectricVehicle(
                "Tesla Model S Plaid", 300.0, "Tri-Motor",
                "Direct", 5, "tesla.jpg", true, 600, true
        );

        assertEquals("ELECTRIC", tesla.getVehicleType());
        assertEquals(600, tesla.getBatteryRangeKm());

        // 2 days: base = 600, minus 7% eco rebate (42) = 558.0
        assertEquals(558.0, tesla.calculateRentalCost(2));
        assertTrue(tesla.getSpecialPerks().contains("Unlimited Supercharging"));
    }
}
