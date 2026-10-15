package com.apexdrive.config;

import com.apexdrive.model.*;
import com.apexdrive.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * Database configuration configuring SQLite connection parameters and
 * automatically seeding the vehicle fleet if the database is fresh.
 * 
 * Demonstrates:
 * - Spring Bean Configuration
 * - JDBC connection pragma management (enforcing foreign keys in SQLite)
 * - Data seeding lifecycle (CommandLineRunner)
 */
@Configuration
public class DatabaseConfig {

    /**
     * Initializes SQLite connection pragmas (enabling foreign keys, WAL journal mode).
     */
    @Bean
    public CommandLineRunner initDatabasePragmas(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
                stmt.execute("PRAGMA journal_mode = WAL;");
            }
        };
    }

    /**
     * Seeds initial fleet of luxury, sports, electric, SUV and sedan vehicles
     * if the vehicle catalog in SQLite is empty on startup.
     */
    @Bean
    public CommandLineRunner seedInitialFleet(VehicleRepository vehicleRepository) {
        return args -> {
            if (vehicleRepository.count() > 0) {
                return; // Data already exists in apexdrive.db, retain persisted state
            }

            List<Vehicle> initialFleet = List.of(
                // 1. Porsche 911 GT3 RS (Sports)
                new SportsVehicle(
                    "Porsche 911 GT3 RS",
                    495.0,
                    "4.0L Naturally Aspirated Flat-6, 518 HP, 0-60 mph in 3.0s, Active DRS Aerodynamics",
                    "7-Speed PDK Dual-Clutch",
                    2,
                    "Premium Petrol",
                    "https://images.unsplash.com/photo-1614162692292-7ac56d7f7f1e?auto=format&fit=crop&w=1200&q=80",
                    true,
                    312,
                    true,
                    45.0
                ),

                // 2. Mercedes-Maybach S 680 (Luxury)
                new LuxuryVehicle(
                    "Mercedes-Maybach S 680",
                    550.0,
                    "6.0L Biturbo V12, 621 HP, Handcrafted Executive Rear Lounge, Burmester 4D Audio",
                    "9G-TRONIC 9-Speed Automatic",
                    4,
                    "Premium Petrol",
                    "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?auto=format&fit=crop&w=1200&q=80",
                    true,
                    75.0,
                    true
                ),

                // 3. Tesla Model S Plaid (Electric)
                new ElectricVehicle(
                    "Tesla Model S Plaid",
                    280.0,
                    "Tri-Motor All-Wheel Drive, 1,020 HP, 0-60 mph in 1.99s, Carbon-Sleeved Rotors",
                    "Single-Speed Fixed Gear",
                    5,
                    "https://images.unsplash.com/photo-1560958089-b8a1929cea89?auto=format&fit=crop&w=1200&q=80",
                    true,
                    600,
                    true
                ),

                // 4. BMW M4 Competition Coupe (Sports)
                new SportsVehicle(
                    "BMW M4 Competition",
                    340.0,
                    "3.0L M TwinPower Turbo Inline-6, 503 HP, M Carbon Bucket Seats, M Drive Professional",
                    "8-Speed M Steptronic",
                    4,
                    "Premium Petrol",
                    "https://images.unsplash.com/photo-1555215695-3004980ad54e?auto=format&fit=crop&w=1200&q=80",
                    true,
                    290,
                    true,
                    35.0
                ),

                // 5. Range Rover Autobiography (SUV)
                new Vehicle(
                    "Range Rover Autobiography",
                    VehicleCategory.SUV,
                    390.0,
                    "4.4L Twin-Turbocharged V8, 523 HP, Electronic Air Suspension with Dynamic Response Pro",
                    "8-Speed Automatic",
                    5,
                    "Mild Hybrid Petrol",
                    "https://images.unsplash.com/photo-1606016159991-dfe4f2746ad5?auto=format&fit=crop&w=1200&q=80",
                    true,
                    350.0
                ),

                // 6. Audi RS e-tron GT (Electric)
                new ElectricVehicle(
                    "Audi RS e-tron GT",
                    350.0,
                    "Dual Synchronous Motors, 637 HP Boost, 800-Volt Architecture, Quattro All-Wheel Drive",
                    "2-Speed Automatic",
                    5,
                    "https://images.unsplash.com/photo-1603584173870-7f23fdae1b7a?auto=format&fit=crop&w=1200&q=80",
                    true,
                    480,
                    true
                ),

                // 7. Rolls-Royce Ghost Series II (Luxury)
                new LuxuryVehicle(
                    "Rolls-Royce Ghost Series II",
                    890.0,
                    "6.75L Twin-Turbocharged V12, 563 HP, Planar Suspension System, Bespoke Starlight Headliner",
                    "8-Speed Satellite-Aided Automatic",
                    4,
                    "Premium Petrol",
                    "https://images.unsplash.com/photo-1631295868223-63265b40d9e4?auto=format&fit=crop&w=1200&q=80",
                    true,
                    120.0,
                    true
                ),

                // 8. Porsche Taycan Turbo S (Electric)
                new ElectricVehicle(
                    "Porsche Taycan Turbo S",
                    380.0,
                    "Overboost Launch Power 750 HP, Carbon Ceramic Brakes, Porsche Active Ride Suspension",
                    "2-Speed Rear Transmission",
                    4,
                    "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1200&q=80",
                    true,
                    460,
                    true
                ),

                // 9. Lamborghini Urus Performante (SUV)
                new SportsVehicle(
                    "Lamborghini Urus Performante",
                    590.0,
                    "4.0L Twin-Turbo V8, 657 HP, Carbon-Fiber Aerodynamics, Akrapovič Titanium Exhaust",
                    "8-Speed Automatic",
                    5,
                    "Premium Petrol",
                    "https://images.unsplash.com/photo-1544829099-b9a0c07fad1a?auto=format&fit=crop&w=1200&q=80",
                    true,
                    306,
                    true,
                    55.0
                ),

                // 10. BMW 760i xDrive Sedan (Sedan)
                new Vehicle(
                    "BMW 760i xDrive Sedan",
                    VehicleCategory.SEDAN,
                    310.0,
                    "4.4L BMW TwinPower Turbo V8 with 48V Mild Hybrid, 536 HP, 31.3-inch Theatre Screen",
                    "8-Speed Sport Automatic",
                    5,
                    "Mild Hybrid Petrol",
                    "https://images.unsplash.com/photo-1556189250-72ba954cfc2b?auto=format&fit=crop&w=1200&q=80",
                    true,
                    350.0
                ),

                // 11. Mercedes-Benz E 450 4MATIC (Sedan)
                new Vehicle(
                    "Mercedes-Benz E 450 4MATIC",
                    VehicleCategory.SEDAN,
                    195.0,
                    "3.0L Inline-6 Turbo with EQ Boost, 375 HP, MBUX Superscreen, AIRMATIC Air Suspension",
                    "9G-TRONIC 9-Speed",
                    5,
                    "Mild Hybrid Petrol",
                    "https://images.unsplash.com/photo-1617814076367-b759c7d7e738?auto=format&fit=crop&w=1200&q=80",
                    true,
                    300.0
                ),

                // 12. Cadillac Escalade V-Series (SUV)
                new Vehicle(
                    "Cadillac Escalade V-Series",
                    VehicleCategory.SUV,
                    420.0,
                    "6.2L Hand-Built Supercharged V8, 682 HP, 38-inch Curved OLED Display, AKG Studio Reference 36-Speaker",
                    "10-Speed Automatic",
                    7,
                    "Premium Petrol",
                    "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=1200&q=80",
                    true,
                    350.0
                )
            );

            vehicleRepository.saveAll(initialFleet);
            System.out.println(">>> ApexDrive Fleet Initialized: 12 premium vehicles successfully saved to SQLite database.");
        };
    }
}
