package com.emarsys.assessment.calculator;

import com.emarsys.assessment.calculator.exception.DateCalculatorException;

import java.time.Duration;
import java.time.LocalDateTime;

public interface DateCalculator {

    LocalDateTime calculate(LocalDateTime time, Duration duration) throws DateCalculatorException;

}
