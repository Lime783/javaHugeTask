package org.example.cli;

import org.example.domain.bookings.Booking;
import org.example.domain.resources.*;
import org.example.domain.users.CompanyUser;
import org.example.domain.users.IndividualUser;
import org.example.domain.users.User;
import org.example.money.Money;
import org.example.repositories.InMemoryBookingRepository;
import org.example.repositories.InMemoryResourceRepository;
import org.example.repositories.InMemoryUserRepository;
import org.example.services.BillingService;
import org.example.services.BookingService;
import org.example.services.PaymentService;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Scanner;

public class CLI {
    final Scanner scanner = new Scanner(System.in);

    private final InMemoryBookingRepository bookingRepository;
    private final InMemoryResourceRepository resourceRepository;
    private final InMemoryUserRepository userRepository;

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final BillingService billingService;


    public CLI(InMemoryBookingRepository inMemoryBookingRepository, InMemoryResourceRepository inMemoryResourceRepository, InMemoryUserRepository inMemoryUserRepository) {
        Objects.requireNonNull(inMemoryBookingRepository, "inMemoryBookingRepository must not be null");
        Objects.requireNonNull(inMemoryResourceRepository, "inMemoryResourceRepository must not be null");
        Objects.requireNonNull(inMemoryUserRepository, "inMemoryUserRepository must not be null");

        this.bookingRepository = inMemoryBookingRepository;
        this.resourceRepository = inMemoryResourceRepository;
        this.userRepository = inMemoryUserRepository;
        this.bookingService = new BookingService(getBookingRepository());
        this.paymentService = new PaymentService(getBookingRepository());
        this.billingService = new BillingService();

        while (true) {
            chooseWhatToDo();
            chooseCategory(scanner.nextLine());
        }
    }

    private void chooseWhatToDo() {
        System.out.println("""
                What service do you want to use?
                1 - users
                2 - resources
                3 - bookings
                4 - payments / invoices
                HELP
                QUIT
                """);
    }

    private void chooseCategory(String choice) {
        switch (choice.toUpperCase()) {
            case "1", "USERS" -> showUsersCategory();
            case "2", "RESOURCES" -> showResourcesCategory();
            case "3", "BOOKINGS" -> showBookingsCategory();
            case "4", "PAYMENTS", "INVOICES" -> showPaymentsAndInvoicesCategory();
            case "HELP" -> showHelp();
            case "QUIT" -> System.exit(0);
            default -> System.out.println("Unknown command, try again or use HELP");
        }
    }

    private void showUsersCategory() {
        System.out.println("""
                What command do you want to use?
                1 - add individual user
                2 - add company user
                3 - list all users
                """);
        switch (scanner.nextLine()) {
            case "1" -> addIndividualUser();
            case "2" -> addCompanyUser();
            case "3" -> listAllUsers();
        }
    }

    private void addIndividualUser() {
        String[] parts = processCommand("ADD_USER_INDIVIDUAL <email> <displayName>", false);

        String email = parts[1];

        String displayName = createName(parts, 2, parts.length - 1);
        getUserRepository().addUser(new IndividualUser(email, displayName));
        System.out.println("Successfully added " + displayName + ", " + email);
    }

    private void addCompanyUser() {
        String[] parts = processCommand("ADD_USER_COMPANY <email> <companyName> <taxID>", false);

        String email = parts[1];
        String companyName = createName(parts, 2, parts.length - 2);
        String taxID = parts[parts.length - 1];
        getUserRepository().addUser(new CompanyUser(email, companyName, taxID));
        System.out.println("Successfully added " + companyName + ", " + email);
    }

    private void listAllUsers() {
        getUserRepository().getUsers().forEach(System.out::println);
    }

    private void showResourcesCategory() {
        System.out.println("""
                What command do you want to use?
                1 - add a room
                2 - add a room with custom hourly rate
                3 - add a desk
                4 - add a desk with custom hourly rate
                5 - add a device
                6 - add a device with custom hourly rate
                7 - list all resources
                """);
        switch (scanner.nextLine()) {
            case "1" -> addRoom();
            case "2" -> addRoomWithCustomHourlyRate();
            case "3" -> addDesk();
            case "4" -> addDeskWithCustomHourlyRate();
            case "5" -> addDevice();
            case "6" -> addDeviceWithCustomHourlyRate();
            case "7" -> listAllDevices();
        }
    }

    private void addRoom() {
        String[] parts = processCommand("ADD_ROOM <name> <seats>", false);

        String name = createName(parts, 1, parts.length - 2);
        int seats = Integer.parseInt(parts[parts.length - 1]);

        getResourceRepository().addResource(new Room(name, seats));
        System.out.println("Successfully added " + name + " with " + seats + " seats");
    }

