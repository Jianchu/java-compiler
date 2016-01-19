import java.io.FileReader;
import java.util.List;

import scanner.Scanner;
import scanner.Token;

public class Joosc {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Joosc <filename>");
            System.exit(-1);
        }

        Scanner scanner = null;
        List<Token> tokens = null;
        try {
            scanner = new Scanner(new FileReader(args[0]));
            tokens = scanner.scan();
        } catch (Exception e) {
            System.err.println(e);
            System.exit(-1);
        }

        // for testing purposes
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
