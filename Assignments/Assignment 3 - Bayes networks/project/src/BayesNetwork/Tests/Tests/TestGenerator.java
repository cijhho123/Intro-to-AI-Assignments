package src.BayesNetwork.Tests.Tests;

import java.util.Random;
import java.util.Scanner; 

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;
import java.nio.file.Paths;  

public class TestGenerator {

    private static final Scanner scanner = new Scanner(System.in);
    private static FileWriter writer;
    private static final Random rnd = new Random();

    public static void main(String[] args) {

        System.out.println("Enter the name of the test file to create: ");
        String fileName = scanner.nextLine();
        String path = null;
        File outputFIle;

        try {
            path = Paths.get(".").toAbsolutePath() + "/tests/" +fileName + ".txt";

            outputFIle = new File(path);

            if(outputFIle.exists()){
                exit("ERROR! File with such name already exists: " + fileName,1);
            }

            outputFIle.createNewFile();
            
            if(!outputFIle.canWrite()){
                exit("ERROR! Can't write to file: " + path,1);
            }

            writer = new FileWriter(path);

        } catch (Exception e){
            String errorMsg = "Error creating the file in path: " + path + "\nStack trace:";
            exit(errorMsg + e.getStackTrace(), 1);
        }
        
        System.out.println("Use default values? Y/N");
        char choice = scanner.next().charAt(0);
        
        

        if(choice == 'Y' || choice == 'y'){
            try {
                useDefaults();
            } catch (Exception e){
                exit("Error writing default values to file " + fileName, 1);
            }
        } else {
            exit("Not implemented yet! can only use default values.", 2);
        }

        exit("The file was created successfully under tests/" +fileName + ".txt", 0);

    }

    private static void useDefaults() throws IOException{
        writeVertices(DefaultValues.VERTICES);
        writeNewLine();

        writeEdges(DefaultValues.VERTICES, DefaultValues.EDGE_PROBABILITY_VERY_VERY_DENSE, DefaultValues.EDGE_WEIGHT_LOWER_BOUND, 
            DefaultValues.EDGE_WEIGHT_UPPER_BOUND);
        writeNewLine();

        writePackages(DefaultValues.VERTICES, DefaultValues.PACKAGES_COUNT_LOW, DefaultValues.PACKAGES_DEPLOY_TIME_LOWER_BOUND,
            DefaultValues.PACKAGES_DEPLOY_TIME_UPPER_BOUND, DefaultValues.PACKAGES_DELIVERY_TIME_LOWER_BOUND, 
            DefaultValues.PACKAGES_DELIVERY_TIME_UPPER_BOUND);
        writeNewLine();

        writeBlockedEdges(DefaultValues.VERTICES, DefaultValues.BLOCKED_EDGE_PROBABILITY_MEDIUM);
        writeNewLine();

        writeFragileEdges(DefaultValues.VERTICES, DefaultValues.FRAGILE_EDGE_PROBABILITY_MEDIUM);
        writeNewLine();

        writeStupidGreedyAgents(DefaultValues.VERTICES, DefaultValues.STUPID_GREEDY_PLAYERS_COUNT);
        writeNewLine();

        writeHumanAgents(DefaultValues.VERTICES, DefaultValues.HUMAN_PLAYERS_COUNT);
        writeNewLine();

        writeInterferingAgents(DefaultValues.VERTICES, DefaultValues.INTERFERING_PLAYERS_COUNT);
        writeNewLine();

        writeGreedyAgents(DefaultValues.VERTICES, DefaultValues.GREEDY_AGENT_COUNT);
        writeNewLine();

        writeAStarAgents(DefaultValues.VERTICES, DefaultValues.A_STAR_AGENT_COUNT, 
            DefaultValues.A_STAR_LIMIT_LOWER_BOUND, DefaultValues.A_STAR_LIMIT_UPPER_BOUND);
        writeNewLine();

        writeRealTimeAStarAgents(DefaultValues.VERTICES, DefaultValues.REAL_TIME_A_STAR_AGENT_COUNT,
            DefaultValues.REAL_TIME_A_STAR_LOWER_BOUND, DefaultValues.REAL_TIME_A_STAR_UPPER_BOUND);
        writeNewLine();
    }

    private static void writeNewLine() throws IOException{
        writer.write("\n");
    }
        
    private static void writeVertices(int n) throws IOException{
        writer.write("; ------------- Edge count -------------\n");
        writer.write("#N " + Integer.toString(n));
    }