    private void addRoomWithCustomHourlyRate() {
        String[] parts = processCommand("ADD_ROOM <name> <seats> <customHourlyRate>", false);

        String name = createName(parts, 1, parts.length - 3);
        int seats = Integer.parseInt(parts[parts.length - 2]);
        Money customHourlyRate = Money.of(parts[parts.length - 1]);

        getResourceRepository().addResource(new Room(name, customHourlyRate, seats));
        System.out.println("Successfully added " + name + " with " + seats + " seats and custom hour rate of " + customHourlyRate);
    }

    private void addDesk() {
        String[] parts = processCommand("ADD_DESK <name> <small|regular>", false);

        if (!(parts[parts.length - 2].equals("small") || parts[parts.length - 2].equals("regular"))) {
            System.out.println("Invalid desk type");
            return;
        }

        String name = createName(parts, 1, parts.length - 2);
        DeskType deskType = DeskType.valueOf(parts[parts.length - 1].toUpperCase());

        getResourceRepository().addResource(new Desk(name, deskType));
        System.out.println("Successfully added " + name + " desk which is " + deskType);
    }

    private void addDeskWithCustomHourlyRate() {
        String[] parts = processCommand("ADD_DESK <name> <small|regular> <customHourlyRate>", false);

        if (!(parts[parts.length - 2].equals("small") || parts[parts.length - 2].equals("regular"))) {
            System.out.println("Invalid desk type");
            return;
        }

        String name = createName(parts, 1, parts.length - 3);
        DeskType deskType = DeskType.valueOf(parts[parts.length - 2].toUpperCase());
        Money customHourlyRate = Money.of(parts[parts.length - 1]);

        getResourceRepository().addResource(new Desk(name, customHourlyRate, deskType));
        System.out.println("Successfully added " + name + " desk which is " + deskType + " and costs " + customHourlyRate + " per hour");
    }

    private void addDevice() {
        String[] parts = processCommand("ADD_DEVICE <name> <quantity>", false);

        String name = createName(parts, 1, parts.length - 2);
        int quantity = Integer.parseInt(parts[parts.length - 1]);

        getResourceRepository().addResource(new Device(name, quantity));
        System.out.println("Successfully added " + name + " (max " + quantity + ")");
    }

    private void addDeviceWithCustomHourlyRate() {
        String[] parts = processCommand("ADD_DEVICE <name> <quantity> <customHourlyRate>", false);

        String name = createName(parts, 1, parts.length - 3);
        int quantity = Integer.parseInt(parts[parts.length - 2]);
        Money customHourlyRate = Money.of(parts[parts.length - 1]);

        getResourceRepository().addResource(new Device(name, customHourlyRate, quantity));
        System.out.println("Successfully added " + name + " (max " + quantity + ") which costs " + customHourlyRate + " per hour");
    }

    private void listAllDevices() {
        getResourceRepository().getResources().forEach(System.out::println);
    }

    private void showBookingsCategory() {
        System.out.println("""
                What command do you want to use?
                1 - book a resource from x to y
                2 - book a resource from x for y minutes
                3 - confirm a booking
                4 - cancel a booking
                5 - list all bookings
                """);
        switch (scanner.nextLine()) {
            case "1" -> bookResourceFromTo();
            case "2" -> bookResourceFromFor();
            case "3" -> confirmBooking();
            case "4" -> cancelBooking();
            case "5" -> listAllBookings();
        }
    }

    private void bookResourceFromTo() {
        System.out.println("(Iso format is YYYY-MM-DDTHH:MM)");
        String[] parts = processCommand("BOOK <userEmail> <resourceName> <startIso> <endIso>", false);

        String userEmail = parts[1];
        String resourceName = createName(parts, 2, parts.length - 3);
        LocalDateTime start = LocalDateTime.parse(parts[parts.length - 2]);
        LocalDateTime end = LocalDateTime.parse(parts[parts.length - 1]);
        User user = getUserRepository().findUserByEmail(userEmail);
        Resource resource = getResourceRepository().findResourceByName(resourceName);

        bookingService.book(user, resource, start, end);
        System.out.println("Successfully booked " + resourceName + " to " + userEmail + " from " + start + " to " + end);
    }

    private void bookResourceFromFor() {
        System.out.println("(Iso format is YYYY-MM-DDTHH:MM)");
        String[] parts = processCommand("BOOK <userEmail> <resourceName> <startIso> <durationMinutes>", false);

        String userEmail = parts[1];
        String resourceName = createName(parts, 2, parts.length - 3);
        LocalDateTime start = LocalDateTime.parse(parts[parts.length - 2]);
        LocalDateTime end = start.plusMinutes(Integer.parseInt(parts[parts.length - 1]));
        User user = getUserRepository().findUserByEmail(userEmail);
        Resource resource = getResourceRepository().findResourceByName(resourceName);

        bookingService.book(user, resource, start, end);
        System.out.println("Successfully booked " + resourceName + " to " + userEmail + " from " + start + " to " + end);
    }

