
# Quiz-app
## Aplikacja Quiz-app polega na tworzeniu i wspolnym rozwiązywaniu quizów ze statystykami oraz rankingami.
Umożliwa tworzenie grup quizowych w ramach którcyh będzie można rozwiązywać wspólnie wybrany zbiór quizów. 
Grupa quizowa będzie mogła zaplanować rozpoczęcie rywalizacji, a pokój quizowy rozpocznie się automatycznie o wybranej dacie.
Po zakończeniu spotkania będzie możliwość zobaczenia rankigów rywalizacji.
Użytkownicy będą mogli rozwiązywać quizy stworzone i udostępnione przez innych uzytkowników. 

Endpointy
| path | Method | Desc |
| :---         |     :---:      |          ---: |
| /users  | GET     | zwrócenie wszystkich użytkowników    |
| /users/{id}     | GET       | zwrócenie konkretnego użytkownika      |
| /users/{id}  | POST     | stworzenie uzytkownika |
| /users/{id}  | PATCH     | edycja uzytkownika |
| /users/{id}  | DELETE     | usunięcie uzytkownika    |
| /quizes | GET | wszsytkie publiczne quizy |
| /quizes/{id} | GET | zwraca konkretny quiz |
| /quizes/{id} | POST | tworzy nowy quiz|
| /quizes/{id} | DELETE | usuwa quiz (jeśli uzytkonik autoryzowany) |
| /quizes/{id} | PUT | updatuje konkretny quiz |
| /rooms/{id} | GET | zwraca konkretny pokój z rozgrywką |
| /rooms/{id} | POST | tworzy pokój z rozgrywką |
| /rooms/{id} | PATCH | updatuje pokój (jeśli uzytkonik autoryzowany) |
| /rooms/{id} | DELETE | usuwa pokój (jeśli uzytkonik autoryzowany) |
| /stats/users/{id} | GET | Zwraca globalne statystyki dla uzytkwnika

