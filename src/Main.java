import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
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
    final static Pattern tooMuchAsterics = Pattern.compile("\\*{2,}");
    final static Pattern tooMuchSlashes = Pattern.compile("\\/{2,}");
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

    private static List<String> prepareCalculationString(String str) {
        Matcher tooMuchAstericsMhr = Patterns.tooMuchAsterics.matcher(str);
        Matcher tooMuchSlashesMhr = Patterns.tooMuchSlashes.matcher(str);
        if (tooMuchAstericsMhr.find() || tooMuchSlashesMhr.find()) {
            throw new IllegalArgumentException("Invalid expression");
        }
        Matcher minusToMinusMhr = Patterns.minusToMinusPtn.matcher(str);
        str = minusToMinusMhr.replaceAll(" - ");
        Matcher minusToPlusMhr = Patterns.minusToPlusPtn.matcher(str);
        str = minusToPlusMhr.replaceAll(" + ");
        Matcher plusToPlusMhr = Patterns.plusToPlusPtn.matcher(str);
        str = plusToPlusMhr.replaceAll("+");
        Matcher spacesMhr = Patterns.removeSpaces.matcher(str);
        str = spacesMhr.replaceAll("");

        int prevType = -1;
        int startIndex = 0;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(' || str.charAt(i) == ')') {

                list.add(str.substring(startIndex, i));
                prevType = -1;
                startIndex = i;
                if (i == str.length() - 1) {
                    list.add(str.substring(i));
                }
                continue;
            }

            int type = Character.getType(str.charAt(i));

            if (i == 0) {
                prevType = type;
                continue;
            }

            if (type == prevType) {
                if (i == str.length() - 1) {
                    list.add(str.substring(startIndex));
                }
            }
            else {
                list.add(str.substring(startIndex, i));
                prevType = type;
                startIndex = i;
                if (i == str.length() - 1) {
                    list.add(str.substring(i));
                }
            }
        }
        return list;
    }

    private static List<String> convertToPolishNotation(List<String> inputList) {
        List<String> polishList = new ArrayList<>();
        Deque<String> stack = new LinkedList<>();
        for (String word : inputList) {
            if (word.matches("\\d+") || word.matches("[a-z]+")) {
                polishList.add(word);
            }
            else if ("+".equals(word) || "-".equals(word)) {
                if (stack.size() == 0) {
                    stack.offerLast(word);
                }
                else {
                    if ("(".equals(stack.peekLast())) {
                        stack.offerLast(word);
                    }
                    else {
                        while (!"(".equals(stack.peekLast()) && stack.size() != 0) {
                            polishList.add(stack.pollLast());
                        }
                        stack.offerLast(word);
                    }
                }
            }
            else if ("*".equals(word) || "/".equals(word)) {
                if (stack.size() == 0) {
                    stack.offerLast(word);
                }
                else {
                    if ("*".equals(stack.peekLast()) || "/".equals(stack.peekLast())) {
                        while ("*".equals(stack.peekLast()) || "/".equals(stack.peekLast()) || stack.size() != 0) {
                            polishList.add(stack.pollLast());
                        }
                        stack.offerLast(word);
                    }
                    else {
                        stack.offerLast(word);
                    }
                }
            }
            else if ("(".equals(word)) {
                stack.offerLast(word);
            }
            else if (")".equals(word)) {
                while (true) {
                    if (stack.size() == 0) {
                        throw new IllegalArgumentException("Invalid expression");
                    }
                    polishList.add(stack.pollLast());
                    if ("(".equals(stack.peekLast())) {
                        stack.pollLast();
                        break;
                    }
                }
            }
        }

        while (stack.size() > 0) {
            if ("(".equals(stack.peekLast())) {
                throw new IllegalArgumentException("Invalid expression");
            }
            polishList.add(stack.pollLast());
        }

        return polishList;
    }

    private static int calculate(List<String> list, Map<String, Integer> vars) {
        Deque<Integer> resultStack = new LinkedList<>();
        for (String item : list) {
            if (item.matches("\\d+")) {
                resultStack.offerLast(Integer.parseInt(item));
            }
            else if (item.matches("[a-z]+")) {
                resultStack.offerLast(vars.get(item));
            }
            else {
                switch (item) {
                    case "+":
                        int a = resultStack.pollLast();
                        int b = resultStack.pollLast();
                        resultStack.offerLast(a + b);
                        break;
                    case "-":
                        a = resultStack.pollLast();
                        b = resultStack.pollLast();
                        resultStack.offerLast(b - a);
                        break;
                    case "*":
                        a = resultStack.pollLast();
                        b = resultStack.pollLast();
                        resultStack.offerLast(a * b);
                        break;
                    case "/":
                        a = resultStack.pollLast();
                        b = resultStack.pollLast();
                        resultStack.offerLast(b / a);
                        break;
                }
            }
        }
        return resultStack.pollLast();
    }

    private static boolean executeCommand(String command) {
        boolean nextIteration = true;
        if (command.equals("/exit")) {
            nextIteration = false;
            System.out.println("Bye!");
        }
        else if (command.equals("/help")) {
            System.out.println("The program calculates the result of an expression in a form \"X + Y - Z\"\n" +
                    "Supports '+', '-', '*'and '/' operations\n" +
                    "Supports both unary and binary minus operators.\n" +
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
            if (array[0].matches("-*[0-9]+")) {
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
            else if (str.contains("+") || (str.contains("-") && str.charAt(0) != '-') || str.contains("*") || (str.contains("/") && str.charAt(0) != '/')) {
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
                    try {
                        List<String> inputList = prepareCalculationString(str);
                        inputList = convertToPolishNotation(inputList);
                        System.out.println(calculate(inputList, variables));
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
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