    //generate a random graph with probability of "probability" for every edge between two vertices to exists
    //and assign random value as weight from given range
    private static void writeEdges(int v, double probability, int weightLowerBound, int weightUpperBound) throws IOException{
        writer.write("; ------------- Edges -------------\n");

        int edgeCounter = 1;

        for(int i = 1; i <= v; i++){
            for(int j = 1; j < i; j++){
                if(rnd.nextDouble() < probability){
                    int weight = rnd.nextInt((weightUpperBound - weightLowerBound)) + weightLowerBound;
                    writer.write("#E" + edgeCounter + " " + i + " " + j + " W" + weight + "\n");

                    edgeCounter ++;
                }
            }
        }
    }

    private static void writePackages(int v, int packageCount, int deployLowerBound, int deployUpperBound, 
        int deliveryLowerBound, int deliveryUpperBound) throws IOException{
            writer.write("; ------------- Packages -------------\n");

            for(int i = 1; i <= packageCount; i++){
                int sourceLocation = rnd.nextInt(v) + 1;
                int destLocation = rnd.nextInt(v) + 1;

                int deployTime = rnd.nextInt((deployUpperBound - deployLowerBound)) + deployLowerBound;
                int deliveryTime = rnd.nextInt((deliveryUpperBound - deliveryLowerBound)) + deliveryLowerBound;

                writer.write("#P" + i + " V" + sourceLocation + " " + deployTime + " D V" + destLocation + " " + deliveryTime + "\n");
            }
    }

    private static void writeBlockedEdges(int v, double probability) throws IOException{
        writer.write("; ------------- Blocked edges -------------\n");

        for(int i = 1; i <= v; i++){
            for(int j = 1; j < i; j++){
                if(rnd.nextDouble() < probability){
                    writer.write("#B V" + i + " V" + j + "\n");
                }
            }
        }
    }

    private static void writeFragileEdges(int v, double probability) throws IOException{
        writer.write("; ------------- Fragile edges -------------\n");

        for(int i = 1; i <= v; i++){
            for(int j = 1; j < i; j++){
                if(rnd.nextDouble() < probability){
                    writer.write("#F V" + i + " V" + j + "\n");
                }
            }
        }
    }

    private static void writeStupidGreedyAgents(int v, int count) throws IOException{
        writer.write("; ------------- Stupid Greedy agents -------------\n");

        for(int i = 1; i <= count; i++){
            int location = rnd.nextInt(v) + 1;
            writer.write("#A V" + location + "\n");
        }
    }

    private static void writeHumanAgents(int v, int count) throws IOException {
        writer.write("; ------------- Human agents -------------\n");

        for(int i = 1; i <= count; i++){
            int location = rnd.nextInt(v) + 1;
            writer.write("#H V" + location);
        }
    }

    private static void writeInterferingAgents(int v, int count) throws IOException {
        writer.write("; ------------- Interfering agent -------------\n");

        for(int i = 1; i <= count; i++){
            int location = rnd.nextInt(v) + 1;
            writer.write("#I V" + location + "\n");
        }
    }

    private static void writeGreedyAgents(int v, int count) throws IOException{
        writer.write("; ------------- Greedy agent -------------\n");

        for(int i = 1; i <= count; i++){
            int location = rnd.nextInt(v) + 1;
            writer.write("#G V" + location + "\n");
        }
    }

    private static void writeAStarAgents(int v, int count, int lower, int upper) throws IOException{
        writer.write("; ------------- A Star agent -------------\n");

        for(int i = 1; i <= count; i++){
            int location = rnd.nextInt(v) + 1;
            int limit = rnd.nextInt(upper - lower) + lower;
            writer.write("#S V" + location + " " + limit  + "\n");
        }
    }

    private static void writeRealTimeAStarAgents(int v, int count, int lower, int upper) throws IOException{
        writer.write("; ------------- Real Time A Star agent -------------\n");

        for(int i = 1; i <= count; i++){
            int location = rnd.nextInt(v) + 1;
            int limit = rnd.nextInt(upper - lower) + lower;
            writer.write("#R V" + location + " " + limit  + "\n");
        }
    }

    

    private static void exit(String msg, int returnCode){
        System.out.println(msg);
        System.out.println("The program will now exit. Press ENTER to contiue.");
        scanner.nextLine();
        scanner.close();

        try {
            writer.close();
        } catch (IOException e) {
        }

        System.exit(returnCode);
    }
}
