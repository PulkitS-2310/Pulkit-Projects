package edu.iastate.cs2280.hw4;

/*
 * @author:Pulkit Saxena
 */

import java.util.Stack;

public class MsgTree
{
	/*
	 * It recognizes the character that has been passed in the code. 
	 */
	public char payloadChar;
	/*
	 * It represents the left subtree
	 */
	public MsgTree left;
	/*
	 * It represents the right subtree
	 */
	public MsgTree right;
	
	/*
	 * It constructs a tree from the given string 
	 * @param encodingString: It contains the input that is provided by the user 
	 */
	public MsgTree(String encodingString)
	 {
			if (encodingString == null) {
			        throw new IllegalArgumentException();
			    }

			    Stack<MsgTree> stack = new Stack<>();
			    MsgTree root = null;
			    MsgTree CurrNode = null;
                int index =0;
			    for (int i = 0; i < encodingString.length(); i++) {
			        char CurrChar = encodingString.charAt(i);

			        if (CurrChar == '^') {
			           
			            MsgTree newNode = new MsgTree(encodingString.charAt(index++));
			            if (root == null) {
			                root = newNode; 
			            }
			            if (CurrNode != null) {
			               
			                if (CurrNode.left == null) {
			                    CurrNode.left = newNode;
			                } else {
			                    CurrNode.right = newNode;
			                }
			            }
			            stack.push(CurrNode); 
			            CurrNode = newNode; 
			        } else {
			           
			            MsgTree newNode = new MsgTree(encodingString.charAt(index++));
			            if (CurrNode.left == null) {
			                CurrNode.left = newNode;
			            } else if (CurrNode.right == null) {
			                CurrNode.right = newNode;
			            }

			            
			            while (!stack.isEmpty() && CurrNode.left != null && CurrNode.right != null) {
			                CurrNode = stack.pop();
			            }
			        }
			    }

			    
			    if (root != null) {
			        this.payloadChar = root.payloadChar;
			        this.left = root.left;
			        this.right = root.right;
			    } else {
			        throw new IllegalStateException();
			    }
		}
			
    /*
     * It constructs a tree from a given character
     * @param payloadChar: It takes in the character that is provided by the user.
     */
	public MsgTree(char payloadChar)
	{
		this.payloadChar=payloadChar;
		this.left=null;
		this.right=null;
	}
	
	/*
	 * It prints out the character code onto the main screen
	 * @param root: It takes the root value as an input from the user and uses it in this code.
	 * @param code: It takes in each line of the file and helps in printing out the message.  
	 */
	public static void printCode(MsgTree root, String code)
	{
		    if (root == null) {
		        return; 
		    }

		    
		    if (root.payloadChar != '^') {
		       
		        if (root.payloadChar == '\n') 
				{
					System.out.print("\\" + "n" + "\t\t\t"); 
				}
				else
				{
					System.out.print(root.payloadChar + "\t\t\t");
				}
				System.out.println(code); 
		    }

		    
		    printCode(root.left, code + "0"); 
		    printCode(root.right, code + "1"); 
		}
	
	/*
	 * This method is used to decode the message
	 * @param root: It takes in the value of the root from the main method
	 * @param encodedMessage:It takes in the message from the user and helps in decoding the message
	 * @return: It converts the decoded message into a string and then returns it.
	 */
	public static String decode(MsgTree root, String encodedMessage) {
	    StringBuilder decoded = new StringBuilder(); 
	    MsgTree Node = root; 

	    
	    for (int i=0; i<encodedMessage.length();i++) {
	    	char c = encodedMessage.charAt(i); 
	       
	        if (c == '0') 
	        {
	            Node = Node.left;
	        } 
	        else if (c == '1') 
	        {
	            Node = Node.right;
	        }

	        
	        if (Node.left == null || Node.right == null) {
	            decoded.append(Node.payloadChar); 
	            Node = root; 
	        }
	    }
	    if(Node != root)
	    {
	    	decoded.append(Node.payloadChar);
	    }

	    return decoded.toString(); 
	}
	
	/*
	 * This method is used to print out the statistics
	 * @param endcodedMessage: It takes the message from the main method.
	 * @param decodedMessage: It takes the decoded message from the main method.
	 */
	public static void Statistics(String encodedMessage, String decodedMessage) {
	    // It represents the total number of characters in the decoded message. 
	    int totalChar = decodedMessage.length();

	    // It tells us about the number of compressed bits in the encoded message
	    int compressedBits = encodedMessage.length();

        //It tells us about the number of uncompressed bits in the message 
	    int uncompressedBits = totalChar * 16;

	    // It gives us the number of average bits per character
	    double avgBits = (double) compressedBits / totalChar;

	    // It tells us about the amount of space that has been saved `
	    double space = (1 - (double) compressedBits / uncompressedBits) * 100;

	    
	    System.out.println("\nSTATISTICS:");
	    System.out.println("-------------------------");
	    System.out.printf("Avg bits/char:\t\t%.2f%n", avgBits);
	    System.out.printf("Total characters:\t%d%n", totalChar);
	    System.out.printf("Space savings:\t\t%.2f%%%n", space);
	}


}

