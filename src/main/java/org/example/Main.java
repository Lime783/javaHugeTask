package org.example;


import org.example.cli.CLI;
import org.example.domain.payments.CardPayment;
import org.example.domain.resources.Resource;
import org.example.domain.resources.Room;
import org.example.domain.users.IndividualUser;
import org.example.money.Money;
import org.example.repositories.InMemoryBookingRepository;
import org.example.repositories.InMemoryResourceRepository;
import org.example.repositories.InMemoryUserRepository;

import java.util.Set;

public class Main {

    public static void testNegativeBalance() {
        Money money1 = new Money("12.345");
        Money money2 = new Money("56.78");
        System.out.println(money1.subtract(money2));
    }

    public static void testRoomConstructors() {
        Resource room1 = new Room("kitchen1", 40);
        Resource room2 = new Room("kitchen2", 40, Set.of("Kettle, Fridge"));
        Resource room3 = new Room("kitchen3", new Money("25"), 10, Set.of("Matcha"));
        Resource room4 = new Room("kitchen3", new Money("25"), 10, Set.of("Matcha"));

        System.out.println(room1.describe());
        System.out.println(room2.describe());
        System.out.println(room3.describe());
    }

    public static void testCardPayments() {
        CardPayment cardPayment1 = new CardPayment(new Money("12.34"), "1234");
        System.out.println(cardPayment1);
        cardPayment1.capture();
        System.out.println(cardPayment1.getPaymentStatus());
        cardPayment1.capture();
        System.out.println(cardPayment1.getPaymentStatus());
    }

    public static void main(String[] args) {
//        testNegativeBalance();
//        testRoomConstructors();
//        testCardPayments();

        var inMemoryBookingRepository = new InMemoryBookingRepository();
        var inMemoryResourceRepository = new InMemoryResourceRepository();
        var inMemoryUserRepository = new InMemoryUserRepository();

        CLI cli = new CLI(inMemoryBookingRepository, inMemoryResourceRepository, inMemoryUserRepository);
//        ADD_USER INDIVIDUAL siemaeniu@wp.pl Jan Kowalski  (1, 1)
//        ADD_ROOM kuchnia 5 200    (2, 2)
//        BOOK siemaeniu@wp.pl kuchnia 2026-07-07T16:30 2026-07-07T18:30    (3, 1)
//        PAY BK-20260707-0 CARD 1234   (5, 1)
//        INVOICE BK-20260707-0    (5, 2)
    }
}