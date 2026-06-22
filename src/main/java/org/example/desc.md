
---

## 1) Fabuła (kontekst)

Firma **ffWork** zarządza siecią coworkingów. Potrzebuje narzędzia do:
- rejestrowania **użytkowników** (osoby i firmy),
- dodawania **zasobów** (sale, biurka, urządzenia),
- tworzenia i obsługi **rezerwacji**, cen oraz płatności,
- wystawiania **faktur** (opcjonalnie: **raportów**).

Twoim zadaniem jest zaprojektowanie obiektowego modelu domeny oraz prostego CLI (REPL), które pozwoli testować logikę biznesową „na żywo”, wpisując komendy.

Traktuj to jak miniaturową, ale kompletną aplikację: ma mieć sensowny podział na warstwy (model → repozytoria → serwisy → CLI), czytelne komunikaty i przewidywalne zachowanie.

---

## 2) Kryteria OOP (co musi pojawić się w kodzie)

To jest „checklista techniczna” — niezależnie od fabuły, Twój kod ma pokazać, że rozumiesz mechanizmy obiektowe:

- **Klasy** — spójny model domeny (użytkownicy, zasoby, rezerwacje, płatności, faktury).
- **Dziedziczenie** — wspólne zachowania w klasie bazowej + specjalizacje w podklasach (np. `Resource` + jego podklasy).
- **Dwie klasy abstrakcyjne** (min.):
    - `Resource`,
    - `Payment`.
- **Interfejsy** (min. 3), np.:
    - `PricingPolicy`,
    - `Billable`,
- **Overriding** (min. 5): np. różne `baseRatePerHour()` w zasobach, `describe()` w zasobach, implementacje `Payment#capture()`.
- **Overloading** (min. 3): np. przeciążone konstruktory, metody `book(...)` w serwisie rezerwacji (wariant „start + end” **oraz** „start + czas trwania”).

> Bardziej zaawansowane elementy (rozbudowany kalendarz, zniżki, portfel, raporty) są oznaczone jako **opcjonalne** — nie są wymagane do zaliczenia podstawowej wersji zadania.

---

## 3) Specyfikacja klas — **wymagania minimalne**

### 3.1. Czas — wybierz klasę do obsługi dat i godzin

W całym projekcie będziesz pracować z momentami w czasie (data + godzina): początek i koniec rezerwacji, data wystawienia faktury, zakresy w raportach. **Nie implementuj własnej klasy czasu** — zamiast tego wybierz gotową klasę ze standardowej biblioteki Javy i w komentarzu (1–3 zdania) uzasadnij swój wybór.

**Twoja klasa czasu musi umożliwiać poniższe operacje** (są używane w dalszej części zadania — wybierz typ, który je wspiera):

**Wskazówka:** jeśli zauważysz, że w wielu miejscach piszesz to samo (np. parsowanie albo formatowanie), możesz schować to za niewielką klasą pomocniczą (np. `TimeUtils`) z metodami statycznymi. To nie jest wymagane, ale bywa wygodne i bardziej czytelne.

---

### 3.2. Pieniądze

#### `Money` (value object)

- **Pola:**
    - `BigDecimal amount` — waluta stała: PLN.

- **Konstruktory / fabryki:**
    - `Money(BigDecimal amount)` — ustawia skalę `2`, `RoundingMode.HALF_UP`.
    - `static Money of(String)` — tworzy obiekt z napisu (np. `"123.45"`).
        - (Opcjonalnie możesz dodać `of(double)`, ale uważaj na dokładność).

- **Metody (minimum):**
    - `Money add(Money other)`
    - `Money subtract(Money other)`
    - `Money multiply(BigDecimal m)`
    - `String toString()` → np. `123.45 PLN`

- **Opcjonalnie (mile widziane):**
    - `Money multiply(double m)`
    - `int compareTo(Money other)`
    - `equals()` / `hashCode()`

- **Inwariant:** docelowo kwota nie powinna być ujemna (chyba że świadomie to dopuszczasz i dokumentujesz).

---

### 3.3. Użytkownicy

#### `class User`

- **Pola:**
    - `String email` (unikalny),
    - `String displayName`.
- **Metody:** akcesory, `toString()`.

**Podklasy:**
- `class IndividualUser extends User`
    - Można dodać pole `studentId` (opcjonalne).
- `class CompanyUser extends User`
    - **Pola:** `String companyName`, `String taxId` (NIP).

---

### 3.4. Zasoby (dziedziczenie i overriding)

#### `abstract class Resource`

