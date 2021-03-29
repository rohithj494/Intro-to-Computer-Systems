import java.io.*;
import java.util.Scanner;

public class VMparser {
	String[] command;					//The command as written in asm
	String inputFile;				//Create file object if it exists
	Scanner scan;
	String path;

	//Constructor to initialise file parsing
	VMparser(String file) throws IOException{
		
		inputFile= file;						//Create file object if it exists
		scan= new Scanner(inputFile);			//Scanner to read from file
	}
	
	//Returns true if there are more commands in the program
	public boolean hasMoreCommands() {
		if (scan.hasNextLine())
			return true;
		else
			return false;
	}
	
	//Scans the next line if there is a next line split by whitespaces
	public void advance() {
		if (hasMoreCommands())
			command= scan.nextLine().split("[ \t]");
	}
	
	//Returns the command type of the line
	String commandType() {
		if (command[0].equals("push"))
			return "C_PUSH";
		else if(command[0].equals("pop"))
			return "C_POP";
		else
			return "C_ARITHMETIC";
	}
	
	//Returns the first argument of the line, I call it the command
	public String arg1(String commandtype, String[] command) {
		if (commandtype.equals("C_RETURN")) {
			return null;
		}
		else
			return command[0];
	}
	
	//For a non arithmetic statement, this function returns the second argument or the
	// "segment"
	public String arg2(String commandtype, String[] command) {
		if (commandtype.equals("C_RETURN") || commandtype.equals("C_ARITHMETIC")) {
			return null;
		}
		else
			return command[1];
	}
	
	//This argument returns the third argument which is an integer in the case of 
	//push and pop instructions
	public String arg3(String commandtype, String[] command) {
		if (commandtype.equals("C_RETURN") || commandtype.equals("C_ARITHMETIC")) {
			return null;
		}
		else
			return command[2];
	}
}
