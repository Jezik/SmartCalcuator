import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean nextIteration = true;

        while (nextIteration) {
            String str = reader.readLine();
            if (str.equals("/exit")) {
                nextIteration = false;
                System.out.println("Bye!");
                continue;
            }

            if (str.equals("")) {
                continue;
            }
            else {
                String[] array = str.split(" ");
                if (array.length == 1) {
                    System.out.println(array[0]);
                }
                else {
                    try {
                        int a = Integer.parseInt(array[0]);
                        int b = Integer.parseInt(array[1]);
                        System.out.println(a + b);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }
}
