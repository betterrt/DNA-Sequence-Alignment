package cs570project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Efficient {
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
   // The minimum cost of alignment
   private int cost;

   public Efficient() {
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
      outputX = new StringBuilder();
      outputY = new StringBuilder();
   }

   public static void main(String[] args) {
      double beforeUsedMem = getMemoryInKB();
      double startTime = getTimeInMilliseconds();

      Efficient efficient = new Efficient();
      efficient.sequenceAlignment(args[0]);

      double afterUsedMem = getMemoryInKB();
      double endTime = getTimeInMilliseconds();
      double totalTime = endTime - startTime;
      double totalUsage = afterUsedMem - beforeUsedMem;
      try {
         efficient.getOutputFile(args[1], totalTime, totalUsage);
      } catch (IOException e) {
         e.printStackTrace();
      }
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
      System.out.println(cost);
      // First string alignment
      System.out.println(outputX);
      // Second string alignment
      System.out.println(outputY);
      // Time in Milliseconds
      System.out.println("time :" + time);
      // Memory in Kilobytes
      System.out.println("memory :" + memory);
      System.out.println(verifyCost(outputX.toString(), outputY.toString()));
      System.out.println("input X length : " + inputX.length());
      System.out.println("input Y length : " + inputY.length());
   }

   /**
    * Run the efficient version of sequence alignment
    * @param args The name of file
    */
   public void sequenceAlignment(String args) {
      getInputString(args);
      //StringBuilder[] output = alignment_DC(inputX.toString(), inputY.toString());
      alignment_DC(inputX.toString(), inputY.toString());
//      outputX = output[0].toString();
//      outputY = output[1].toString();
   }

   /**
    * Recurrence function
    * @param x input x
    * @param y input y
    * @return The array(call it res) of two output strings, res[0] is outputX,
    *          res[1] is outputY
    */
   public void alignment_DC(String x, String y) {
      // If x only has one letter, then x has to align with whole y, no more recurrence call
      // The same is true if y only has one letter
      if(x.length() <= 1 || y.length() <= 1) {
         basicDP(x, y);
         return;
      }

      StringBuilder[] res = new StringBuilder[2];
      res[0] = new StringBuilder();
      res[1] = new StringBuilder();

      // xMid is always in xLeft (xMid is 0-indexed)
      int xMid = (x.length() + 1 ) / 2 - 1;
      String xLeft = x.substring(0, xMid + 1);
      // Reverse xRight
      StringBuilder xRight = new StringBuilder(x.substring(xMid + 1));
      String xRightReverse = xRight.reverse().toString();
      // Reverse y
      StringBuilder yR = new StringBuilder(y);
      String yReverse = yR.reverse().toString();

      int[] leftAlignment = alignment_DP(xLeft, y);
      int[] rightAlignment = alignment_DP(xRightReverse, yReverse);
      // The split point of y (split is 1-indexed)
      int split = 0;
      int len = leftAlignment.length;
      int min = Integer.MAX_VALUE;
      // Find the split point such that gives the minimum alignment cost
      for(int i = 0; i < len; ++i) {
         if((leftAlignment[i] + rightAlignment[len - 1 - i]) < min) {
            min = leftAlignment[i] + rightAlignment[len - 1 - i];
            split = i;
         }
      }

      alignment_DC(x.substring(0, xMid + 1), y.substring(0, split));
      alignment_DC(x.substring(xMid + 1), y.substring(split));

   }

   /**
    * Use DP to calculate the sequence alignment cost column by column
    * @param x part of inputX
    * @param y part of inputY
    * @return the last column of DP array
    */
   public int[] alignment_DP(String x, String y) {
      int m = y.length();
      int n = x.length();
      int[] prev = new int[m + 1];
      int[] cur = new int[m + 1];
      // Initialize the first column
      for(int i = 0; i < m + 1; ++i) {
         cur[i] = i * gapPenalty;
      }
      for(int i = 1; i < n + 1; ++i) {
         // Copy cur in last iteration to prev in current iteration
         for(int k = 0; k < cur.length; ++k) {
            prev[k] = cur[k];
         }

         cur[0] = i * gapPenalty;
         for(int j = 1; j < m + 1; ++j){
            int mismatch = mismatchCost.get(x.charAt(i - 1) + y.charAt(j - 1));
            cur[j] = Math.min(mismatch + prev[j - 1], Math.min(gapPenalty + cur[j - 1],
                       gapPenalty + prev[j]));
         }
      }
      return cur;
   }

   // Used to handle basic situations(i.e. One of the string has length <= 1)
   public void basicDP(String x, String y) {
      int m = x.length();
      int n = y.length();
      int[][] dp = new int[m + 1][n + 1];
      for(int i = 0; i < m + 1; ++i) {
         Arrays.fill(dp[i], Integer.MAX_VALUE);
      }
      for (int i = 0; i < m + 1; ++i) {
         dp[i][0] = i * gapPenalty;
      }
      for (int j = 0; j < n + 1; ++j) {
         dp[0][j] = j * gapPenalty;
      }
      for (int i = 1; i < m + 1; ++i) {
         for (int j = 1; j < n + 1; ++j) {
            int mismatch = mismatchCost.get(x.charAt(i - 1) + y.charAt(j - 1));
            dp[i][j] = Math.min(mismatch + dp[i - 1][j - 1],
                  Math.min(gapPenalty + dp[i - 1][j], gapPenalty + dp[i][j - 1]));
         }
      }
      int i = x.length();
      int j = y.length();
      StringBuilder x_out = new StringBuilder(x);
      StringBuilder y_out = new StringBuilder(y);
      while(i != 0 || j != 0) {
         if(i != 0 && j != 0 && dp[i][j] == mismatchCost.get(x.charAt(i - 1) +
               y.charAt(j - 1)) + dp[i - 1][j - 1]) {
            i--;
            j--;
         } else if(i != 0 && dp[i][j] == gapPenalty + dp[i - 1][j]) {
            y_out.insert(j, "_");
            i--;
         } else if(j != 0 && dp[i][j] == gapPenalty + dp[i][j - 1]) {
            x_out.insert(i, "_");
            j--;
         }
      }

      outputX.append(x_out);
      outputY.append(y_out);
      cost += dp[m][n];
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
