
### 1. Wzorce Prezentacji (Presentation Patterns)


* **Model-View-Controller (MVC)**
    * **Gdzie:** Cała warstwa webowa aplikacji.
    * **Kod:**
        * **Controller:** Klasy z adnotacją `@RestController`, np. `UserController.java`, `QuizController.java`. Obsługują one żądania HTTP i sterują przepływem.
        * **Model:** Klasy encji i DTO, np. `UserModel.java`, `UserDto.java`. Reprezentują dane.
        * **View:** W przypadku REST API widokiem jest JSON generowany przez bibliotekę Jackson (domyślną w Springu) na podstawie zwracanych obiektów.
* **Front Controller**
    * **Gdzie:** Jest to wzorzec zaimplementowany "pod maską" przez Spring Framework jako `DispatcherServlet`.
    * **Kod:** wszystkie żądania (np. `/users`, `/quizzes`) trafiają do jednego punktu wejścia frameworka, który następnie deleguje je do odpowiednich metod w kontrolerach (np. `getSingleUser` w `UserController`).
* **Page Controller**
    * **Gdzie:** Poszczególne metody w kontrolerach pełnią rolę kontrolerów konkretnych stron/akcji.
    * **Kod:** Metoda `getSingleUser` w `UserController.java` obsługuje konkretne żądanie GET dla zasobu użytkownika.
* **Data Transfer Object (DTO)**
    * **Gdzie:** Obiekty służące do przesyłania danych między klientem a serwerem, oddzielające model bazy danych od widoku.
    * **Kod:** Widać to wyraźnie w klasach takich jak `RegisterDto.java`, `QuizDto.java` czy `LoginResponseEntity.java`. Kontrolery przyjmują i zwracają DTO zamiast czystych encji w wielu miejscach.

### 2. Wzorce Logiki Dziedziny (Domain Logic Patterns)

* **Service Layer (Warstwa Usług)**
    * **Gdzie:** Klasy z adnotacją `@Service`. Definiują one granice transakcji i koordynują operacje.
    * **Kod:** Klasy `UserService.java`, `QuizService.java`, `RoomService.java`. Np. metoda `addQuestionToQuiz` w `QuizService` waliduje uprawnienia, sprawdza limity i zapisuje dane.
* **Domain Model (Model Dziedziny)**
    * **Gdzie:** Encje nie są tylko kontenerami na dane (anemiczne), ale posiadają logikę biznesową.
    * **Kod:**
        * `QuizModel.java`: Metoda `updateQuizStatus()` sprawdza liczbę poprawnych pytań i aktualizuje status quizu.
        * `QuestionModel.java`: Metoda `updateQuestionStatus()` waliduje liczbę odpowiedzi.
        * `RoomModel.java`: Metoda `getMaxScore()` oblicza maksymalny wynik.

### 3. Wzorce Architektury Źródła Danych (Data Source Patterns)

* **Repository (Magazyn)**
    * **Gdzie:** Interfejsy rozszerzające `JpaRepository` lub `ListCrudRepository`.
    * **Kod:** `UserRepository.java`, `QuizRepository.java`. Udostępniają one interfejs kolekcji do operacji na bazie danych (`save`, `findById`, `delete`).
* **Data Mapper**
    * **Gdzie:** Implementowany przez bibliotekę **Hibernate** (używaną przez Spring Data JPA).
    * **Kod:** Adnotacje `@Entity`, `@Table`, `@Column` w klasach takich jak `UserModel.java` definiują mapowanie między obiektami Java a tabelami w relacyjnej bazie danych.

### 4. Wzorce Struktury Obiektowo-Relacyjnej (ORM Structural Patterns)

* **Identity Field (Pole Tożsamości)**
    * **Gdzie:** Każda encja posiada pole przechowujące ID z bazy danych.
    * **Kod:** Np. w `UserModel.java`: `@Id @GeneratedValue(...) private long id;`.
* **Foreign Key Mapping (Mapowanie Klucza Obcego)**
    * **Gdzie:** Mapowanie relacji między obiektami na klucze obce w bazie.
    * **Kod:**
        * W `QuizModel.java`: `@ManyToOne private UserModel owner;` – quiz ma jednego właściciela.
        * W `QuestionModel.java`: `@OneToMany List<AnswerModel> answers;`.
* **Association Table Mapping (Mapowanie Tabeli Asocjacji)**
    * **Gdzie:** Obsługa relacji wiele-do-wielu.
    * **Kod:** W `RoomModel.java`: `@ManyToMany Set<UserModel> participants;`. Hibernate automatycznie utworzy tabelę łączącą pokoje z uczestnikami.

### 5. Wzorce Zachowań ORM (ORM Behavioral Patterns)

* **Lazy Load (Opóźnione Ładowanie)**
    * **Gdzie:** Wstrzymywanie ładowania powiązanych danych do momentu ich użycia.
    * **Kod:** Jawnie użyte w `QuizModel.java`: `@ManyToMany(fetch = FetchType.LAZY, ...) Set<RoomModel> rooms;`. Dzięki temu pobranie quizu nie pobiera automatycznie wszystkich pokoi.
* **Unit of Work (Jednostka Pracy)**
    * **Gdzie:** Zarządzanie transakcjami (Spring Data JPA).
    * **Kod:** Chociaż nie widać jawnej klasy `UnitOfWork`, mechanizm ten działa w metodach serwisów. Np. w `UserService.saveUser` wywołanie `userRepository.save(user)` odbywa się w kontekście transakcyjnym (domyślnie w Spring Data), co gwarantuje atomowość operacji.

### 6. Inne Wzorce

* **Client Session State (Stan Sesji Klienta)**
    * **Gdzie:** Przechowywanie stanu uwierzytelnienia po stronie klienta (token JWT).
    * **Kod:** Klasa `SecurityConfig.java` ustawia politykę sesji na `SessionCreationPolicy.STATELESS`, a `TokenService.java` generuje i weryfikuje tokeny JWT przesyłane przez klienta.
* **Registry (Rejestr)**
    * **Gdzie:** Kontener wstrzykiwania zależności (Dependency Injection) Springa.
    * **Kod:** Adnotacje `@Service` (np. `UserService`) oraz `@Repository` rejestrują beany w kontekście aplikacji, który działa jak rejestr, umożliwiając ich wstrzykiwanie (np. w konstruktorze `UserController`).
* **Plugin**
    * **Gdzie:** Konfiguracja komponentów ładowana w czasie uruchamiania.
    * **Kod:** W `SecurityConfig.java` wstrzykiwana jest konfiguracja kluczy RSA (`RsaKeyProperties`), co pozwala na wymianę implementacji lub konfiguracji bez zmiany kodu logiki.