- **Pola:**
    - `String name` (unikalny),
    - `Money customHourlyRate` (opcjonalna niestandardowa stawka).

- **Metody abstrakcyjne:**
    - `protected abstract Money baseRatePerHour();`
    - `public abstract String describe();` — krótki opis do listowania.

- **Metody konkretne:**
    - `public Money hourlyRate()` — jeśli `customHourlyRate != null` → zwróć ją; w przeciwnym razie `baseRatePerHour()`.

**Podklasy:**
- `class Room extends Resource`
    - **Pola:** `int seats`, `Set<String> equipment` (np. `"projector"`, `"whiteboard"`).
    - **Overriding:** `baseRatePerHour()`, `describe()`.

- `class Desk extends Resource`
    - **Pola:** `enum DeskType { HOT, FIXED } type`.
    - **Overriding:** jw.

- `class Device extends Resource`
    - **Pola:** `int quantity` (ile sztuk można równolegle zarezerwować).
    - **Overriding:** jw.

---

### 3.5. Rezerwacje

#### `enum BookingStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED }`

#### `class Booking`

- **Pola:**
    - `String id` (np. `BK-<yyyyMMdd>-<counter>`),
    - `User user`,
    - `Resource resource`,
    - `start`, `end` — momenty w czasie (typ Twojej wybranej klasy czasu, np. `LocalDateTime`),
    - `BookingStatus status`,
    - `Money calculatedPrice` (cena wyliczona na podstawie polityki cen),
    - `Payment payment` (może być `null`).

- **Inwarianty:**
    - `end` > `start`,
    - `status` zmienia się tylko dozwolonymi przejściami:
        - `PENDING → CONFIRMED` lub `CANCELLED`,
        - `CONFIRMED → COMPLETED` lub `CANCELLED`.

- **Metody pomocnicze:**
    - `int durationMinutes()` — liczba minut pomiędzy `start` a `end` (np. `Duration.between(start, end).toMinutes()`).

---

### 3.6. Polityki cen (interfejsy + implementacje)

#### `interface PricingPolicy`

- `Money price(Booking booking)` — liczy cenę **bazową** za czas trwania na podstawie `booking.resource.hourlyRate()`.

  **Reguła minutowa (sugerowana):**
    - `pricePerMinute = hourlyRate / 60`,
    - `price = minutes × pricePerMinute`,
    - zaokrąglanie do 2 miejsc, `RoundingMode.HALF_UP`.

**Implementacje (wersja podstawowa):**
- `class StandardPricing implements PricingPolicy`
- `class HappyHoursPricing implements PricingPolicy`
    - **Założenia:** –30% w godzinach 14:00–16:00 (np. w każdy dzień).
    - Najprościej: jeśli **godzina startu rezerwacji** mieści się w tym przedziale → rabat 30% na całą rezerwację.

> ⭐ **Opcjonalnie:** możesz rozbudować logikę Happy Hours (np. naliczać rabat tylko za część rezerwacji, która wpada w godziny HH).

> ⭐ **System zniżek (`Discountable`, `StudentDiscount`, itd.) jest przeniesiony do sekcji „Opcjonalne rozszerzenia”.**  
> W wersji podstawowej wystarczy `PricingPolicy`.

---

### 3.7. Płatności i faktury (wersja podstawowa)

#### `enum PaymentStatus { INITIATED, CAPTURED }`

#### `abstract class Payment`

- **Pola:**
    - `Money amount`,
    - `String paymentId`,
    - `PaymentStatus status`.

- **Metody abstrakcyjne:**
    - `void capture()` — realizuje płatność i ustawia status na `CAPTURED` (przy błędnym stanie może rzucać `IllegalStateException`).

#### `class CardPayment extends Payment`

- **Pola:** `String last4` (ostatnie 4 cyfry karty).
- **Metody:**
    - `capture()` — symuluje autoryzację płatności (zmiana statusu na `CAPTURED`).

> ⭐ **Opcjonalne (zaawansowane):** dodać `WalletPayment`, dodatkowy status `REFUNDED`, metodę `refund()` i logikę zwrotów — opisane w sekcji „Opcjonalne rozszerzenia”.

#### `interface Billable`

- `Invoice toInvoice(Booking booking)`.

#### `class Invoice`

- **Pola:**
    - `String invoiceNumber`,
    - `issueDate` — moment wystawienia (typ Twojej wybranej klasy czasu, np. `LocalDateTime`),
    - `User buyer`,
    - `Money total`,
    - `String itemDescription` (np. `"Rezerwacja <resource> <start–end>"`).

---

### 3.8. Repozytoria (in-memory)

