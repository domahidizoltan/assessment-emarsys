package com.emarsys.assessment;

import com.emarsys.assessment.calculator.DateCalculator;
import com.emarsys.assessment.calculator.DueDateCalculator;
import com.emarsys.assessment.calculator.exception.DateCalculatorException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Scanner;

@SpringBootApplication
public class CalculatorApplication {

	private static final Scanner SCANNER = new Scanner(System.in);


	@Bean
	public DateCalculator dueDateCalculator() {
		return new DueDateCalculator();
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(CalculatorApplication.class, args);

		System.out.println(" ");
		System.out.println("This console reader is not fail-safe. Please use required formats.");
		LocalDateTime submissionTime = readSubmissionTime();
		Duration duration = readTurnaroundTime();
		System.out.println(" ");

		DateCalculator dateCalculator = ctx.getBean(DateCalculator.class);
		try {
			LocalDateTime resolutionTime = dateCalculator.calculate(submissionTime, duration);
			System.out.println("Resolution time is: " + resolutionTime.toString().replace('T', ' '));
		} catch (DateCalculatorException e) {
			System.out.println("Could not calculate resolution time: " + e.getMessage() );
		}

		System.out.println(" ");
	}

	private static LocalDateTime readSubmissionTime() {
		System.out.print("Enter submission time (YYYY-MM-DD HH:mm:ss): ");
		String submissionTimeString = SCANNER.nextLine();

		return LocalDateTime.parse(submissionTimeString.replace(' ', 'T'));
	}

	private static Duration readTurnaroundTime() {
		System.out.print("Enter turnaround time hours or days (1H, 2D): ");
		String turnaroundTimeString = SCANNER.nextLine();

		String unitString = turnaroundTimeString.substring(turnaroundTimeString.length() - 1);
		TemporalUnit unit = unitString.equals("D") ? ChronoUnit.DAYS : ChronoUnit.HOURS;

		String duration = turnaroundTimeString.substring(0, turnaroundTimeString.indexOf(unitString));

		return Duration.of(Integer.parseInt(duration), unit);
	}

}
