package cs570project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Basic {
   // The mismatch cost between two letters; key is the sum of ASCII code
   // of two letters, value is the mismatch cost between them
   private static Map<Integer, Integer> mismatchCost;
   // The cost of a gap
   private static int gapPenalty;
   // Two input strings X & Y
   private StringBuilder inputX;
   private StringBuilder inputY;
   // Two output strings(i.e. The optimal solution)
   private StringBuilder outputX;
   private StringBuilder outputY;

   private int dp[][];

   public static void main(String[] args) {
      double beforeUsedMem = getMemoryInKB();
      double startTime = getTimeInMilliseconds();

      Basic basic = new Basic();
      basic.sequenceAlignment(args[0]);

      double afterUsedMem = getMemoryInKB();
      double endTime = getTimeInMilliseconds();
      double totalUsage = afterUsedMem-beforeUsedMem;
      double totalTime = endTime - startTime;
      try {
         basic.getOutputFile(args[1], totalTime, totalUsage);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public Basic() {
      mismatchCost = new HashMap<>();
      gapPenalty = 30;
      // A-A C-C G-G T-T: 0
      mismatchCost.put(130, 0);
      mismatchCost.put(134, 0);
      mismatchCost.put(142, 0);
      mismatchCost.put(168, 0);
      // A-C : 110
      mismatchCost.put(132, 110);
      // A-G : 48
      mismatchCost.put(136, 48);
      // A-T : 94
      mismatchCost.put(149, 94);
      // C-G : 118
      mismatchCost.put(138, 118);
      // C-T : 48
      mismatchCost.put(151, 48);
      // G-T : 110
      mismatchCost.put(155, 110);

      inputX = new StringBuilder();
      inputY = new StringBuilder();
   }

   /**
    * Read the input Strings inputX and inputY
    * @param input The name of input file
    */
   public void getInputString(String input) {
      String inputPath = "dailystudy\\src\\datapoints\\" + input;
      File inputFile = new File(inputPath);
      try (Scanner scanner = new Scanner(inputFile)) {
         inputX.append(scanner.next());
         while (scanner.hasNextInt()) {
            String cur = inputX.toString();
            inputX.insert(scanner.nextInt() + 1, cur);
         }

         inputY.append(scanner.next());
         while(scanner.hasNextInt()) {
            String cur = inputY.toString();
            inputY.insert(scanner.nextInt() + 1, cur);
         }
      } catch(FileNotFoundException e) {
         System.out.println("Input file path illegal");
      }
   }

   /**
    * Using basic DP method to find the optimal solution for sequence alignment
    * @param args The name of input file
    */
   public void sequenceAlignment(String args) {
      getInputString(args);
      int m = inputX.length();
      int n = inputY.length();
      // dp[i][j] represents the min cost of an alignment between X1...Xi &
      //  Y1...Yj(1-indexed)
      dp = new int[m + 1][n + 1];
      for(int i = 0; i < m + 1; ++i) {
         Arrays.fill(dp[i], Integer.MAX_VALUE);
      }
      // Base case : X is empty & Y
      //             Y is empty & X
      for (int i = 0; i < m + 1; ++i) {
         dp[i][0] = i * gapPenalty;
      }
      for (int j = 0; j < n + 1; ++j) {
         dp[0][j] = j * gapPenalty;
      }

      for (int i = 1; i < m + 1; ++i) {
         for (int j = 1; j < n + 1; ++j) {
            int mismatch = mismatchCost.get(inputX.charAt(i - 1) + inputY.charAt(j - 1));
            dp[i][j] = Math.min(mismatch + dp[i - 1][j - 1],
                  Math.min(gapPenalty + dp[i - 1][j], gapPenalty + dp[i][j - 1]));
         }
      }
   }

   /**
    * Generate output strings outputX and outputY
    */
   public void getOutputString() {
      int i = inputX.length();
      int j = inputY.length();
      outputX = new StringBuilder(inputX);
      outputY = new StringBuilder(inputY);
      while(i != 0 || j != 0) {

         if(i != 0 && j != 0 && dp[i][j] == mismatchCost.get(inputX.charAt(i - 1) +
               inputY.charAt(j - 1)) + dp[i - 1][j - 1]) {
            i--;
            j--;
         } else if(i != 0 && dp[i][j] == gapPenalty + dp[i - 1][j]) {
            outputY.insert(j, "_");
            i--;
         } else if(j != 0 && dp[i][j] == gapPenalty + dp[i][j - 1]) {
            outputX.insert(i, "_");
            j--;
         }
      }
   }

   /**
    * Write the results into output file
    */
   public void getOutputFile(String output, double time, double memory) throws IOException {
      String outputPath = "dailystudy\\src\\datapoints\\" + output;

      File outputFile = new File(outputPath);
      // If output file does not exist, the following function will create it
      // If output file exists, the function will just return false and do nothing
      outputFile.createNewFile();
      PrintStream ps = new PrintStream(outputPath);
      System.setOut(ps);
      // Cost of the alignment
      System.out.println(dp[inputX.length()][inputY.length()]);
      getOutputString();
      // First string alignment
      System.out.println(outputX);
      // Second string alignment
      System.out.println(outputY);
      // Time in Milliseconds
      System.out.println("time :" + time);
      // Memory in Kilobytes
      System.out.println("memory :" + memory);
      System.out.println("input X length : " + inputX.length());
      System.out.println("input Y length : " + inputY.length());
   }

   public int verifyCost(String s1, String s2) {
      int cost = 0;
      for(int i = 0; i < s1.length(); ++i) {
         if(s1.charAt(i) == '_' || s2.charAt(i) == '_') {
            cost += gapPenalty;
         } else {
            cost += mismatchCost.get(s1.charAt(i) + s2.charAt(i));
         }
      }
      return cost;
   }
   /**
    * Calculate the memory used by program
    */
   private static double getMemoryInKB() {
      double total = Runtime.getRuntime().totalMemory();
      return (total - Runtime.getRuntime().freeMemory())/10e3;
   }

   /**
    * Get current time to calculate the time used by program
    */
   private static double getTimeInMilliseconds() {
      return System.nanoTime()/10e6;
   }

}
