
/*
 * @author: Pulkit Saxena
 */
package hw3;

import java.util.ArrayList;

public class TwentyFour {
	/*
	 * It stores all the values
	 */
    private int values;
    /*
     * It stores all the expressions
     */
    private String expression;
    /*
     * @param numbers: It takes in a number from a main method and stores it in values.
     */
     public TwentyFour(int numbers) {
        values = numbers;
        expression = Integer.toString(values);
       }
     /*
      * This is an empty constructor.
      */
    public TwentyFour() {}
    /*
     * @param TwentyFour a: It takes the first number in the main method
     * @param TwentyFour b: It takes the second number from the main method
     * @param operation: It tells which mathematical operation has to be performed on the two numbers that have been taken.
     */
    public TwentyFour(TwentyFour a, TwentyFour b, char operation) {
        if (operation == '+') {
            values = a.values + b.values;
            expression = "(" + a.expression + "+" + b.expression + ")";
        } else if (operation == '-') {
            values = a.values - b.values;
            expression = "(" + a.expression + "-" + b.expression + ")";
        } else if (operation == '*') {
            values = a.values * b.values;
            expression = "(" + a.expression + "*" + b.expression + ")";
        } else if (operation == '/') {
            if (b.values != 0 && a.values % b.values == 0) {
                values = a.values / b.values;
                expression = "(" + a.expression + "/" + b.expression + ")";
            } else {
                values = Integer.MIN_VALUE; 
            }
        } else if (operation == '^') {
            values = (int) Math.pow(a.values, b.values);
            if(b.values==0)
            {
            	values=1;
            }
            expression = "(" + a.expression + "^" + b.expression + ")";
        }
    }
    /*
     * @return: it returns the number in its integral form.
     */
    public int intValue() {
        return values;
    }
   /*
    * @return: it returns the number in its expression form. 
    */
    public String toString() 
    {
        return expression;
    }
    /*
     *  @param expressions: It takes an arraylist from the main method.
     *  @param target: It takes a target value from the main method.
     *  @param result: It outputs the correct answer in the form of an arraylist after we reach the base case.
     */
    public static void generateTarget(ArrayList<TwentyFour> expressions, int target, ArrayList<String> result) {
        if (expressions.size() == 1) {
            TwentyFour one = expressions.get(0);
            if (one.intValue() == target) {
            	 System.out.println(result);
                if (!result.contains(one.toString())) {
                    result.add(one.toString());
                   
                }
            }
            
        }
        for (int i = 0; i < expressions.size(); i++) {
            for (int j =0; j < expressions.size(); j++) {
                if (i == j) 
                 {
                	continue;
                	
                 }
               
                TwentyFour a = expressions.get(i);
                TwentyFour b = expressions.get(j);
                if(j==target)
                {
                	result.add(a.toString());
                }
                
                ArrayList<TwentyFour> subList = new ArrayList<>(expressions);
                subList.remove(j);  
                if (i < j) {
                    subList.remove(i);
                } else {
                    subList.remove(i - 1);
                }               
                char[] operations = {'+', '-', '*', '/', '^'};
                for (char op : operations) {
                    
                    if (op == '/' && b.intValue() == 0) {
                        continue;
                    }

                    TwentyFour combined = new TwentyFour(a, b, op);
                    if (combined.intValue() == Integer.MIN_VALUE) 
                    {
                        continue; 
                    }
                    if (combined.intValue() == target) 
                    {
                        result.add(combined.toString());
                    }
                    subList.add(combined);
                    generateTarget(subList, target, result);
                    subList.remove(subList.size() - 1); 
                    }
            }
        }
    }
}
