import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Main {

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

        Pattern minusToMinusPtn = Pattern.compile("\\s-{11}\\s|\\s-{9}\\s|\\s-{7}\\s|\\s-{5}\\s|\\s-{3}\\s"); //The odd number of minuses gives a minus
        Pattern minusToPlusPtn = Pattern.compile("\\s-{10}\\s|\\s-{8}\\s|\\s-{6}\\s|\\s-{4}\\s|\\s-{2}\\s"); //The even number of minuses gives a plus
        Pattern plusToPlusPtn = Pattern.compile("\\+{2,}"); //Converts '++++++++' to a single '+'
        Pattern spacesPtn = Pattern.compile("\\s{2,}"); //Removes extra spaces

        boolean nextIteration = true;
        while (nextIteration) {
            String str = reader.readLine();
            if ("".equals(str)) {
                continue;
            }

            if (str.equals("/exit")) {
                nextIteration = false;
                System.out.println("Bye!");
            }
            else if (str.equals("/help")) {
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
            }
        }
    }
}
