# ApexDrive — Vehicle Rental Management System

> **College Java OOP Project — B.Tech Computer Science & Engineering**  
> Built with **Java 21**, **Spring Boot 3.2**, **Spring Data JPA**, **SQLite (`apexdrive.db`)**, and modern automotive web UI (HTML5, CSS3, Vanilla ES6+ JavaScript).

---

## 🏎️ 1. Project Overview

**ApexDrive** is a production-grade, full-stack vehicle rental management platform engineered specifically to showcase authentic **Java Object-Oriented Programming (OOP)** concepts, secure layered architecture, and real SQLite relational database persistence.

Unlike naive projects that simulate persistence with client-side `localStorage`, ApexDrive performs all fleet queries, dynamic pricing computations, customer validations, and calendar conflict checks on the **Java 21 backend**.

### Core Capabilities
* **Fleet Catalog & Dynamic Filtering**: Filter vehicles in real-time by category (*Sports, Luxury, Electric, SUV, Sedan*), maximum daily price, and calendar date availability.
* **Intelligent Overlap Prevention**: Evaluates existing non-cancelled reservations and prevents double-booking a car during overlapping date ranges.
* **Polymorphic Rate Engine**: Special vehicle subclasses (*Luxury, Sports, Electric*) override base calculation logic to inject VIP concierge fees, eco-rebates, telemetry insurance, and weekly duration discounts.
* **Instant Backend Quotes**: Live date adjustments trigger `/api/bookings/calculate-price` to preview duration, base fees, discounts, and perks before committing.
* **Permanent SQLite Storage**: All fleet modifications and customer reservations are saved to the persistent `apexdrive.db` file. Bookings persist across application and laptop restarts.
* **Cryptographic Reference Codes**: Generates unique, memorable reservation codes (e.g. `APX-8C2A-9E3F`).
* **Customer Privacy Enforcement**: Booking records and cancellation operations require verification of both the unique booking reference and the customer's matching email address.
* **Administrative Fleet Portal**: Authenticated admin dashboard with fleet metrics, gross revenue analytics, reservation auditing/cancellation, and vehicle management (add, edit, toggle availability, delete).
* **Simulated Payment Gateway**: Clear academic demonstration of card and pickup billing without storing sensitive customer credentials.

---

## 🏛️ 2. Java OOP Architecture & Viva Presentation Guide

For your college presentation or viva examination, here is how each core OOP principle is implemented in this codebase:

```
                          ┌──────────────────────────┐
                          │   <<interface>>          │
                          │     RentalCalculable     │
                          └─────────────▲────────────┘
                                        │ implements
                          ┌─────────────┴────────────┐
                          │        Vehicle           │  (Encapsulation & Base Properties)
                          └──────▲──────▲──────▲─────┘
                                 │      │      │ extends (Inheritance)
            ┌────────────────────┘      │      └───────────────────┐
            │                           │                          │
┌───────────┴──────────┐   ┌────────────┴────────┐   ┌─────────────┴──────────┐
│    LuxuryVehicle     │   │    SportsVehicle    │   │     ElectricVehicle    │
├──────────────────────┤   ├─────────────────────┤   ├────────────────────────┤
│ + conciergeFee       │   │ + topSpeedKph       │   │ + batteryRangeKm       │
│ + calculateRentalCost│   │ + calculateRentalCost│  │ + calculateRentalCost  │
└──────────────────────┘   └─────────────────────┘   └────────────────────────┘
                       (Polymorphic Pricing Overrides)
```

### 1. Encapsulation
* **Definition**: Bundling data and methods operating on that data within a class while restricting direct access from outside.
* **Implementation**:
  * In `Vehicle.java`, `Booking.java`, and `Customer.java`, all internal fields (e.g., `dailyPrice`, `status`, `customerEmail`) are marked `private`.
  * Access is mediated through `public` getters and defensive setters that guard against illegal states (e.g., rejecting negative daily prices in `Vehicle.setDailyPrice()`).
  * In `Booking.java`, the `cancelBooking(reason)` method encapsulates state mutation, ensuring status cannot be cancelled more than once.

