import java.io.BufferedReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class jackTokenizer implements Cloneable{
	//Variable Declaration
	public int lineCount, indexPointer, linePointer, lineLength;
	public String[] lineIntoTokens;
	
	//Using a special character to represent spaces inside string constants
	public static String spaceInQ= String.valueOf((char)2);
	
	public Map<Integer, String> processedLines= new HashMap<Integer, String>();
	
	//Set of all the keywords
	public static Set<String> Keywords= new HashSet<>(Arrays.asList("class", "constructor",
			"function", "method", "field", "static", "var", "int", "char",
			"boolean", "void", "true", "false", "null", "this", "let", "do",
			"if", "else", "while", "return"));
	
	//Set of all the Symbols
	public static Set<String> Symbols= new HashSet<>(Arrays.asList("{", "}", "(", ")", "[", "]", ".",
			",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=", "~"));
	
	
	public String file;
	public String presentToken;
	
	
	//Initial attempt at clone function, but only creates a reference clone and not a true clone
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	//Custom clone funtion that initialises the new objects with the same values as another
	//jackTokenizer object at that instance
	public jackTokenizer(int line, int ind, String tok, String[] linetoks, int mx, Map<Integer, String>mapp, int linec) {
		this.linePointer= line;
		this.indexPointer= ind;
		this.presentToken= tok;
		this.lineIntoTokens= linetoks;
		this.lineLength= mx;
		this.processedLines= mapp;
		this.lineCount= linec;
	}
	
	public jackTokenizer(jackTokenizer t) {
		this(t.linePointer, t.indexPointer, t.presentToken, t.lineIntoTokens, t.lineLength, t.processedLines, t.lineCount);
	}
	
	//Main constructor that parses through the file
	public jackTokenizer(String filens) {
		file=filens;
		tokenizerInit(file);
	}
	
	
	//Each line of the file is parsed and pushed into a hashmap
	//indexed by line number
	public void tokenizerInit(String file) {
		Scanner br= new Scanner(file);
		lineCount=1;
		int quoteCount=0;
		String curString;
		
		while(br.hasNextLine()) {
			curString= br.nextLine();
			curString= curString.trim();
			
			String separated= "";
			String temp= "";
			String processedLine="";
			for (char element: curString.toCharArray()) {
				temp= String.valueOf(element);
				if (temp.equals("\"")) {
					quoteCount++;
				}
				
				//Not in a string constant
				if (quoteCount%2==0) {
					if (Symbols.contains(temp)) {
						temp=" "+ temp+ " ";
					}
				}
				else{
					if (temp.equals(" ")) {
						temp= spaceInQ;
					}
					
				}
				processedLine+= temp;

			}
			curString= processedLine.replaceAll("\\s+",  " ").trim();
			processedLines.put(lineCount++, curString);
		}
		//Number of lines
		lineCount-=1; 
		linePointer= 1;
		lineIntoTokens= processedLines.get(1).split(" ");
		indexPointer= 0;
		lineLength= lineIntoTokens.length-1;
		br.close();
	}
	
	//If the current file has more tokens, return true
	public boolean hasNext() {
		if (linePointer < lineCount || (linePointer == lineCount &&(indexPointer <= lineLength))) {
			return true;
		}
		else
			return false;
	}
	
	//Goes to the next token
	public void advance() {
		if (hasNext()) {
			if (indexPointer<=lineLength) {
				presentToken= lineIntoTokens[indexPointer++];
				//tokentype;
			}
			else if (linePointer<lineCount) {
				lineIntoTokens= processedLines.get(++linePointer).split(" ");
				indexPointer=0;
				lineLength= lineIntoTokens.length-1;
				presentToken= lineIntoTokens[indexPointer++];
				
			}
		}
		
	}
	
	//Creating an overloaded method
	//Abstracting the need for token type
	public String returnTokenVal() {
		String tokenType= tokenType();
		return returnTokenVal(tokenType);
	}
	
	//Function that returns the string value of the token
	public String returnTokenVal(String tokenType) {
		if (tokenType.equals("keyword")){
			return presentToken;
		}
		//Redundant code: javax.xml automatically converts these to special characters
		else if (tokenType.equals("symbol")) {
			String tok;
			if (presentToken.equals("<")) {
				return "<";
			}
			else if (presentToken.equals(">")) {
				return ">";
			}
			else if (presentToken.equals("&")) {
				return "&";
			}
			else if (presentToken.equals("\"")) {
				return "\"";
			}
			else
				return presentToken;
			
		}
		else if (tokenType.equals("integerConstant")) {
			return presentToken;
		}
		else if (tokenType.equals("stringConstant")) {
			String tok= presentToken.replaceAll(spaceInQ, " ");
			tok= tok.substring(1, tok.length()-1);
			return tok;
		}
		else
			return presentToken;
	}
	
	
	//Returns the type of the current token
	public String tokenType() {
		if (Keywords.contains(presentToken)) {
			return "keyword";
		}
		else if(Symbols.contains(presentToken)) {
			return "symbol";
		}
		else if(Character.isDigit(presentToken.charAt(0))) {
			return "integerConstant";
		}
		else if (presentToken.charAt(0)=='"') {
			return "stringConstant";
		}
		else {
			return "identifier";
		}
	}
}
