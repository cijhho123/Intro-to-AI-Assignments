package src.BayesNetwork;

import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;


public class Main {

    public static PrintStream outputStream = System.out;
    public static boolean isInLogMode = false;
    public static boolean isHumanPlayerDisplay = false;

    public static boolean RUNNING = true;

    //assumes the path to the file is provided as arg0 (in Java, the name of the program is not included in args[])
    public static void main(String[] args){
        System.out.println("starting...");

        parseFlags(args);

        
        File inputFile = inputChecks(args);

        if(inputFile == null){
            Scanner s = new Scanner(System.in);
            System.out.println("The program will now exit, press ENTER to close.");
            s.nextLine(); 
            s.close();
            return;
        }

        try {
            Enviroment env = new Enviroment(inputFile);
            env.init();
        } catch (Exception e){
            e.printStackTrace();
        } 
    }

    @SuppressWarnings("unused")
    private static void parseFlags(String[] args){
        String[] flags = {"-o", "-l"}; //o - output file, l - log mode

        for (int i=0; i<args.length; i++) {
            if(args[i].equals("-o")){
                try {
                    Main.outputStream = new PrintStream(new File(args[i+1]));
                } catch (Exception e) {
                    System.out.println("Invalid input! usage: -o OUTPUT_FILE_NAME");
                    e.printStackTrace();
                }

            } else if (args[i].equals("-l")){
                ;
            }
        }
    }

    //check for input file validity, return NULL if there's a problem
    private static File inputChecks(String [] args){
        // check for existence of arguments
        if(args.length == 0){
            System.out.println("The program expects the path to the input file to be provided as the first argument.\n");
            return null;
        }

        File inputFile = new File(args[0]);

        //check for existence of the input file
        if(! inputFile.exists()){
            System.out.println("ERROR! no such file exists. File: " + args[0]);
            return null;
        }

        //check for reading perms of the input file
        if(! inputFile.canRead()){
            System.out.println("ERROR! the file exists, but can't read from it.  File: " + args[0]);
            return null;
        }
        
        return inputFile;
    }
}

