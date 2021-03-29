import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class VMdriver {

	//Variables that keep track of the input path, input file and output path
	static String filepath="";
	static String path="";
	static File inputFile;

	//Function that returns the filename of the .vm file currently being parsed
	public static String filename() {
		String filename= new File(path).getName();

		//System.out.println(filename.substring(0, filename.length()-3));
		return filename.substring(0, filename.length()-3);
	}


	//Main driver function
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//Initialising the comment remover.
		VMnoWhiteSpace spaceRemover= new VMnoWhiteSpace();

		//Taking the path of the directory/file as an input
		filepath= args[0];
		inputFile= new File(filepath);	
		File[] listOfFiles;
		File outputFile;

		//If the input was a .vm file, then the array of files contains just the one .vm file
		if (filepath.substring(filepath.length()-3).equals(".vm")) {
			listOfFiles= new File[] {inputFile};
			outputFile= new File(filepath.substring(0, filepath.length()-2)+""+"asm");
		}

		//If the input was a folder, the array of files contains all the files inside the folder
		//Output path is updated accordingly
		else {

			//Listing all the files inside the directory
			listOfFiles= inputFile.listFiles();

			String folderpath= inputFile.getAbsolutePath();
			String folderName= inputFile.getName();
			outputFile= new File(folderpath+"\\"+folderName+".asm");
			//System.out.println(outputFile.getAbsolutePath());

		}



		//Creating output file from the path calculated
		FileWriter writer= new FileWriter(outputFile);


		//Initialising the code writer classes
		VMarithmeticCode acode= new VMarithmeticCode();				//Module that returns the asm code for arithmetic ops
		VMmemoryAccessCode mcode= new VMmemoryAccessCode();         //Module returns asm code for push and pop
		VMprogramFlow pcode= new VMprogramFlow();
		VMfunctionFlow fcode= new VMfunctionFlow();

		/*-------------Bootstrapping section--------------------------------------------------*/

		String bootstrap="";
		bootstrap="@256\n"
				+ "D=A\n"
				+ "@SP\n"
				+ "M=D\n";
		writer.write(bootstrap);
		bootstrap= fcode.callcode("Sys.init", 0);
		writer.write(bootstrap);

		/*--------------------------------------------------------------------------------------*/

		//Iterate through all the files in the list of files and convert to assembly code

		for (File file : listOfFiles) {
			path= file.getAbsolutePath();
			if (path.substring(path.length()-2).equals("vm")){
				String filesansspaces= spaceRemover.whitespaceremove(path);	

				//Removing comments from the .vm program
				VMparser parse= new VMparser(filesansspaces);				//Sends the file as a String to be parsed.

				//Loop that runs for the length of the vm file and appends each lines
				//corresponding asm code to the file.


				while (parse.hasMoreCommands()) {
					parse.advance();
					String[] line= parse.command;
					String commandtype= parse.commandType();
					
					//Would have looked better with a switch case statement, but building up from previous projects made me go with this.
					if (commandtype.equals("C_ARITHMETIC")) {
						String asm= acode.aCode(line[0]);
						writer.write(asm);
					}
					else if (commandtype.equals("C_PUSH") || commandtype.equals("C_POP")) {
						String asm= mcode.pushPop(line[0], line[1], Integer.parseInt(line[2]));
						writer.write(asm);
					}
					else if (commandtype.equals("C_LABEL")){
						String asm= pcode.labelcode(line[1]);
						writer.write(asm);
					}
					else if (commandtype.equals("C_GOTO")){
						String asm= pcode.gotocode(line[1]);
						writer.write(asm);
					}
					else if (commandtype.equals("C_IF")) {
						String asm= pcode.ifcode(line[1]);
						writer.write(asm);
					}
					else if (commandtype.equals("C_FUNCTION")) {
						String asm= fcode.functioncode(line[1], Integer.parseInt(line[2]));
						writer.write(asm);
					}
					else if (commandtype.equals("C_RETURN")) {
						String asm= fcode.returncode(line[0]);
						writer.write(asm);
					}
					else if (commandtype.equals("C_CALL")) {
						String asm= fcode.callcode(line[1], Integer.parseInt(line[2]));
						writer.write(asm);
					}
				}

			}

		}
		writer.close();
	}

}