Prosty wzorzec repozytorium (dane trzymane w pamięci). Możesz użyć tablic `[]`, `List`, `Map`, itp.

- `interface UserRepository {
      void add(User u);
      Optional<User> findByEmail(String email);
      User[] findAll(); albo List<User> findAll();
  }`

- `interface ResourceRepository {
      void add(Resource r);
      Optional<Resource> findByName(String name);
      Resource[] findAll(); albo List<Resource> findAll();
      // ⭐ Opcjonalnie: List<Resource> findByType(Class<? extends Resource> t);
  }`

- `interface BookingRepository {
      void add(Booking b);
      Optional<Booking> findById(String id);
      Booking[] findAll(); albo List<Booking> findAll(); 
      // ⭐ Opcjonalnie: List<Booking> findByResource(Resource r);
      // ⭐ Opcjonalnie: List<Booking> findByUser(User u);
  }`

**Implementacje:**  
`InMemoryUserRepository`, `InMemoryResourceRepository`, `InMemoryBookingRepository` — trzymają dane w kolekcjach/tablicach Javy.

---

### 3.9. Serwisy (logika biznesowa)

#### `class BookingService`

- **Zależności:** repozytoria (`UserRepository`, `ResourceRepository`, `BookingRepository`) + aktualna `PricingPolicy` (np. `StandardPricing` albo `HappyHoursPricing`).

- **Metody (overloading):**
    - `Booking book(User u, Resource r, <Czas> start, <Czas> end)`
    - `Booking book(User u, Resource r, <Czas> start, int durationMinutes)` — deleguje do wersji z `end`, wyliczając koniec jako `start.plusMinutes(durationMinutes)`.

  > `<Czas>` to typ Twojej wybranej klasy czasu (np. `LocalDateTime`).

- **Algorytm `book` (wersja podstawowa):**
    1. Waliduj czasy (`end > start`).
    2. Sprawdź kolizje dla zasobu:
        - dla `Room`/`Desk` — żadna rezerwacja w stanie `CONFIRMED`/`PENDING` nie może się **nakładać** (zakresy `[start, end)`):
            - nakładanie: `startA < endB && startB < endA`.
        - dla `Device` — dozwolonych jest `quantity` równoległych rezerwacji (zlicz nakładające się).
    3. Utwórz `Booking` ze statusem `PENDING` i policz cenę:
        - `base = pricingPolicy.price(booking)`.
    4. Zapisz w repo i nadaj `id` (format `BK-<yyyyMMdd>-<counter>` po **dacie startu**).

- **Inne metody (minimum):**
    - `confirm(String bookingId)` — zmiana statusu na `CONFIRMED`,
    - `cancel(String bookingId)` — zmiana statusu na `CANCELLED`, jeśli to dozwolone,
    - `complete(String bookingId)` — zmiana statusu na `COMPLETED`,
    - `list(...)` — zwraca listę rezerwacji.
        - **Filtry po użytkowniku/zasobie/statusie mogą być zrobione w prosty sposób (np. osobne metody lub parametry).**

#### `class PaymentService`

- `Payment pay(String bookingId, String cardLast4)` — tworzy `CardPayment`, woła `capture()`, przypina płatność do `Booking` i ją zwraca.

> ⭐ **Opcjonalne (zaawansowane):** obsługa portfela (`WalletPayment`), zwrotów (`refund`) i dodatkowych walidacji — w sekcji rozszerzeń.

#### `class BillingService implements Billable`

- `Invoice toInvoice(Booking booking)` — numer faktury `INV-<yyyyMMdd>-<counter>` po **dacie wystawienia**.

---

## 3.10. Raporty (⭐ opcjonalne)

> **Ta sekcja jest opcjonalna — dla chętnych / na dodatkowe „punkty”.**

#### `class ReportingService`

- `Map<Resource, Double> utilization(<Czas> from, <Czas> to)` (lub własna klasa z polami / tablica zamiast `Map`) — obłożenie w %:
    - licz minuty z rezerwacji o statusie **CONFIRMED/COMPLETED** w przedziale `[from, to)`,
    - podziel przez łączną liczbę minut w tym przedziale (zasób dostępny 24/7),
    - zaokrąglij do dwóch miejsc po przecinku.

- `Map<String, Money> revenueByResource(<Czas> from, <Czas> to)` (lub własna klasa / tablica zamiast `Map`) oraz `Money totalRevenue(...)` — suma z **opłaconych** rezerwacji (po `capture`).

  > `<Czas>` to typ Twojej wybranej klasy czasu (np. `LocalDateTime`).

---

