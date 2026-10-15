package com.apexdrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ApexDrive — Vehicle Rental Management System
 * Main Spring Boot Application Entry Point.
 * 
 * College Java OOP Project demonstrating:
 * - Object-Oriented Programming (Encapsulation, Inheritance, Polymorphism, Abstraction)
 * - Spring Boot REST APIs
 * - Real SQLite Database Persistence (apexdrive.db)
 * - Layered Architecture (Model, Repository, Service, Controller)
 */
@SpringBootApplication
public class ApexDriveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApexDriveApplication.class, args);
        System.out.println("==========================================================");
        System.out.println("  APEXDRIVE VEHICLE RENTAL MANAGEMENT SYSTEM STARTED!     ");
        System.out.println("  Access Web Application : http://localhost:8080         ");
        System.out.println("  API Health Endpoint    : http://localhost:8080/api/vehicles");
        System.out.println("  Database File          : apexdrive.db (SQLite)          ");
        System.out.println("==========================================================");
    }
}
