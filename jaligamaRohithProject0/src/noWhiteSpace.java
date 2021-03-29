import java.io.*;
import java.util.*;

public class noWhiteSpace {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub	
		String filename= args[0];		//Path to the file read from user input
		try {
			File inputFile= new File(filename);				//Create file object if it exists
			Scanner scan= new Scanner(inputFile);			//Scanner to read from file
			String path= inputFile.getAbsolutePath();
			
			File outputFile= new File(path.substring(0, path.length()-2)+"out"); 	//Create output file with same path but .out suffix
			FileWriter writer= new FileWriter(outputFile);
			
			/* State check variable*/
			
			//Bool to check if control has come out of a comment
			boolean outOfComment= false;
			//Bool to check if a comment starts at index 0 of a line
			boolean indexZero= false;
			//Bool to check if the line contains characters other than comments and whitespaces.
			boolean lineContainsSig= false;
			
			
			
			while (scan.hasNextLine()) {
				//Reading the next line of the file
				String line= scan.nextLine();
				if (line.length()==0) {
					//Do nothing, skip this line
				}
				else {
					lineContainsSig= false;							//Start by assuming no code in a line
					for (int i=0; i<line.length(); i++) {
						
	//****************************Case 1: Multi line comment
						
						if (i<line.length()-1 && line.charAt(i)=='/' && line.charAt(i+1)=='*') {
							if (i==0) indexZero= true;				//Comment starts at the start of the line
							outOfComment= false;					//Control is inside the comment now
							while (!outOfComment) {					//Run this loop until control exits the multi line comment
								while (i<line.length()) {
									if (i<line.length()-1 && line.charAt(i)== '*' && line.charAt(i+1)=='/') {
										outOfComment=true;
										
										/* There only needs to be a newline if there is continuous code
										 * 	before and after the comment
										 */
										if (!indexZero  && i!=line.length()-2)
											writer.write("\n");
										i++;
										break;
									}
									i++;
								}
								if (i==line.length() && scan.hasNextLine()) {
									line= scan.nextLine();
									i=0;
								}
							}
						}
						
	//****************************Case 2: Single Line Comment
						else if (i<line.length()-1 && line.charAt(i)=='/' && line.charAt(i+1)=='/') {
							if (i==0) indexZero= true;
							i=line.length();		//Go to end of line because its a single line comment
						}
						
						
	//****************************Case 3: Tab/Space
						else if (line.charAt(i)==' ' || line.charAt(i)=='\t') {
							//Do nothing
						}
						
						
	//****************************Case 4: Normal Character
						else {
							lineContainsSig= true;   			//Line contains code
							writer.write(line.charAt(i));
						}
					}
					if (lineContainsSig) {
						writer.write("\n");
					}
					indexZero=false;
					
				}
					
				
			}
			
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		      e.printStackTrace();
		}
		
	}

}
