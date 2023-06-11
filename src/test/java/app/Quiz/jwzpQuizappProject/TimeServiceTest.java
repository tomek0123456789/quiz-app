package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.service.TimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TimeServiceTest {

    private TimeService timeService;
    private Clock clock;

    @BeforeEach
    void setUp() {
        // Ustawiamy zegar na konkretnej strefie czasowej dla testów
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        Instant instant = Instant.parse("2023-05-17T10:00:00Z");
        clock = Clock.fixed(instant, zoneId);

        timeService = new TimeService();
    }

    @Test
    void getCurrentTime_ReturnsCurrentTime() {
        timeService.setClock(clock);

        Instant currentTime = timeService.getCurrentTime();

        Instant expectedTime = Instant.parse("2023-05-17T10:00:00Z");
        assertEquals(expectedTime, currentTime);
    }

    @Test
    void getFutureTime_ReturnsFutureTime() {
        timeService.setClock(clock);

        Instant startTime = Instant.parse("2023-05-17T10:00:00Z");

        Instant futureTime = timeService.getFutureTime(startTime, 3600L);

        Instant expectedTime = Instant.parse("2023-05-17T11:00:00Z");
        assertEquals(expectedTime, futureTime);
    }
}

