# Low-Level System Design (LLD) - Parking Lot System

A modular, extensible Object-Oriented Low-Level Design (LLD) for a Parking Lot System implemented in Java.

---

## 🏗 System Architecture & Key Components

The system is designed following Object-Oriented Design (OOD) principles and key design patterns to ensure high cohesion, low coupling, and scalability.

```
                      +-------------------+
                      |    ParkingLot     | (Singleton)
                      +---------+---------+
                                |
               +----------------+----------------+
               |                                 |
     +---------v---------+             +---------v---------+
     |   ParkingFloor    |             |      Ticket       |
     +---------+---------+             +-------------------+
               |
     +---------v---------+
     |    ParkingSpot    |
     +-------------------+
```

---

## 🎨 Design Patterns Implemented

1. **Singleton Pattern**:
   - `ParkingLot`: Ensures only a single instance of the parking lot service exists across the application.
2. **Factory Pattern**:
   - `VehicleFactory`: Instantiates vehicles (`Bike`, `Car`, `Truck`) based on `VehicleType`.
   - `PaymentFactory`: Instantiates `PaymentStrategy` (`UPIStrategy`, `CardStrategy`) based on `PaymentMode`.
   - `PricingStrategyFactory`: Instantiates `PricingStrategy` (`TimeBasedPricingStrategy`, `EventBasedPricingStrategy`).
3. **Strategy Pattern**:
   - **Payment Strategies** (`PaymentStrategy`): Pluggable payment implementations (`UPIStrategy`, `CardStrategy`).
   - **Pricing Strategies** (`PricingStrategy`): Flexible fare calculation logic (`TimeBasedPricingStrategy`, `EventBasedPricingStrategy`).

---

## 📁 Package Structure

```
ParkingLot/
├── Enum/
│   ├── GateType.java
│   ├── PaymentMode.java
│   ├── PaymentStatus.java
│   ├── PricingStrategyType.java
│   └── VehicleType.java
├── Factory/
│   ├── PaymentFactory.java
│   ├── PricingStrategyFactory.java
│   └── VehicleFactory.java
├── Model/
│   ├── Bike.java
│   ├── Car.java
│   ├── EntryGate.java
│   ├── ExitGate.java
│   ├── Gate.java
│   ├── ParkingFloor.java
│   ├── ParkingSpot.java
│   ├── Ticket.java
│   ├── Truck.java
│   └── Vehicle.java
├── Service/
│   ├── ParkingLot.java
│   └── PaymentProcessor.java
├── Strategy/
│   ├── Payment/
│   │   ├── CardStrategy.java
│   │   ├── PaymentStrategy.java
│   │   └── UPIStrategy.java
│   └── Pricing/
│       ├── EventBasedPricingStrategy.java
│       ├── PricingStrategy.java
│       └── TimeBasedPricingStrategy.java
└── main.java
```

---

## 🚀 Key Workflows

### 1. Vehicle Parking (Entry)
1. Vehicle arrives at an `EntryGate`.
2. `EntryGate` delegates to `ParkingLot.parkVehicle(vehicle)`.
3. `ParkingLot` iterates through `ParkingFloor`s to find an unoccupied `ParkingSpot` matching the `VehicleType`.
4. A `Ticket` is generated with entry timestamp, vehicle info, floor ID, and spot ID.
5. The ticket is registered in active tickets.

### 2. Vehicle Unparking & Payment (Exit)
1. Vehicle arrives at an `ExitGate` with ticket ID and payment mode.
2. `ExitGate` delegates to `ParkingLot.unparkVehicle(ticketId, paymentMode)`.
3. `PricingStrategy` calculates the total parking fee based on duration.
4. `PaymentProcessor` executes payment using the selected `PaymentStrategy` (`UPI` or `Card`).
5. On successful payment, the ticket is removed from active tickets.

## 🔒 Thread Safety & Multi-Threading Concurrency

The system handles high-concurrency scenarios where multiple vehicles arrive at different `EntryGate`s simultaneously:

1. **Atomic Spot Allocation (CAS)**:
   - Uses `AtomicBoolean.compareAndSet(false, true)` (`tryOccupy()`) at the `ParkingSpot` level.
   - Eliminates Check-Then-Act race conditions without coarse-grained global locks on `ParkingLot`.
2. **Concurrent Data Structures**:
   - Uses `ConcurrentHashMap` for `activeTickets` and floor spot mappings to prevent map corruption during concurrent access.
3. **Thread-Safe Singleton**:
   - Uses double-checked locking with `volatile` for `ParkingLot.getInstance()`.
4. **Spot Cleanup on Unpark**:
   - `spot.vacate()` atomically frees the spot when a vehicle leaves, allowing waiting threads to park safely.

---

## 🛠 Prerequisites & Running the Code

- **Java Development Kit (JDK)**: 17 or higher.

### Compilation & Execution
```bash
# Compile all java files
javac -d bin $(find . -name "*.java")

# Run main class
java -cp bin Main
```
