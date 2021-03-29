import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class VMdriver {
	static String filepath="";
	static File inputFile;
	public static String filename() {
		String tempname= inputFile.getName();
		String filename= tempname.substring(0, tempname.length()-2);
		return filename;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//Initialising the comment remover and calling the function.
		VMnoWhiteSpace spaceRemover= new VMnoWhiteSpace();
		filepath= args[0];
		String filesansspaces= spaceRemover.whitespaceremove(filepath);
		
		//Creating output file from input file path
		inputFile= new File(filepath);	
		String path= inputFile.getAbsolutePath();
		File outputFile= new File(path.substring(0, path.length()-2)+"asm");
		FileWriter writer= new FileWriter(outputFile);
		
		//Initialising the other submodules of the program
		VMparser parse= new VMparser(filesansspaces);				//Sends the file as a String to be parsed.
		VMarithmeticCode acode= new VMarithmeticCode();				//Module that returns the asm code for arithmetic ops
		VMmemoryAccessCode mcode= new VMmemoryAccessCode();         //Module returns asm code for push and pop
		
		//Loop that runs for the length of the vm file and appends each lines
		//corresponding asm code to the file.
		while (parse.hasMoreCommands()) {
			parse.advance();
			String[] line= parse.command;
			String commandtype= parse.commandType();
			
			if (commandtype.equals("C_ARITHMETIC")) {
				String asm= acode.aCode(line[0]);
				writer.write(asm);
			}
			else {
				String asm= mcode.pushPop(line[0], line[1], Integer.parseInt(line[2]));
				writer.write(asm);
			}
		}
		writer.close();
	}

}
