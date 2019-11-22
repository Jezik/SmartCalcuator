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

    private static String prepareCalculationString(Pattern minusToMinus, Pattern minusToPlus, Pattern plusToPlus, Pattern spaces, String str) {
        Matcher minusToMinusMhr = minusToMinus.matcher(str);
        str = minusToMinusMhr.replaceAll(" - ");
        Matcher minusToPlusMhr = minusToPlus.matcher(str);
        str = minusToPlusMhr.replaceAll(" + ");
        Matcher plusToPlusMhr = plusToPlus.matcher(str);
        str = plusToPlusMhr.replaceAll("+");
        Matcher spacesMhr = spaces.matcher(str);
        str = spacesMhr.replaceAll(" ");
        return str;
    }

    private static int calculate(String[] array) throws Exception {
        int result = Integer.parseInt(array[0]);
        for (int i = 1; i < array.length; ) {
            if ("+".equals(array[i])) {
                result += Integer.parseInt(array[i+1]);
                i += 2;
            }
            else if ("-".equals(array[i])) {
                result -= Integer.parseInt(array[i+1]);
                i += 2;
            }
            else {
                throw new Exception();
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Map<String, Integer> variables = new HashMap<>();

        Pattern minusToMinusPtn = Pattern.compile("\\s-{11}\\s|\\s-{9}\\s|\\s-{7}\\s|\\s-{5}\\s|\\s-{3}\\s"); //The odd number of minuses gives a minus
        Pattern minusToPlusPtn = Pattern.compile("\\s-{10}\\s|\\s-{8}\\s|\\s-{6}\\s|\\s-{4}\\s|\\s-{2}\\s"); //The even number of minuses gives a plus
        Pattern plusToPlusPtn = Pattern.compile("\\+{2,}"); //Converts '++++++++' to a single '+'
        Pattern spacesPtn = Pattern.compile("\\s{2,}"); //Removes extra spaces

        boolean nextIteration = true;
        while (nextIteration) {
            String str = reader.readLine();
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
                    for (Map.Entry<String, Integer> entry : variables.entrySet()) {
                        System.out.println(entry.getKey() + "->" + entry.getValue());
                    }
                    break;
                case CALCULATION:
                    System.out.println("Calculation string");
                    break;
                case COMMAND:
                    System.out.println("Command string");
                    break;
                case CALL:
                    System.out.println("Call string");
                    break;
            }


            /*if ("".equals(str)) {
                continue;
            }*/

            if (str.equals("/exit")) {
                nextIteration = false;
                System.out.println("Bye!");
            }
            /*else if (str.equals("/help")) {
                System.out.println("The program calculates the result of an expression in a form \"X + Y - Z\"\n" +
                        "The program supports both unary and binary minus operators.\n" +
                "Consider that the even number of minuses gives a plus, and the odd number of minuses gives a minus! ");
            }
            else if (str.matches("/\\w*")) {
                System.out.println("Unknown command");
            }
            else {
                str = prepareCalculationString(minusToMinusPtn, minusToPlusPtn, plusToPlusPtn, spacesPtn, str);
                String[] array = str.split(" ");
                try {
                    System.out.println(calculate(array));
                }
                catch(Exception e) {
                    System.out.println("Invalid expression");
                }
            }*/
        }
    }
}


//TODO: Stage VI. Support variables
//  1. Check what input string is (maybe use enum ans switch for a proper operation):
//    -assignment (has '=')
//    -calculation (has '+' or '-')
//    -command (has '/' as a first symbol)
//  2. Implement variables and check their names:
//    -only latin letters (upper and lower; a1 -> 'Invalid identifier')
//    -check if assignment is proper (only one '=' on a string; a = 5 and a=5 are valid; a = a23d -> 'Invalid assignment')
//    -constant storage for all variables
//    -variable could be assigned to a variable (a = b, if b is unknown "Invalid variable')
//    -check that variable exists
//  3. Old support of simple examples
//  4. Support of commands
//  5. /help command