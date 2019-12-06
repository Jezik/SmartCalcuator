import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.math.BigInteger;


public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Map<String, BigInteger> variables = new HashMap<>();

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
                    Utility.makeAssignment(variables, str);
                    break;
                case CALCULATION:
                    try {
                        List<String> inputList = Utility.prepareCalculationString(str);
                        inputList = Utility.convertToPolishNotation(inputList);
                        System.out.println(Utility.calculate(inputList, variables));
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case COMMAND:
                    nextIteration = Utility.executeCommand(str);
                    break;
                case CALL:
                    Utility.makeCall(str.split(" "), variables);
                    break;
            }
        }
    }
}