### 2. Inheritance
* **Definition**: Mechanism where a child class acquires attributes and behaviors from a parent class, promoting code reuse.
* **Implementation**:
  * `Vehicle.java` is the superclass containing common fleet attributes: `name`, `category`, `dailyPrice`, `specifications`, `transmission`, `seatingCapacity`, and `fuelType`.
  * `LuxuryVehicle.java`, `SportsVehicle.java`, and `ElectricVehicle.java` `extend Vehicle` to inherit all core properties and add specialized attributes:
    * `LuxuryVehicle`: adds `conciergeFee` and `chauffeurAvailable`.
    * `SportsVehicle`: adds `topSpeedKph`, `trackPackage`, and `performanceInsuranceFee`.
    * `ElectricVehicle`: adds `batteryRangeKm`, `fastChargingSupport`, and `ecoDiscountPct`.

### 3. Polymorphism
* **Definition**: The ability of an object to take many forms and execute subtype-specific behaviors through a shared interface or superclass method.
* **Implementation**:
  * The `RentalCalculable` interface declares `double calculateRentalCost(long rentalDays)`.
  * `Vehicle.java` provides the baseline calculation with standard weekly volume discounts.
  * Subclasses provide polymorphic overrides (`@Override`):
    * `LuxuryVehicle`: applies VIP concierge preparation fees and a 10% long-term loyalty discount.
    * `SportsVehicle`: applies telemetry track insurance and an 8% multi-day discount.
    * `ElectricVehicle`: dynamically deducts a 7% green energy environmental rebate and additional duration savings.
  * When the service layer invokes `vehicle.calculateRentalCost(days)`, Java's dynamic method dispatch automatically calls the correct subclass algorithm at runtime without needing complex `if-else` type inspections!

### 4. Abstraction
* **Definition**: Hiding low-level implementation details and exposing only essential contracts to consumers.
* **Implementation**:
  * Interfaces `VehicleService` and `BookingService` define the business contracts, while `VehicleServiceImpl` and `BookingServiceImpl` encapsulate the JPA database queries, concurrency checks, and email validations.
  * `RentalCalculable` defines the pricing contract independently of how each car calculates its cost.

---

## 🗄️ 3. Real SQLite Database Details

* **Database Engine**: SQLite 3
* **Database File**: `apexdrive.db` (created automatically in the project root directory on first launch)
* **ORM Layer**: Spring Data JPA + Hibernate with `SQLiteDialect`
* **Integrity Enforcement**: `PRAGMA foreign_keys = ON;` and `PRAGMA journal_mode = WAL;` enabled in `DatabaseConfig.java`.

### Database Tables

#### Table: `vehicles`
| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | Unique Vehicle ID |
| `vehicle_type` | VARCHAR | JPA Discriminator (`STANDARD`, `LUXURY`, `SPORTS`, `ELECTRIC`) |
| `name` | VARCHAR | Model Name (e.g. *Porsche 911 GT3 RS*) |
| `category` | VARCHAR | `SPORTS`, `LUXURY`, `ELECTRIC`, `SUV`, `SEDAN` |
| `daily_price` | DOUBLE | Base daily rental fee |
| `specifications` | TEXT | Powertrain and performance notes |
| `transmission` | VARCHAR | *Automatic*, *PDK Dual-Clutch*, etc. |
| `seating_capacity` | INTEGER | Cabin passenger capacity |
| `fuel_type` | VARCHAR | *Petrol*, *Hybrid*, *Electric* |
| `image_url` | VARCHAR | High-definition automotive photograph |
| `available` | BOOLEAN | Master fleet status (Active vs Decommissioned) |
| `mileage_limit_km_per_day` | DOUBLE | Daily included mileage |
| `concierge_fee` | DOUBLE | Luxury subtype field |
| `chauffeur_available` | BOOLEAN | Luxury subtype field |
| `top_speed_kph` | INTEGER | Sports subtype field |
| `track_package` | BOOLEAN | Sports subtype field |
| `battery_range_km` | INTEGER | Electric subtype field |
| `fast_charging_support` | BOOLEAN | Electric subtype field |

#### Table: `customers`
| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | Customer ID |
| `full_name` | VARCHAR | Full Name |
| `email` | VARCHAR | Verified email (indexed) |
| `phone` | VARCHAR | Contact telephone number |
| `driver_license` | VARCHAR | Driver's license number |
| `created_at` | DATETIME | Registration timestamp |

