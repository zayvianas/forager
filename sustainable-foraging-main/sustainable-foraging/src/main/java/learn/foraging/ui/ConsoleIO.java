package learn.foraging.ui;

import learn.foraging.models.Forager;
import learn.foraging.ui.State;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
@Component
public class ConsoleIO {

    private static final String INVALID_NUMBER
            = "[INVALID] Enter a valid number.";
    private static final String NUMBER_OUT_OF_RANGE
            = "[INVALID] Enter a number between %s and %s.";
    private static final String REQUIRED
            = "[INVALID] Value is required.";
    private static final String INVALID_DATE
            = "[INVALID] Enter a date in MM/dd/yyyy format.";
    private static final String INVALID_STATE
            = "[INVALID] Enter a two letter state abbreviation.";

    private final Scanner scanner = new Scanner(System.in);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public void print(String message) {
        System.out.print(message);
    }

    public void println(String message) {
        System.out.println(message);
    }

    public void printf(String format, Object... values) {
        System.out.printf(format, values);
    }

    public String readString(String prompt) {
        String input;
        while(true){
            print(prompt);
            input = scanner.nextLine();
            if (!input.contains(",")){
                return input;
            }
            println("Sorry. Commas are not allowed as part of the input. Please try another input.");
        }
    }

    public String readRequiredString(String prompt) {
        while (true) {
            String result = readString(prompt);
            if (!result.isBlank()) {
                return result;
            }
            println(REQUIRED);
        }
    }

    public String readRequiredState(String prompt){
        while (true) {
            boolean isInEnum = false;
            String input = readRequiredString(prompt);
            for(State state : State.values()){
                if (state.getAbbreviation().equalsIgnoreCase(input)){
                    return input;
                }
            }
            println(INVALID_STATE);
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readRequiredString(prompt));
            } catch (NumberFormatException ex) {
                println(INVALID_NUMBER);
            }
        }
    }

    public double readDouble(String prompt, double min, double max) {
        while (true) {
            double result = readDouble(prompt);
            if (result >= min && result <= max) {
                return result;
            }
            println(String.format(NUMBER_OUT_OF_RANGE, min, max));
        }
    }

    public int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readRequiredString(prompt));
            } catch (NumberFormatException ex) {
                println(INVALID_NUMBER);
            }
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            int result = readInt(prompt);
            if (result >= min && result <= max) {
                return result;
            }
            println(String.format(NUMBER_OUT_OF_RANGE, min, max));
        }
    }

    public boolean readBoolean(String prompt) {
        while (true) {
            String input = readRequiredString(prompt).toLowerCase();
            if (input.equals("y")) {
                return true;
            } else if (input.equals("n")) {
                return false;
            }
            println("[INVALID] Please enter 'y' or 'n'.");
        }
    }

    public LocalDate readLocalDate(String prompt) {
        while (true) {
            String input = readRequiredString(prompt);
            try {
                LocalDate date = LocalDate.parse(input, formatter);
                if (date.isAfter(LocalDate.now())){
                    println("Date cannot be a future date.");
                } else {
                    return date;
                }

            } catch (DateTimeParseException ex) {
                println(INVALID_DATE);
            }
        }
    }

    public BigDecimal readBigDecimal(String prompt) {
        while (true) {
            String input = readRequiredString(prompt);
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException ex) {
                println(INVALID_NUMBER);
            }
        }
    }

    public BigDecimal readBigDecimal(String prompt, BigDecimal min, BigDecimal max) {
        while (true) {
            BigDecimal result = readBigDecimal(prompt);
            if (result.compareTo(min) >= 0 && result.compareTo(max) <= 0) {
                return result;
            }
            println(String.format(NUMBER_OUT_OF_RANGE, min, max));
        }
    }

    public String readStringWithBlank(String prompt, Forager forager){
        String input;
        while (true) {
            input = readString(prompt);
            if (input.isEmpty() && prompt.contains("First")){
                return forager.getFirstName();
            } else if (input.isEmpty() && prompt.contains("Last")) {
                return forager.getLastName();
            }

            try{
                Integer.parseInt(input);
                println("Sorry. You must enter something with letters in it as well.");
            } catch (NumberFormatException ex){
                return input;
            }
        }
    }

    public String readStateWithBlank(String prompt, Forager forager){
        while (true) {
            String input = readString(prompt);
            for(State state : State.values()){
                if (input.isBlank()){
                    return forager.getState();
                } else if (state.getAbbreviation().equalsIgnoreCase(input)){
                    return input;
                }
            }
            println(INVALID_STATE);
        }
    }
}
