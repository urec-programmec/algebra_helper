import javax.script.ScriptException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws ScriptException {

        String reader = "";
        Scanner scanner = new Scanner(System.in);
        Loader.load();

        System.out.println("Input arithmetic expressions. Input \"q\" to exit.");
        reader = scanner.nextLine();

        while (!reader.equals("q")){
            try {
                System.out.println("Answer: " + VirtualMachine.Calculate(reader));
            }
            catch (Exception e){
                System.out.println("There is an error: " + e.getMessage());
            }
            System.out.println("\nNew expression:");
            reader = scanner.nextLine();
        }

        System.out.println("Goodbye!");


    }
}
