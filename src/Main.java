import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/** Enum holds supported operations
 * ASSIGNMENT values to variables;
 * CALCULATION of the expression
 * COMMAND to get /help or /exit
 * CALL to print a value of a variable
 */
enum Operation {
    ASSIGNMENT,
    CALCULATION,
    COMMAND,
    CALL
}

/** Patterns storage
 * Contains Patterns for regexps, which are used to modify input strings
 * according to a proper operation
 */
class Patterns {
    final static Pattern removeSpaces = Pattern.compile("\\s+");
    final static Pattern checkIdentifier = Pattern.compile("[^a-zA-Z]+");
    final static Pattern onlyDigits = Pattern.compile("[0-9]+");
    final static Pattern onlyLetters = Pattern.compile("[a-zA-Z]+");
    final static Pattern minusToMinusPtn = Pattern.compile("\\s-{11}\\s|\\s-{9}\\s|\\s-{7}\\s|\\s-{5}\\s|\\s-{3}\\s"); //The odd number of minuses gives a minus
    final static Pattern minusToPlusPtn = Pattern.compile("\\s-{10}\\s|\\s-{8}\\s|\\s-{6}\\s|\\s-{4}\\s|\\s-{2}\\s"); //The even number of minuses gives a plus
    final static Pattern plusToPlusPtn = Pattern.compile("\\+{2,}"); //Converts '++++++++' to a single '+'
    final static Pattern spacesPtn = Pattern.compile("\\s{2,}"); //Removes extra spaces
}

public class Main {

    private static void makeAssignment(Map<String, Integer> map, String string) {
        Matcher removeSpacesMhr = Patterns.removeSpaces.matcher(string);
        string = removeSpacesMhr.replaceAll("");
        String[] array = string.split("=");
        if (array.length == 2) {
            Matcher checkIdentifierMhr = Patterns.checkIdentifier.matcher(array[0]);
            if (!checkIdentifierMhr.find()) {
                Matcher onlyDigitsMhr = Patterns.onlyDigits.matcher(array[1]);
                Matcher onlyLettersMhr = Patterns.onlyLetters.matcher(array[1]);
                if (onlyDigitsMhr.matches()) {
                    map.put(array[0], Integer.parseInt(array[1]));
                }
                else if (onlyLettersMhr.matches()) {
                    if (map.containsKey(array[1])) {
                        map.put(array[0], map.get(array[1]));
                    }
                    else {
                        System.out.println("Unknown variable");
                    }
                }
                else {
                    System.out.println("Invalid assignment");
                }
            }
            else {
                System.out.println("Invalid identifier");
            }
        }
        else {
            System.out.println("Invalid assignment");
        }
    }

    private static String prepareCalculationString(String str) {
        Matcher minusToMinusMhr = Patterns.minusToMinusPtn.matcher(str);
        str = minusToMinusMhr.replaceAll(" - ");
        Matcher minusToPlusMhr = Patterns.minusToPlusPtn.matcher(str);
        str = minusToPlusMhr.replaceAll(" + ");
        Matcher plusToPlusMhr = Patterns.plusToPlusPtn.matcher(str);
        str = plusToPlusMhr.replaceAll("+");
        Matcher spacesMhr = Patterns.spacesPtn.matcher(str);
        str = spacesMhr.replaceAll(" ");
        return str;
    }

    private static int calculate(String[] array, Map<String, Integer> vars) {
        int result = array[0].matches("[0-9]+") ? Integer.parseInt(array[0]) : vars.get(array[0]);
        for (int i = 1; i < array.length; ) {
            if ("+".equals(array[i])) {
                result += array[i+1].matches("[0-9]+") ? Integer.parseInt(array[i+1]) : vars.get(array[i+1]);
                i += 2;
            }
            else if ("-".equals(array[i])) {
                result -= array[i+1].matches("[0-9]+") ? Integer.parseInt(array[i+1]) : vars.get(array[i+1]);
                i += 2;
            }
        }
        return result;
    }

    private static boolean executeCommand(String command) {
        boolean nextIteration = true;
        if (command.equals("/exit")) {
            nextIteration = false;
            System.out.println("Bye!");
        }
        else if (command.equals("/help")) {
            System.out.println("The program calculates the result of an expression in a form \"X + Y - Z\"\n" +
                    "The program supports both unary and binary minus operators.\n" +
                    "Consider that the even number of minuses gives a plus, and the odd number of minuses gives a minus!\n" +
                    "Also supports variables, only latin letters are allowed.");
        }
        else {
            System.out.println("Unknown command");
        }

        return nextIteration;
    }

    private static void makeCall(String[] array, Map<String, Integer> vars) {
        if (array.length > 1) {
            System.out.println("Invalid expression");
        }
        else {
            if (array[0].matches("[0-9]+")) {
                System.out.println(array[0]);
            }
            else if (vars.containsKey(array[0])) {
                System.out.println(vars.get(array[0]));
            }
            else {
                System.out.println("Unknown variable");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Map<String, Integer> variables = new HashMap<>();

        boolean nextIteration = true;
        while (nextIteration) {
            String str = reader.readLine();

            if ("".equals(str)) {
                continue;
            }

            Operation operation;
            if (str.contains("=")) {
                operation = Operation.ASSIGNMENT;
            }
            else if (str.contains("+") || str.contains("-")) {
                operation = Operation.CALCULATION;
            }
            else if (str.startsWith("/")){
                operation = Operation.COMMAND;
            }
            else {
                operation = Operation.CALL;
            }

            switch (operation) {
                case ASSIGNMENT:
                    makeAssignment(variables, str);
                    break;
                case CALCULATION:
                    str = prepareCalculationString(str);
                    System.out.println(calculate(str.split(" "), variables));
                    break;
                case COMMAND:
                    nextIteration = executeCommand(str);
                    break;
                case CALL:
                    makeCall(str.split(" "), variables);
                    break;
            }
        }
    }
}