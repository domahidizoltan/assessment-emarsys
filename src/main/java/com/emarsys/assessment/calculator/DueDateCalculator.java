package com.emarsys.assessment.calculator;

import com.emarsys.assessment.calculator.exception.InvalidTurnaroundTimeException;
import com.emarsys.assessment.calculator.exception.OutOfWorkingHoursSubmissionException;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

public class DueDateCalculator implements DateCalculator {

    @Override
    public LocalDateTime calculate(LocalDateTime submissionTime, Duration turnaroundTime)
        throws OutOfWorkingHoursSubmissionException, InvalidTurnaroundTimeException {

        validateSubmissionTime(submissionTime);
        validateTurnaroundTime(turnaroundTime);

        long turnaroundHours = turnaroundTime.toHours();
        LocalDateTime resolutionTime = LocalDateTime.from(submissionTime);
        resolutionTime = forwardHours(resolutionTime, turnaroundHours);
        resolutionTime = forwardDays(resolutionTime, turnaroundHours);

        return resolutionTime;

    }

    private void validateSubmissionTime(LocalDateTime submissionTime) throws OutOfWorkingHoursSubmissionException {
        if (submissionTime == null || !isWorkDay(submissionTime) || !isWorkingHour(submissionTime)) {
            throw new OutOfWorkingHoursSubmissionException("Problems must be submitted from Monday to Friday between 9AM and 5PM!");
        }
    }

    private void validateTurnaroundTime(Duration turnaroundTime) throws InvalidTurnaroundTimeException {
        if (turnaroundTime == null || turnaroundTime.isZero() || turnaroundTime.isNegative()) {
            throw new InvalidTurnaroundTimeException("Turnaround time must be a positive time duration!");
        }

        long remainder = turnaroundTime.getSeconds() % Duration.ofHours(1).getSeconds();
        if (remainder != 0) {
            throw new InvalidTurnaroundTimeException("Turnaround time must be specified in hours!");
        }
    }

    private LocalDateTime forwardDays(LocalDateTime resolutionTime, long turnaroundHours) {
        long days = (turnaroundHours % 24 == 0)
            ? turnaroundHours / 24
            : turnaroundHours / 8;

        while (days > 0 || !isWorkDay(resolutionTime)) {
            if (isWorkDay(resolutionTime)) {
                days -= 1;
            }
            resolutionTime = resolutionTime.plusDays(1);
        }

        return resolutionTime;
    }

    private LocalDateTime forwardHours(LocalDateTime resolutionTime, long turnaroundHours) {
        long hours = turnaroundHours % 8;

        while (hours > 0 || !isWorkingHour(resolutionTime)) {
            if (isWorkingHour(resolutionTime)) {
                hours -= 1;
            }
            resolutionTime = resolutionTime.plusHours(1);
        }

        return resolutionTime;
    }

    private boolean isWorkingHour(LocalDateTime submissionTime) {
        LocalDateTime today9AM = submissionTime.withHour(9).withMinute(0).withSecond(0);
        LocalDateTime today5PM = submissionTime.withHour(17).withMinute(0).withSecond(0);
        return submissionTime.isAfter(today9AM) && submissionTime.isBefore(today5PM);
    }

    private boolean isWorkDay(LocalDateTime submissionTime) {
        DayOfWeek dayOfWeek = submissionTime.getDayOfWeek();
        return !(dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY));
    }

}
