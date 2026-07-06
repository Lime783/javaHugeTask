package org.example.cli;

import org.example.domain.users.CompanyUser;
import org.example.domain.users.IndividualUser;
import org.example.domain.users.User;
import org.example.repositories.InMemoryBookingRepository;
import org.example.repositories.InMemoryResourceRepository;
import org.example.repositories.InMemoryUserRepository;

import java.util.Objects;
import java.util.Scanner;

public class CLI {
    Scanner scanner = new Scanner(System.in);
    private InMemoryBookingRepository bookingRepository;
    private InMemoryResourceRepository resourceRepository;
    private InMemoryUserRepository userRepository;

    public CLI(InMemoryBookingRepository inMemoryBookingRepository, InMemoryResourceRepository inMemoryResourceRepository, InMemoryUserRepository inMemoryUserRepository) {
        Objects.requireNonNull(inMemoryBookingRepository, "inMemoryBookingRepository must not be null");
        Objects.requireNonNull(inMemoryResourceRepository, "inMemoryResourceRepository must not be null");
        Objects.requireNonNull(inMemoryUserRepository, "inMemoryUserRepository must not be null");

        this.bookingRepository = inMemoryBookingRepository;
        this.resourceRepository = inMemoryResourceRepository;
        this.userRepository = inMemoryUserRepository;

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
                4 - pricings (not now)
                5 - payments / invoices
                HELP
                QUIT
                """);
    }

    private void chooseCategory(String choice) {
        switch (choice.toUpperCase()) {
            case "1", "USERS" -> showUsersCategory();
            case "2", "RESOURCES" -> showResourcesCategory();
            case "3", "BOOKINGS" -> showBookingsCategory();
//            case "4", "PRICINGS" -> showPricingCategory();
            case "5", "PAYMENTS", "INVOICES" -> showPaymentsAndInvoicesCategory();
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
        System.out.println("ADD_USER INDIVIDUAL <email> <displayName>");
        String command = scanner.nextLine();
        String[] parts = command.split("\\s+");

        if (parts.length < 4) {
            System.out.println("Invalid command length");
            return;
        }

        if (!(parts[0].equals("ADD_USER") || parts[1].equals("INDIVIDUAL"))) {
            System.out.println("Invalid command");
            return;
        }

        String email = parts[2];
        String displayName = parts[3];
        getUserRepository().addUser(new IndividualUser(email, displayName));
    }

    private void addCompanyUser() {
        System.out.println("ADD_USER COMPANY <email> <companyName> <taxID>");
        String command = scanner.nextLine();
        String[] parts = command.split("\\s+");

        //TODO: companyName ze spacja
        if (parts.length < 5) {
            System.out.println("Invalid command length");
            return;
        }

        if (!(parts[0].equals("ADD_USER") || parts[1].equals("COMPANY"))) {
            System.out.println("Invalid command");
            return;
        }

        String email = parts[2];
        String companyName = parts[3];
        String taxID = parts[4];
        getUserRepository().addUser(new CompanyUser(email, companyName, taxID));
    }

    private void listAllUsers() {
        System.out.println("LIST_USERS");
        if (!(scanner.nextLine().equals("LIST_USERS"))) {
            System.out.println("Invalid command");
            return;
        }
        getUserRepository().getUsers().forEach(System.out::println);
    }

    private void showResourcesCategory() {
        System.out.println("""
                What command do you want to use?
                1 - add a room
                2 - add a desk
                3 - add a device
                4 - list all resources
                """);
        switch (scanner.nextLine()) {
            case "1" -> addRoom();
            case "2" -> addDesk();
            case "3" -> addDevice();
            case "4" -> listAllDevices();
        }
    }

    private void addRoom() {
        System.out.println("ADD_ROOM <name> <seats> <hourlyRate>");
    }

    private void addDesk() {
        System.out.println("ADD_DESK <name> <hot|fixed> <hourlyRate>");
    }

    private void addDevice() {
        System.out.println("ADD_DEVICE <name> <quantity> <hourlyRate>");
    }

    private void listAllDevices() {
        System.out.println("LIST_RESOURCES");
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
        System.out.println("BOOK <userEmail> <resourceName> <startIso> <endIso>");
    }

    private void bookResourceFromFor() {
        System.out.println("BOOK <userEmail> <resourceName> <startIso> <durationMinutes>");
    }

    private void confirmBooking() {
        System.out.println("CONFIRM <bookingId>");
    }

    private void cancelBooking() {
        System.out.println("CANCEL <bookingId>");
    }

    private void listAllBookings() {
        System.out.println("LIST_BOOKINGS");
    }


    private void showPricingCategory() {
        System.out.println("""
                What command do you want to use?
                1 - set pricing policy
                """);
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
        System.out.println("PAY <bookingId> CARD <last4>");
    }

    private void createInvoice() {
        System.out.println("INVOICE <bookingId>");
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
                2 - add a desk
                3 - add a device
                4 - list all resources
                
                BOOKINGS
                1 - book a resource from x to y
                2 - book a resource from x for y minutes
                3 - confirm a booking
                4 - cancel a booking
                5 - list all bookings
                
                PRICINGS
                1 - set pricing policy
                
                PAYMENTS / INVOICES
                1 - pay for a booking
                2 - create an invoice for an existing booking
                
                ==============================================
                """);
    }

    public InMemoryBookingRepository getBookingRepository() {
        return bookingRepository;
    }

    public void setBookingRepository(InMemoryBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public InMemoryResourceRepository getResourceRepository() {
        return resourceRepository;
    }

    public void setResourceRepository(InMemoryResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public InMemoryUserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
