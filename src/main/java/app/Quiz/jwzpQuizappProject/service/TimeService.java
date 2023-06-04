package app.Quiz.jwzpQuizappProject.service;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

//todo refactor it into a single bean in @Component
@Service
public class TimeService {
    // ASK todo how to implement that?

//    public TimeService(Clock clock) {
//        this.clock = clock;
//    }

//    @Bean
//    Clock clock() {
//        return ;
//    }

    private final Clock clock = Clock.systemUTC();

    public Instant getCurrentTime() {
        return clock.instant();
    }

    public Instant getFutureTime(Instant instant, Long timeAmount) {
        return instant.plusSeconds(timeAmount);
//        return clock.instant().plus(timeAmount, ChronoUnit.SECONDS);
    }
}
