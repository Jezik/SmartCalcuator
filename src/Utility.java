import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;

class Utility {
    static void makeAssignment(Map<String, BigInteger> map, String string) {
        Matcher removeSpacesMhr = Patterns.removeSpaces.matcher(string);
        string = removeSpacesMhr.replaceAll("");
        String[] array = string.split("=");
        if (array.length == 2) {
            Matcher checkIdentifierMhr = Patterns.checkIdentifier.matcher(array[0]);
            if (!checkIdentifierMhr.find()) {
                Matcher onlyDigitsMhr = Patterns.onlyDigits.matcher(array[1]);
                Matcher onlyLettersMhr = Patterns.onlyLetters.matcher(array[1]);
                if (onlyDigitsMhr.matches()) {
                    map.put(array[0], new BigInteger(array[1]));
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

    static List<String> prepareCalculationString(String str) {
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

    static List<String> convertToPolishNotation(List<String> inputList) {
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

    static BigInteger calculate(List<String> list, Map<String, BigInteger> vars) {
        Deque<BigInteger> resultStack = new LinkedList<>();
        for (String item : list) {
            if (item.matches("\\d+")) {
                resultStack.offerLast(new BigInteger(item));
            }
            else if (item.matches("[a-z]+")) {
                resultStack.offerLast(vars.get(item));
            }
            else {
                switch (item) {
                    case "+":
                        BigInteger a = resultStack.pollLast();
                        BigInteger b = resultStack.pollLast();
                        resultStack.offerLast(a.add(b));
                        break;
                    case "-":
                        a = resultStack.pollLast();
                        b = resultStack.pollLast();
                        resultStack.offerLast(b.subtract(a));
                        break;
                    case "*":
                        a = resultStack.pollLast();
                        b = resultStack.pollLast();
                        resultStack.offerLast(a.multiply(b));
                        break;
                    case "/":
                        a = resultStack.pollLast();
                        b = resultStack.pollLast();
                        resultStack.offerLast(b.divide(a));
                        break;
                }
            }
        }
        return resultStack.pollLast();
    }

    static boolean executeCommand(String command) {
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

    static void makeCall(String[] array, Map<String, BigInteger> vars) {
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
}
