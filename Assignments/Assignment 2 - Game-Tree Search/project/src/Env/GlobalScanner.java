package src.Env;

import java.util.Scanner;

//using the singelton pattern to have a global instance of of Java's scanner
public class GlobalScanner {
    private static Scanner self = new Scanner(System.in);

    public static Scanner get() { 
        return self; 
    }

    public static void close(){
        self.close();
    }
}
