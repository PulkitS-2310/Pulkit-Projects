package edu.iastate.cs2280.hw4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide a filename as an argument.");
            return;
        }
        
        String filename = args[0]; 

        try {
            Scanner fileScanner = new Scanner(new File(filename));
            StringBuilder encodingBuilder = new StringBuilder();
            String compressedMessage = null;

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                
                if (line.matches("[01]+")) {
                    compressedMessage = line; 
                    break; 
                }

                encodingBuilder.append(line);
                encodingBuilder.append("\n");
            }

           
            String encodingString = encodingBuilder.toString().trim();

            
            MsgTree root = new MsgTree(encodingString);

         
            System.out.println("character code");
            System.out.println("-------------------------");
            MsgTree.printCode(root, "");

            
            if (compressedMessage != null) {
                System.out.println("\nMESSAGE: " + MsgTree.decode(root, compressedMessage));
                MsgTree.Statistics(encodingString, compressedMessage);
            } else {
                System.out.println("No message found in the file.");
            }
           fileScanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
        }
    }
}