## 4) CLI (REPL) — komendy i format

### Użytkownicy

- `ADD_USER INDIVIDUAL <email> <fullName>`
- `ADD_USER COMPANY <email> <companyName> <nip>`
- `LIST_USERS`

### Zasoby

- `ADD_ROOM <name> <seats> <hourlyRate>`
- `ADD_DESK <name> <hot|fixed> <hourlyRate>`
- `ADD_DEVICE <name> <quantity> <hourlyRate>`
- `LIST_RESOURCES`
  > ⭐ **Opcjonalnie:** `LIST_RESOURCES [TYPE=<ROOM|DESK|DEVICE>]`

### Rezerwacje

- `BOOK <userEmail> <resourceName> <startIso> <endIso>`
- `BOOK <userEmail> <resourceName> <startIso> <durationMinutes>`
- `CONFIRM <bookingId>`
- `CANCEL <bookingId>`
- `LIST_BOOKINGS`
  > ⭐ **Opcjonalnie:** `LIST_BOOKINGS [USER=<email>] [RESOURCE=<name>] [STATUS=<PENDING|CONFIRMED|CANCELLED|COMPLETED>]`

### Polityki cen

- `SET_PRICING STANDARD|HAPPY_HOURS`

> ⭐ **Zniżki (`SET_DISCOUNT ...`) są w całości opcjonalne — patrz sekcja rozszerzeń.**

### Płatności / faktury

- `PAY <bookingId> CARD <last4>`
- `INVOICE <bookingId>`

> ⭐ **Opcjonalne:** `PAY <bookingId> WALLET` (jeśli implementujesz `WalletPayment`).

### Raporty (⭐ opcjonalne)

- `REPORT UTILIZATION <fromIso> <toIso>`
- `REPORT REVENUE <fromIso> <toIso>`

### Pomoc / wyjście

- `HELP` — drukuje krótką ściągę,
- `QUIT` — kończy program.

**Zachowanie I/O:**
- Sukces: `OK: <krótki opis>` + ewentualne dane (ID, kwoty).
- Błąd: `ERROR: <treść>` (np. walidacja dat, brak użytkownika/zasobu, kolizja).

---

## 5) Dokładne reguły i założenia (ważne przy ocenie)

1. **Format daty/godziny** w CLI: `YYYY-MM-DDTHH:MM`. Parsowany przez Twoją klasę czasu (np. `LocalDateTime.parse(...)`).
2. **Kolizje rezerwacji**: zakresy `[start, end)` nakładają się, jeśli `startA < endB && startB < endA` (porównania przez metody Twojej klasy czasu, np. `isBefore`).
3. **Cennik**: bazowo `pricePerMinute = hourlyRate / 60`. Całość `minutes × pricePerMinute` → skala 2, `RoundingMode.HALF_UP`.
4. **Happy Hours**: –30% (najprościej: cała rezerwacja ma rabat, gdy `start` ∈ HH).
5. **Płatności (wersja podstawowa)**: `capture()` zmienia `status` na `CAPTURED`. Zwroty są **opcjonalne**.
6. **Raporty (opcjonalne)**: upraszczamy dostępność zasobów do 24/7 (brak kalendarza świąt/godzin otwarcia).
7. **Identyfikatory**:
    - Rezerwacje: `BK-<yyyyMMdd>-<counter>` na podstawie `booking.start`,
    - Faktury: `INV-<yyyyMMdd>-<counter>` na podstawie `issueDate`.
8. **Pakiety (sugerowane):**
    - `time` (klasa/util do obsługi czasu),
    - `money`,
    - `domain` (user/resource/booking),
    - `pricing`,
    - `payment`,
    - `billing`,
    - `repo`,
    - `service`,
    - `cli`,
    - ⭐ `report`, `discount` — jeśli implementujesz rozszerzenia.

---

## 6) Plan pracy (subtaski)

1. `time/` — wybór klasy czasu (np. `LocalDateTime`); ewentualny `TimeUtils` do parsowania/formatowania.
2. `money/` — `Money`.
3. `domain/` — `User` + podklasy; `Resource` + podklasy; `BookingStatus`, `Booking`.
4. `repo/` — repozytoria in-memory.
5. `pricing/` — `PricingPolicy`, `StandardPricing`, `HappyHoursPricing`.
6. `service/` — `BookingService`, `PaymentService`, `BillingService`.
7. `cli/` — REPL: parser komend, czytelne komunikaty, formatowanie kwot.
8. ⭐ `discount/` — zniżki (`Discountable`, itd.).
9. ⭐ `report/` — `ReportingService`.
10. ⭐ Własna klasa czasu (`FFDateTime`) zamiast gotowej — dla chętnych.

