
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//Importing the program to remove white spaces.

public class driver {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//Initialising and calling the white space remover function that returns a String. 
		noWhiteSpace spaceRemover= new noWhiteSpace();
		String filename= args[0];
		String filesansspaces= spaceRemover.whitespaceremove(filename);
		
		//Creating output file from input file path
		File inputFile= new File(filename);	
		String path= inputFile.getAbsolutePath();
		File outputFile= new File(path.substring(0, path.length()-3)+"hack");
		FileWriter writer= new FileWriter(outputFile);
		
		//Initialising the other submodules of the program
		parser parse= new parser(filesansspaces);	//Sends the file as a String to be parsed.
		code bincode= new code();					//Module that returns the binary codes
		symtable symboltable= new symtable();		//Symbol table
		
		int linecount=-1;							//Since the first line is the 0th line
		
		/* First parse only looks at L_COMMANDS adds the label to the symbol tables along with
		 * the line numbers.
		 */
		while (parse.hasMoreCommands()) {
			parse.advance();
			String command= parse.command;				//The entire line of the program
			String commandtype= parse.commandType();	//Command type
			linecount++;
			if (commandtype.equals("L_COMMAND")) {
				String symbol= parse.symbol(commandtype, command);
				// If the symbol table doesn't already have the label, add it
				if (!symboltable.has(symbol)) {
					symboltable.addSym(symbol, linecount);
				}
				
				//Since an L_COMMAND does not count as a line, decrementing linecount
				linecount--;
			}
		}
		
		
		int variableMem= 16;				/*Since registers 1 to 15 are default, user 
										defined variables are added from location 16 onwards*/
		
		/* Second parse of the program converts each line to machine code using lookups from various tables*/
		parser secondparse= new parser(filesansspaces);
		while (secondparse.hasMoreCommands()){
			secondparse.advance();
			String command= secondparse.command;
			String commandtype= secondparse.commandType();
			//			System.out.println(parse.command+ " " + commandtype);
			//			System.out.print("Symbol= "+ parse.symbol(commandtype, parse.command));
			//			System.out.print(" Dest= "+ parse.dest(commandtype, parse.command));
			//			System.out.print(" Comp= "+ parse.comp(commandtype, parse.command));
			//			System.out.print(" Jump= "+ parse.jmp(commandtype, parse.command)+"\n");
			linecount++;
			
	//Case 1: A_COMMAND: Either the value is a number or a label. Have to lookup in table if it is label.
			if (commandtype.equals("A_COMMAND")) {
				String symbol= secondparse.symbol(commandtype, command);
				
				//Checking first character of the symbol to distinguish a number form a label
				if (Character.isDigit((symbol.charAt(0)))){
					String line= "0"+ code.symbol(symbol);
					writer.write(line+"\n");
					//System.out.println(line);
				}
				else {
					//If the symbol is already in the table, print the value.
					if (symboltable.has(symbol)) {
						int address= symboltable.getAdd(symbol);
						String code= "0" + String.format("%015d", Long.parseLong(Integer.toBinaryString(address)));
						writer.write(code+"\n");
						//System.out.println(code);
					}
					//if the symbol is not in the table, add it with the current variableMem count, print that value in binary.
					//Increment the counter for the next symbol.
					else {
						symboltable.addSym(symbol, variableMem);
						String code= "0" + String.format("%015d", Long.parseLong(Integer.toBinaryString(variableMem)));
						writer.write(code+"\n");
						//System.out.println(code);
						variableMem++;
					}
				}

			}
		//Case 2: C_COMMAND: Print the machine code after appending the lookups for each code segment
			else if (commandtype.equals("C_COMMAND")) {
				String dest= secondparse.dest(commandtype, command);
				String comp= secondparse.comp(commandtype, command);
				String jmp= secondparse.jmp(commandtype, command);
				String line= "111"+code.compcode(comp)+code.destcode(dest)+code.jmpcode(jmp);
				writer.write(line+"\n");
				//System.out.println(line);

			}
		//Case 3: L_COMMAND: Don't do anything, decrement line counter.
			else {
				
				linecount--;
				//System.out.println(linecount);
			}
		}
		writer.close();
	}
}
