package org.example;


import org.example.cli.CLI;
import org.example.domain.payments.CardPayment;
import org.example.domain.resources.Resource;
import org.example.domain.resources.Room;
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

        // Uzywam LocalDateTime bo potzrebuje zarowno daty jak i godziny.
        // Zakładam, że firma działa w Polsce i nie będzie otwarta między 2 a 3
        // więc nie potrzebuje obsługi zmiany czasu z zimowego na letni i na odwrót

        InMemoryBookingRepository inMemoryBookingRepository = new InMemoryBookingRepository();
        InMemoryResourceRepository inMemoryResourceRepository = new InMemoryResourceRepository();
        InMemoryUserRepository inMemoryUserRepository = new InMemoryUserRepository();

        new CLI(inMemoryBookingRepository, inMemoryResourceRepository, inMemoryUserRepository);
//        ADD_USER_INDIVIDUAL siemaeniu@wp.pl Jan Kowalski  (1, 1)
//        ADD_ROOM kuchnia 5 200    (2, 2)
//        BOOK siemaeniu@wp.pl kuchnia 2026-07-07T16:30 2026-07-07T18:30    (3, 1)
//        PAY BK-20260707-0 CARD 1234   (4, 1)
//        INVOICE BK-20260707-0    (4, 2)
    }
}