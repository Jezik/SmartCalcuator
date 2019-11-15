import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    private static int sum(String[] array) {
        int sum = 0;
        for (String number : array) {
            try {
                sum += Integer.parseInt(number);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return sum;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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
                System.out.println("The program calculates the sum of numbers");
            }
            else {
                String[] array = str.split(" ");
                System.out.println(sum(array));
            }
        }
    }
}
