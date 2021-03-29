import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class vmWriter {
	public PrintWriter printWriter;
	
	//Constructor method for the writer class
	public vmWriter(File out) throws FileNotFoundException {
		printWriter= new PrintWriter(out);
	}
	 
	public void close(){
	        printWriter.close();
	    }
	 
	//Each of these functions print out a line of vm code.
	public void writeFunction(String name, int localVars) {
		printWriter.print("function "+ name+" "+ localVars+"\n");
	}
	
	public void writePush(String segment, int index) {
		printWriter.print("push "+segment+" "+index+"\n");
	}
	
	public void writePop(String segment, int index) {
		printWriter.print("pop "+segment+" "+index+"\n");
	}
	
	public void writeCall(String name, int nArgs) {
		printWriter.print("call "+ name+" "+ nArgs+"\n");
	}
	
	public void writeArithmetic(String operation) {
		printWriter.print(operation+"\n");
	}
	
	public void writeLabel(String label) {
		printWriter.print("label "+ label+"\n");
	}
	
	public void writeIf(String label) {
		printWriter.print("if-goto "+label+"\n");
	}
	
	public void writeGoto(String label) {
		printWriter.print("goto "+label+"\n");
	}
	
	public void writeReturn() {
		printWriter.print("return\n");
	}
	
	public void writeCommand(String command) {
		printWriter.print(command+"\n");
	}
}