    private void confirmBooking() {
        String[] parts = processCommand("CONFIRM <bookingId>", true);

        String bookingId = parts[1];
        Booking bookingToConfirm = getBookingRepository().findBookingByID(bookingId);

        bookingService.confirmBooking(bookingToConfirm);
        System.out.println("Successfully confirmed booking " + bookingToConfirm.getId());
    }

    private void cancelBooking() {
        String[] parts = processCommand("CANCEL <bookingId>", true);

        String bookingId = parts[1];
        Booking bookingToConfirm = getBookingRepository().findBookingByID(bookingId);

        bookingService.cancelBooking(bookingToConfirm);
        System.out.println("Successfully canceled booking " + bookingToConfirm.getId());
    }

    private void listAllBookings() {
        getBookingRepository().getBookings().forEach(System.out::println);
    }

    private void showPaymentsAndInvoicesCategory() {
        System.out.println("""
                What command do you want to use?
                1 - pay for a booking
                2 - make an invoice for existing booking
                """);
        switch (scanner.nextLine()) {
            case "1" -> payForBooking();
            case "2" -> createInvoice();
        }
    }

    private void payForBooking() {
        String[] parts = processCommand("PAY <bookingId> CARD <last4>", true);

        if (!(Objects.equals(parts[2], "CARD"))) {
            throw new IllegalArgumentException("Invalid command");
        }

        String bookingId = parts[1];
        String last4 = parts[3];

        paymentService.pay(bookingId, last4);
        System.out.println("Successfully paid for booking " + bookingId + " using card with 4 last digits: " + last4);
    }

    private void createInvoice() {
        String[] parts = processCommand("INVOICE <bookingId>", true);

        String bookingId = parts[1];
        Booking bookingToInvoice = getBookingRepository().findBookingByID(bookingId);

        billingService.toInvoice(bookingToInvoice);
        System.out.println("Successfully created invoice for booking " + bookingToInvoice.getId());
    }

    private void showHelp() {
        System.out.println("""
                ==================== HELP ====================
                
                MAIN MENU
                1 / USERS              - users operations
                2 / RESOURCES          - resources operations
                3 / BOOKINGS           - bookings operations
                4 / PRICINGS           - pricing policy operations
                5 / PAYMENTS           - payments operations
                5 / INVOICES           - invoices operations
                HELP                   - show this help
                QUIT                   - exit the application
                
                USERS
                1 - add individual user
                2 - add company user
                3 - list all users
                
                RESOURCES
                1 - add a room
                2 - add a room with custom hourly rate
                3 - add a desk
                4 - add a desk with custom hourly rate
                5 - add a device
                6 - add a device with custom hourly rate
                7 - list all resources
                
                BOOKINGS
                1 - book a resource from x to y
                2 - book a resource from x for y minutes
                3 - confirm a booking
                4 - cancel a booking
                5 - list all bookings
                
                
                PAYMENTS / INVOICES
                1 - pay for a booking
                2 - create an invoice for an existing booking
                
                ==============================================
                """);
    }

    private String[] processCommand(String commandToShow, boolean fixedLength) {
        int partsRequired = commandToShow.split("\\s+").length;

        System.out.println(commandToShow);
        String userCommand = scanner.nextLine();
        String[] parts = userCommand.split("\\s+");

        validateCommandLength(partsRequired, parts, fixedLength);
        validateFirstCommand(commandToShow, parts);

        return parts;
    }

    private void validateCommandLength(int partsRequired, String[] parts, boolean fixedLength) {
        if (fixedLength) {
            if (parts.length != partsRequired) {
                throw new IllegalArgumentException("Invalid command length");
            }
        } else {
            if (parts.length < partsRequired) {
                throw new IllegalArgumentException("Invalid command length");
            }
        }
    }

    private void validateFirstCommand(String commandToShow, String[] parts) {
        if (!(parts[0].equals(commandToShow.split("\\s+")[0]))) {
            throw new IllegalArgumentException("Invalid command");
        }
    }

    private String createName(String[] parts, int from, int to) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = from; i <= to; i++) {
            stringBuilder.append(parts[i]).append(" ");
        }
        return stringBuilder.toString();
    }

    public InMemoryBookingRepository getBookingRepository() {
        return bookingRepository;
    }

    public InMemoryResourceRepository getResourceRepository() {
        return resourceRepository;
    }

    public InMemoryUserRepository getUserRepository() {
        return userRepository;
    }
}