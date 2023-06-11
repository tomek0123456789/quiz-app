package app.Quiz.jwzpQuizappProject.service;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

//todo refactor it into a single bean in @Component
@Service
public class TimeService {
    private Clock clock = Clock.systemUTC();

    public void setClock(Clock clock){
        this.clock = clock;
    }

    public Instant getCurrentTime() {
        return clock.instant();
    }

    public Instant getFutureTime(Instant instant, Long timeAmount) {
        return instant.plusSeconds(timeAmount);
    }
}
