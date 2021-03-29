
import java.io.*;
import java.util.Scanner;

public class parser {
	String command;					//The command as written in asm
	String inputFile;				//Create file object if it exists
	Scanner scan;
	String path;

	//Constructor to initialise file parsing
	parser(String file) throws IOException{
		
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
	
	//Scans the next line if there is a next line
	public void advance() {
		if (hasMoreCommands())
			command= scan.nextLine();
	}
	
	//Returns the command type of the line
	String commandType() {
		if (command.charAt(0)=='@')
			return "A_COMMAND";
		else if(command.charAt(0)=='(')
			return "L_COMMAND";
		else
			return "C_COMMAND";
	}
	
	//In case of an A_COMMAND or L_COMMAND, returns the argument (Label, symbol or number)
	public String symbol(String commandtype, String command) {
		if (commandtype.equals("A_COMMAND")) {
			return command.substring(1);
		}
		else if (commandtype.equals("L_COMMAND")) {
			return command.substring(1, command.length()-1);
		}
		else
			return null;
	}
	
	//For a C_COMMAND, returns the dest part of the line if it exists
	public String dest(String commandtype, String command) {
		if (commandtype.equals("C_COMMAND")) {
			if (command.contains("=")) {
				return command.substring(0, command.indexOf('='));
			}
			else 
				return "missing";
		}
		else 
			return null;
	}
	
	//For a C_COMMAND, returns the comp part of the line
	public String comp(String commandtype, String command) {
		if (commandtype.equals("C_COMMAND")) {
			if (command.contains("=")) {
				if (command.contains(";")) {
					return command.substring((command.indexOf('=')+1), command.indexOf(';'));
				}
				else 
					return command.substring(command.indexOf('=')+1);
			}
			else {
				if (command.contains(";")){
					return command.substring(0, command.indexOf(';'));
				}
				else
					return command;
			}
		}
		else
			return null;
	}
	
	//For a C_COMMAND, returns the jmp part of the line if it exists
	public static String jmp(String commandtype, String command) {
		if (commandtype.equals("C_COMMAND")) {
			if (command.contains(";")) {
				return command.substring(command.indexOf(';')+1);
			}
			else
				return "missing";
		}
		else
			return null;
	}
	
}