---

## 7) Testy ręczne

### Testy **podstawowe (must pass)**

**Test 0 — Dane startowe**
- `ADD_ROOM "Sala Alfa" 12 80`
- `ADD_DESK "Hot-1" hot 25`
- `ADD_DEVICE "Projektor-1" 2 40`
- `ADD_USER INDIVIDUAL anna@ex.com "Anna Nowak"`
- `ADD_USER COMPANY biuro@acme.pl "ACME Sp. z o.o." 5211234567`
- `SET_PRICING STANDARD`
- **Spodziewane:** `LIST_RESOURCES`, `LIST_USERS` zwracają powyższe pozycje.

**Test 1 — Rezerwacja i płatność (overloading)**
- `BOOK biuro@acme.pl "Sala Alfa" 2025-09-15T10:00 2025-09-15T12:00` → `PENDING`, cena `160.00 PLN`.
- `CONFIRM <id>` → `CONFIRMED`.
- `PAY <id> CARD 4242` → `Payment captured method=CARD last4=4242` (lub podobny komunikat).
- `INVOICE <id>` → `Invoice total=160.00 PLN buyer=ACME Sp. z o.o.`.
- `BOOK biuro@acme.pl "Sala Alfa" 2025-09-16T09:00 90` → `120.00 PLN`.

**Test 2 — Kolizje**
- Mając `CONFIRMED` `10:00–12:00`, próba `11:00–13:00` → `ERROR: resource not available`.
- Równoległa rezerwacja innego zasobu — przechodzi.

**Test 3 — Happy Hours**
- `SET_PRICING HAPPY_HOURS`
- `BOOK anna@ex.com "Hot-1" 2025-09-17T14:00 2025-09-17T16:00` → około `35.00 PLN` (–30% od `50.00`).

**Test 6 — Ilość urządzeń**
- Dwie rezerwacje `Projektor-1` w tym samym czasie przy `quantity=2` — przechodzą; trzecia → błąd.

### Testy ⭐ opcjonalne (dla chętnych)

**Test 4 — Zniżki** (wymaga systemu zniżek)
- `SET_DISCOUNT STUDENT`
- `BOOK anna@ex.com "Sala Alfa" 2025-09-18T09:00 2025-09-18T11:00` → np. `128.00 PLN` (–20%).
- `BOOK biuro@acme.pl "Sala Alfa" 2025-09-18T12:00 2025-09-18T14:00` → `160.00 PLN` (bez zniżki).

**Test 5 — Anulowanie i refund** (wymaga `WalletPayment` + `refund()`)
- Po `PAY ... WALLET`, `CANCEL <id>` → `CANCELLED` + `refund processed method=WALLET`.

**Test 7 — Raporty** (wymaga `ReportingService`)
- `REPORT UTILIZATION 2025-09-15T00:00 2025-09-20T00:00` → procent obłożenia per zasób.
- `REPORT REVENUE 2025-09-01T00:00 2025-09-30T23:59` → suma i podział przychodów.

---

## 8) Opcjonalne rozszerzenia (dla chętnych)

W tej sekcji są elementy, które **podnoszą poziom trudności**, ale nie są wymagane do podstawowego zaliczenia:

1. **Własna klasa czasu** — zamiast gotowej (`LocalDateTime`) napisz własną, niemutowalną klasę (np. `FFDateTime implements Comparable`):
    - pola `private final int year, month, day, hour, minute`,
    - walidacja zakresów (uproszczony kalendarz: każdy miesiąc ma 30 dni),
    - własny parser formatu `YYYY-MM-DDTHH:MM` (`parse`), arytmetyka minut (`plusMinutes`, `minutesUntil`, `toEpochMinutes`) i `toString`,
    - w komentarzu opisz różnice względem `LocalDateTime` (kto waliduje, kto liczy minuty, czytelność).
      Dla ambitnych: prawdziwe długości miesięcy i lata przestępne.
2. **System zniżek** (`Discountable` + `NoDiscount`, `StudentDiscount`, `CompanyTierDiscount`) + komenda `SET_DISCOUNT` + Test 4.
3. **Portfel i refundy** (`WalletPayment`, `refund()`, status `REFUNDED`) + Test 5.
4. **Raporty** (`ReportingService`, komendy `REPORT ...`) + Test 7.
5. Dodatkowe filtry w CLI (`LIST_BOOKINGS` z parametrami, `LIST_RESOURCES TYPE=...`).

---

Powodzenia! 💪