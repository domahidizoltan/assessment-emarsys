package com.emarsys.assessment.calculator;


import com.emarsys.assessment.calculator.exception.DateCalculatorException;
import com.emarsys.assessment.calculator.exception.InvalidTurnaroundTimeException;
import com.emarsys.assessment.calculator.exception.OutOfWorkingHoursSubmissionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zoltan Domahidi
 * @date 2018-07-24
 *
 * The Due Date Calculator problem was solved by using TDD.
 * The test order follows the implementation order.
 */
public class DueDateCalculatorTest {

    private static final LocalDateTime TUESDAY_SUBMISSION_TIME = LocalDateTime.parse("2018-07-24T14:12:00");
    private static final LocalDateTime THURSDAY_SUBMISSION_TIME = LocalDateTime.parse("2018-07-26T14:12:00");
    private static final Duration TWO_DAYS_TURNAROUND = Duration.ofDays(2);


    private DateCalculator underTest;

    @BeforeEach
    public void setUp() {
        underTest = new DueDateCalculator();
    }


    @Test
    public void shouldReturnResolutionTime() throws DateCalculatorException {
        LocalDateTime actualResolutionTime = underTest.calculate(TUESDAY_SUBMISSION_TIME, TWO_DAYS_TURNAROUND);

        assertEquals(THURSDAY_SUBMISSION_TIME, actualResolutionTime);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "2018-07-24T08:59:59",
        "2018-07-24T17:00:01",
        "2018-07-21T13:00:00",
        "2018-07-22T16:50:00"
    })
    public void shouldThrowExceptionWhenReportedInNonWorkingHours(String nonWorkingHour) {
        LocalDateTime submitTime = LocalDateTime.parse(nonWorkingHour);

        assertThrows(OutOfWorkingHoursSubmissionException.class,
            () -> underTest.calculate(submitTime, TWO_DAYS_TURNAROUND));
    }

    @Test
    public void shouldThrowExceptionWhenSubmissionTimeIsNull() {
        assertThrows(OutOfWorkingHoursSubmissionException.class,
            () -> underTest.calculate(null, TWO_DAYS_TURNAROUND));
    }

    @Test
    public void shouldReturnResolutionTimeAfterWeekend() throws DateCalculatorException {
        LocalDateTime actualResolutionTime = underTest.calculate(THURSDAY_SUBMISSION_TIME, TWO_DAYS_TURNAROUND);

        LocalDateTime expectedResolutionTime = LocalDateTime.parse("2018-07-30T14:12:00");
        assertEquals(expectedResolutionTime, actualResolutionTime);
    }

    @Test
    public void shouldReturnResolutionTimeForNextDay() throws DateCalculatorException {
        LocalDateTime tuesdayEndOfDay = LocalDateTime.parse("2018-07-24T16:59:50");
        Duration fastTurnaround = Duration.ofHours(1);
        LocalDateTime actualResolutionTime = underTest.calculate(tuesdayEndOfDay, fastTurnaround);

        LocalDateTime expectedResolutionTime = LocalDateTime.parse("2018-07-25T09:59:50");
        assertEquals(expectedResolutionTime, actualResolutionTime);
    }

    private static Stream<Duration> invalidTurnaroundTimeProvider() {
        return Stream.of(
            Duration.ofMinutes(1),
            Duration.ofSeconds(1),
            Duration.ofMinutes(61),
            Duration.ZERO,
            Duration.ZERO.minusSeconds(1),
            null
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTurnaroundTimeProvider")
    public void shouldThrowExceptionIfTurnAroundTimeIsFractionOfHourOrInvalid(Duration invalidTurnaroundTime) {
        assertThrows(InvalidTurnaroundTimeException.class,
            () -> underTest.calculate(TUESDAY_SUBMISSION_TIME, invalidTurnaroundTime));
    }

    @Test
    public void shouldReturnResolutiontimeOnThursday() throws DateCalculatorException {
        Duration twoDaysAndTwoHoursTurnaround = Duration.ofHours(18);
        LocalDateTime actualResolutionTime = underTest.calculate(TUESDAY_SUBMISSION_TIME, twoDaysAndTwoHoursTurnaround);

        LocalDateTime expectedResolutionTime = LocalDateTime.parse("2018-07-26T16:12:00");
        assertEquals(expectedResolutionTime, actualResolutionTime);
    }

    @Test
    public void shouldSkipTwoWeekends() throws DateCalculatorException {
        LocalDateTime friday = LocalDateTime.parse("2018-07-27T14:00:00");
        Duration sixDaysTurnaround = Duration.ofDays(6);
        LocalDateTime actualResolutionTime = underTest.calculate(friday, sixDaysTurnaround);

        LocalDateTime expectedResolutionTime = LocalDateTime.parse("2018-08-06T14:00:00");
        assertEquals(expectedResolutionTime, actualResolutionTime);
    }

    @Test
    public void shouldSkipWeekendForFridayFastTurnaround() throws DateCalculatorException {
        LocalDateTime fridayEndOfDay = LocalDateTime.parse("2018-07-27T16:50:00");
        Duration fastTurnaround = Duration.ofHours(1);
        LocalDateTime actualResolutionTime = underTest.calculate(fridayEndOfDay, fastTurnaround);

        LocalDateTime expectedResolutionTime = LocalDateTime.parse("2018-07-30T09:50:00");
        assertEquals(expectedResolutionTime, actualResolutionTime);
    }

}
