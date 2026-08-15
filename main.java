import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Model.EntryGate;
import Model.ExitGate;
import Model.ParkingFloor;
import Model.ParkingSpot;
import Model.Ticket;
import Model.Vehicle;
import Service.ParkingLot;
import Enum.PaymentMode;
import Enum.VehicleType;
import Factory.VehicleFactory;

class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== INITIALIZING PARKING LOT SYSTEM ===");
        EntryGate gate1 = new EntryGate("Gate-E1");
        EntryGate gate2 = new EntryGate("Gate-E2");
        ExitGate exitGate = new ExitGate("Gate-X1");

        // Setup Floor 1 with ONLY 1 CAR spot
        ParkingFloor floor1 = new ParkingFloor("Floor1");
        floor1.addSpot(new ParkingSpot("F1_CAR_1", VehicleType.CAR, false));
        ParkingLot.getInstance().addFloor(floor1);

        System.out.println("\n=== CONCURRENCY TEST: 2 Vehicles arrive at 2 different gates simultaneously for 1 spot ===");
        
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(2);

        Vehicle car1 = VehicleFactory.getVehicle("CAR-101", VehicleType.CAR);
        Vehicle car2 = VehicleFactory.getVehicle("CAR-102", VehicleType.CAR);

        final Ticket[] ticket1 = new Ticket[1];
        final Ticket[] ticket2 = new Ticket[1];

        // Thread 1: Car 1 arrives at Gate 1
        Thread t1 = new Thread(() -> {
            try {
                startSignal.await(); // Wait for sync start
                System.out.println("[Thread-1] Gate-E1 attempting to park CAR-101...");
                ticket1[0] = gate1.parkVehicle(car1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                doneSignal.countDown();
            }
        }, "Thread-Gate1");

        // Thread 2: Car 2 arrives at Gate 2
        Thread t2 = new Thread(() -> {
            try {
                startSignal.await(); // Wait for sync start
                System.out.println("[Thread-2] Gate-E2 attempting to park CAR-102...");
                ticket2[0] = gate2.parkVehicle(car2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                doneSignal.countDown();
            }
        }, "Thread-Gate2");

        t1.start();
        t2.start();

        // Release both threads simultaneously
        startSignal.countDown();
        doneSignal.await();

        System.out.println("\n=== RESULT OF CONCURRENT PARKING ===");
        System.out.println("Car 1 Ticket: " + (ticket1[0] != null ? ticket1[0].getId() : "FAILED (No Spot)"));
        System.out.println("Car 2 Ticket: " + (ticket2[0] != null ? ticket2[0].getId() : "FAILED (No Spot)"));

        Ticket successfulTicket = ticket1[0] != null ? ticket1[0] : ticket2[0];

        if (successfulTicket != null) {
            System.out.println("\n=== UNPARKING VEHICLE & FREED SPOT VERIFICATION ===");
            exitGate.unparkVehicle(successfulTicket.getId(), PaymentMode.UPI);

            System.out.println("\n=== PARKING A NEW VEHICLE AFTER SPOT FREED ===");
            Vehicle car3 = VehicleFactory.getVehicle("CAR-103", VehicleType.CAR);
            Ticket ticket3 = gate1.parkVehicle(car3);
            System.out.println("Car 3 Ticket: " + (ticket3 != null ? ticket3.getId() : "FAILED"));
            if (ticket3 != null) {
                exitGate.unparkVehicle(ticket3.getId(), PaymentMode.UPI);
            }
        }

        System.out.println("\n=== STRESS TEST: 10 Threads competing simultaneously for 2 CAR spots ===");
        runStressTest();
    }

    private static void runStressTest() throws InterruptedException {
        // Reset parking lot with 2 car spots
        ParkingFloor floor = new ParkingFloor("Floor2");
        floor.addSpot(new ParkingSpot("F2_CAR_1", VehicleType.CAR, false));
        floor.addSpot(new ParkingSpot("F2_CAR_2", VehicleType.CAR, false));
        ParkingLot.getInstance().addFloor(floor);

        int totalThreads = 10;
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(totalThreads);
        
        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger failCount = new java.util.concurrent.atomic.AtomicInteger(0);

        for (int i = 1; i <= totalThreads; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    startSignal.await();
                    EntryGate gate = new EntryGate("Gate-E" + (id % 3 + 1));
                    Vehicle v = VehicleFactory.getVehicle("STRESS-CAR-" + id, VehicleType.CAR);
                    Ticket ticket = gate.parkVehicle(v);
                    if (ticket != null) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown();
                }
            }, "StressThread-" + id).start();
        }

        startSignal.countDown(); // Fire all 10 threads at once
        doneSignal.await();

        System.out.println("\n=== STRESS TEST RESULTS ===");
        System.out.println("Total Threads Attempted: " + totalThreads);
        System.out.println("Successful Parks: " + successCount.get() + " (Expected: 3)");
        System.out.println("Failed Parks (No Spot): " + failCount.get() + " (Expected: 7)");
        System.out.println("Thread Safety Status: " + (successCount.get() == 3 ? "PASSED ✅" : "FAILED ❌"));
    }
}