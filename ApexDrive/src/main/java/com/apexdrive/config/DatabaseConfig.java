package com.apexdrive.config;

import com.apexdrive.model.*;
import com.apexdrive.repository.VehicleRepository;
import org.springframework.jdbc.core.JdbcTemplate;
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

    /** Seeds an Indian-market fleet or migrates the original seed vehicles in place. */
    @Bean
    public CommandLineRunner seedInitialFleet(VehicleRepository vehicleRepository, JdbcTemplate jdbcTemplate) {
        return args -> {
            List<Vehicle> initialFleet = List.of(
                new Vehicle("Maruti Suzuki Swift", VehicleCategory.SEDAN, 1800.0,
                    "1.2L petrol hatchback, compact city-friendly cabin", "5-Speed Manual", 5, "Petrol",
                    "https://upload.wikimedia.org/wikipedia/commons/9/9a/2018_Suzuki_Swift_SZ5_Boosterjet_SHVS_1.0_Front.jpg", true, 250.0),
                new Vehicle("Hyundai Creta", VehicleCategory.SUV, 3500.0,
                    "Compact SUV with a spacious cabin and elevated driving position", "6-Speed Manual", 5, "Petrol",
                    "https://upload.wikimedia.org/wikipedia/commons/5/52/Moscow%2C_Hyundai_Creta_I_-_Yandex_streetview_cameramobile_Sept_2025_02.jpg", true, 250.0),
                new Vehicle("Tata Nexon", VehicleCategory.SUV, 2800.0,
                    "Compact SUV with a 1.2L turbo-petrol engine", "6-Speed Manual", 5, "Petrol",
                    "https://upload.wikimedia.org/wikipedia/commons/f/fa/Tata_Nexon_XM.jpg", true, 250.0),
                new Vehicle("Mahindra Thar", VehicleCategory.SUV, 4500.0,
                    "4x4 lifestyle SUV with a 2.0L turbo-petrol engine", "6-Speed Manual", 4, "Petrol",
                    "https://upload.wikimedia.org/wikipedia/commons/7/74/Mahindra_Thar_CRDi_%2816183560860%29.jpg", true, 200.0),
                new Vehicle("Toyota Innova Crysta", VehicleCategory.SUV, 5000.0,
                    "Multi-purpose family vehicle with three-row seating", "6-Speed Automatic", 7, "Diesel",
                    "https://upload.wikimedia.org/wikipedia/commons/7/7c/2023_Toyota_Zenix_2.0_v.jpg", true, 250.0),
                new Vehicle("Mahindra XUV700", VehicleCategory.SUV, 4500.0,
                    "Three-row SUV with a 2.0L turbo-petrol engine", "6-Speed Automatic", 7, "Petrol",
                    "https://upload.wikimedia.org/wikipedia/commons/b/ba/2021_Mahindra_XUV700_2.2_AX7_%28India%29_front_view.png", true, 250.0),
                new Vehicle("Maruti Suzuki Baleno", VehicleCategory.SEDAN, 2000.0,
                    "1.2L petrol premium hatchback", "5-Speed Manual", 5, "Petrol",
                    "https://upload.wikimedia.org/wikipedia/commons/9/96/2017_Suzuki_Baleno_SZ3_Dualjet_1.2_Front.jpg", true, 250.0),
                new Vehicle("Hyundai Venue", VehicleCategory.SUV, 2600.0,
                    "Subcompact SUV suited to city and highway trips", "6-Speed Manual", 5, "Petrol",
                    "https://upload.wikimedia.org/wikipedia/commons/d/d8/2019_Hyundai_Venue_Launch_Edition_%2848742702161%29.jpg", true, 250.0),
                new Vehicle("Tata Punch", VehicleCategory.SUV, 2200.0,
                    "Compact SUV with a 1.2L petrol engine", "5-Speed Manual", 5, "Petrol",
                    "https://upload.wikimedia.org/wikipedia/commons/1/1e/2021_Tata_Punch_Creative_%28India%29_front_view_01.png", true, 250.0),
                new Vehicle("Mahindra Scorpio-N", VehicleCategory.SUV, 4800.0,
                    "Body-on-frame SUV with available four-wheel drive", "6-Speed Manual", 7, "Diesel",
                    "https://upload.wikimedia.org/wikipedia/commons/7/7e/Mahindra_Scorpio_zipping_by.jpg", true, 250.0),
                new Vehicle("Toyota Fortuner", VehicleCategory.SUV, 7500.0,
                    "Full-size SUV with a 2.8L diesel engine", "6-Speed Automatic", 7, "Diesel",
                    "https://upload.wikimedia.org/wikipedia/commons/2/28/Toyota_Fortuner_car.jpg", true, 250.0),
                new Vehicle("Kia Seltos", VehicleCategory.SUV, 3200.0,
                    "Mid-size SUV with a comfortable five-seat cabin", "6-Speed Manual", 5, "Petrol",
                    "https://upload.wikimedia.org/wikipedia/commons/f/ff/Kia_Seltos_2024_3.jpg", true, 250.0)
            );

            if (vehicleRepository.count() == 0) {
                vehicleRepository.saveAll(initialFleet);
                System.out.println(">>> ApexDrive Indian-market fleet initialized in SQLite.");
                return;
            }

            List<String> legacyNames = List.of(
                "Porsche 911 GT3 RS", "Mercedes-Maybach S 680", "Tesla Model S Plaid",
                "BMW M4 Competition", "Range Rover Autobiography", "Audi RS e-tron GT",
                "Rolls-Royce Ghost Series II", "Porsche Taycan Turbo S", "Lamborghini Urus Performante",
                "BMW 760i xDrive Sedan", "Mercedes-Benz E 450 4MATIC", "Cadillac Escalade V-Series"
            );
            for (int index = 0; index < legacyNames.size(); index++) {
                Vehicle replacement = initialFleet.get(index);
                jdbcTemplate.update("""
                    UPDATE vehicles
                    SET vehicle_type = 'STANDARD', name = ?, category = ?, daily_price = ?,
                        specifications = ?, transmission = ?, seating_capacity = ?, fuel_type = ?,
                        image_url = ?, mileage_limit_km_per_day = ?
                    WHERE name = ?
                    """,
                    replacement.getName(), replacement.getCategory().name(), replacement.getDailyPrice(),
                    replacement.getSpecifications(), replacement.getTransmission(), replacement.getSeatingCapacity(),
                    replacement.getFuelType(), replacement.getImageUrl(), replacement.getMileageLimitKmPerDay(),
                    legacyNames.get(index)
                );
            }
            jdbcTemplate.update("UPDATE vehicles SET image_url = ? WHERE name = ? AND image_url = ?",
                initialFleet.get(9).getImageUrl(), "Mahindra Scorpio-N",
                "https://upload.wikimedia.org/wikipedia/commons/7/74/Mahindra_Thar_CRDi_%2816183560860%29.jpg");
            jdbcTemplate.update("UPDATE vehicles SET image_url = ? WHERE name = ? AND image_url = ?",
                initialFleet.get(11).getImageUrl(), "Kia Seltos",
                "https://upload.wikimedia.org/wikipedia/commons/5/52/Moscow%2C_Hyundai_Creta_I_-_Yandex_streetview_cameramobile_Sept_2025_02.jpg");
            System.out.println(">>> ApexDrive legacy seed vehicles migrated in place; persisted rows and bookings retained.");
        };
    }
}