#### Table: `bookings`
| Column | Type | Notes |
|---|---|---|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | Booking ID |
| `booking_reference` | VARCHAR UNIQUE | Format `APX-XXXX-XXXX` |
| `customer_id` | INTEGER FOREIGN KEY | References `customers(id)` |
| `vehicle_id` | INTEGER FOREIGN KEY | References `vehicles(id)` |
| `pickup_date` | DATE | Rental pickup date |
| `return_date` | DATE | Rental return date |
| `rental_days` | INTEGER | Calculated rental duration |
| `daily_rate_snapshot` | DOUBLE | Daily rate snapshot at booking time |
| `total_rental_cost` | DOUBLE | Polymorphically calculated final cost |
| `booking_status` | VARCHAR | `CONFIRMED`, `CANCELLED`, `COMPLETED` |
| `created_at` | DATETIME | Timestamp of reservation creation |
| `cancelled_at` | DATETIME | Timestamp if cancelled |
| `cancellation_reason` | TEXT | Preserved audit cancellation reason |
| `special_requests` | TEXT | Customer delivery/VIP notes |

### How to Inspect `apexdrive.db`
You can open and view the SQLite database using any of the following tools:
1. **DB Browser for SQLite (Free GUI)**:
   * Download from [sqlitebrowser.org](https://sqlitebrowser.org/)
   * Open `apexdrive.db` -> Click **Browse Data** to view records in real time.
2. **VS Code / IDE Extension**:
   * Install the "SQLite Viewer" extension in VS Code / Antigravity / IntelliJ.
   * Right-click `apexdrive.db` and select **Open with SQLite Viewer**.
3. **Command Line (`sqlite3`)**:
   ```powershell
   sqlite3 apexdrive.db "SELECT booking_reference, vehicle_id, total_rental_cost, booking_status FROM bookings;"
   ```

---

## 🚀 4. How to Run on a Windows Laptop

### Option A: One-Click Startup (Recommended)
Double-click `run.bat` in the project root folder, or run in PowerShell / Command Prompt:
```powershell
.\run.bat
```
This script will:
1. Detect your Java 21 runtime (either portable Corretto JDK 21 or system Java).
2. Detect Maven.
3. Compile all Java code.
4. Launch the Spring Boot server.
5. Provide the live URL: `http://localhost:8080`.

### Option B: Standard Maven Command
If you have Java 21 and Maven on your system `PATH`:
```powershell
mvn spring-boot:run
```

### Option C: Build a Standalone Runnable JAR
To demonstrate on another computer without installing Maven:
1. Run `build.bat` or:
   ```powershell
   mvn clean package -DskipTests
   ```
2. This creates the executable fat JAR:
   ```
   target\apexdrive-1.0.0.jar
   ```
3. Run the JAR anywhere with Java 21:
   ```powershell
   java -jar target\apexdrive-1.0.0.jar
   ```

Once started, open your web browser to:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 🔐 5. Credentials & Demonstration Guide

### Administrator Portal
* **URL**: Access via the **Admin Portal** link in the navigation bar.
* **Username**: `admin`
* **Password**: `ApexAdmin2026!`
* *(A quick-fill demo button is pre-loaded on the login page for convenience during your viva presentation).*

### Recommended Demo Script for Your College Presentation
1. **Explore the Fleet**:
   * Show the homepage and filter by **Sports**, **Luxury**, and **Electric**.
   * Highlight how each card shows polymorphic perks (e.g., *Apex Sport Telemetry*, *VIP Concierge*, *Complimentary Supercharging*).
2. **Demonstrate Live Polymorphic Pricing**:
   * Click **Reserve Car** on a sports car (*Porsche 911 GT3 RS*).
   * Change the pickup and return dates from 2 days to 5 days.
   * Show the examiner how the backend dynamically computes duration, applies the multi-day discount, and updates the price instantly.
3. **Demonstrate Overlap Protection**:
   * Complete a reservation for the Porsche for tomorrow to 3 days later.
   * Note the generated reference code (e.g. `APX-4K2P-9M8L`).
   * Try to reserve the exact same Porsche for the exact same dates.
   * Point out the warning banner: the backend flags the car as unavailable and disables double-booking!
4. **Demonstrate Customer Privacy & History**:
   * Go to **My Bookings**.
   * Show that searching requires both the **Reference Code** and matching **Customer Email**.
   * Show the booking details and click **Cancel Booking**.
   * Note how the record remains safely stored in the database with status `CANCELLED` and audit reason, and the car becomes available again for new reservations.
5. **Demonstrate Admin Capabilities**:
   * Log into the **Admin Portal**.
   * Review total reservations, gross revenue, and fleet size.
   * Show adding a new custom vehicle or toggling availability.
6. **Demonstrate Persistence**:
   * Stop the server (`Ctrl+C`) and restart it (`.\run.bat`).
   * Reopen the browser and search for your booking — it is still there in `apexdrive.db`!

---

## 📁 6. Project File Structure

```
apexdrive/
├── pom.xml                                      # Maven project configuration & dependencies
├── run.bat                                      # One-click Windows runner
├── build.bat                                    # Standalone JAR packaging script
├── README.md                                    # Comprehensive documentation & Viva guide
├── apexdrive.db                                 # Persistent SQLite database file
├── src/
│   ├── main/
│   │   ├── java/com/apexdrive/
│   │   │   ├── ApexDriveApplication.java        # Spring Boot entry point
│   │   │   ├── config/
│   │   │   │   ├── DatabaseConfig.java          # SQLite pragmas & automatic fleet seeding
│   │   │   │   └── WebConfig.java               # CORS & static resource mapping
│   │   │   ├── model/
│   │   │   │   ├── RentalCalculable.java        # Interface demonstrating Abstraction
│   │   │   │   ├── Vehicle.java                 # Base class demonstrating Encapsulation
│   │   │   │   ├── LuxuryVehicle.java           # Subclass with VIP pricing polymorphism
│   │   │   │   ├── SportsVehicle.java           # Subclass with track telemetry polymorphism
│   │   │   │   ├── ElectricVehicle.java         # Subclass with eco-rebate polymorphism
│   │   │   │   ├── Customer.java                # Customer entity
│   │   │   │   ├── Booking.java                 # Reservation entity with overlap checks
│   │   │   │   ├── VehicleCategory.java         # Category enum
│   │   │   │   └── BookingStatus.java           # Reservation status enum
│   │   │   ├── dto/
│   │   │   │   ├── BookingRequest.java          # Jakarta Bean Validation request
│   │   │   │   ├── BookingResponse.java         # Formatted reservation output
│   │   │   │   ├── PriceCalculationRequest.java # Live quote input
│   │   │   │   ├── PriceCalculationResponse.java# Quote breakdown
│   │   │   │   ├── VehicleDto.java              # Admin vehicle payload
│   │   │   │   ├── CancelBookingRequest.java    # Cancellation verification
│   │   │   │   └── AdminStatsResponse.java      # Fleet & revenue analytics
│   │   │   ├── repository/
│   │   │   │   ├── VehicleRepository.java       # Spring Data JPA queries & date filters
│   │   │   │   ├── BookingRepository.java       # Overlap JPQL queries & revenue sum
│   │   │   │   └── CustomerRepository.java      # Customer data access
│   │   │   ├── service/
│   │   │   │   ├── VehicleService.java          # Abstraction interface
│   │   │   │   ├── VehicleServiceImpl.java      # Implementation with polymorphic factory
│   │   │   │   ├── BookingService.java          # Abstraction interface
│   │   │   │   └── BookingServiceImpl.java      # Core reservation business workflows
│   │   │   ├── controller/
│   │   │   │   ├── VehicleController.java       # Public fleet REST endpoints
│   │   │   │   ├── BookingController.java       # Public reservation REST endpoints
│   │   │   │   ├── AdminController.java         # Protected administrative endpoints
│   │   │   │   └── GlobalExceptionHandler.java  # REST JSON error interceptor
│   │   │   └── exception/
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── VehicleUnavailableException.java
│   │   │       ├── InvalidBookingException.java
│   │   │       └── UnauthorizedAccessException.java
│   │   └── resources/
│   │       ├── application.properties           # SQLite JDBC & Hibernate settings
│   │       └── static/
│   │           ├── index.html                   # Modern automotive single-page UI
│   │           ├── css/
│   │           │   └── style.css                # Dark luxury styling & responsiveness
│   │           └── js/
│   │               └── app.js                   # Client REST API communication
│   └── test/java/com/apexdrive/
│       ├── ApexDriveApplicationTests.java       # Spring context integration test
│       ├── model/
│       │   └── VehicleOOPTest.java              # Unit tests verifying OOP principles
│       └── service/
│           └── BookingServiceTest.java          # Unit tests verifying availability & rules
```
