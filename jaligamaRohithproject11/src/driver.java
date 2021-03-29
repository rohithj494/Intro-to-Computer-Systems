import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

public class driver {

	public static void main(String[] args) throws TransformerException, ParserConfigurationException, IOException, DOMException, CloneNotSupportedException, XMLStreamException, FactoryConfigurationError {
		// TODO Auto-generated method stub
		
		//Variables that keep track of the input path, input file and output path
		String filepath="";
		String path="";
		File inputFile;
		
		//Initialising the comment remover.
		commentRemover cleanFile= new commentRemover();
		
		//Taking the path of the directory/file as an input
		filepath= args[0];
		inputFile= new File(filepath);	
		File[] listOfFiles;
		File outputFile;
		
		//If the input was a .jack file, then the array of files contains just the one .jack file
		if (filepath.substring(filepath.length()-5).equals(".jack")) {
			listOfFiles= new File[] {inputFile};
			outputFile= new File(filepath.substring(0, filepath.length()-5)+""+"XML.xml");
		}
		
		//If the input was a folder, the array of files contains all the files inside the folder
		//Output path is updated accordingly
		else {

			//Listing all the files inside the directory
			listOfFiles= inputFile.listFiles();

			String folderpath= inputFile.getAbsolutePath();
			String folderName= inputFile.getName();
			//System.out.println(outputFile.getAbsolutePath());

		}
		//Creeating output folder
//		File xmlFolder= new File(filepath+"//xmlFiles");
//		boolean make= xmlFolder.mkdir();
		
		//Iterate through all the files in the list of files and create XML files
		for (File file : listOfFiles) {
			path= file.getAbsolutePath();
			if (path.substring(path.length()-4).equals("jack")){
				
				//Creating the output file
				String filename= file.getName();
//				String pathtoXML= xmlFolder.getAbsolutePath();
				//outputFile= new File(filepath+"//"+filename.substring(0, filename.length()-5)+".xml");
				outputFile= new File(filepath+"//"+filename.substring(0, filename.length()-5)+".vm");
				//Input file without comments and leading spaces
				String fileNC= cleanFile.whitespaceremove(path);
				
				//Initialising the tokenizer
				jackTokenizer tokenize= new jackTokenizer(fileNC);
				
				//Initialising the compilation engine
				compilationEngine engine= new compilationEngine(tokenize, outputFile);
				
				//Compiles the entire class and returns an XML document, but also writes out an
				//entire .vm file 
				Document doc= engine.compileClass();
				
				
				//For writing out to a file. Using XML libraries for help
//				TransformerFactory tf = TransformerFactory.newInstance();
//			    Transformer transformer= tf.newTransformer();
//			    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//			    transformer.setOutputProperty(OutputKeys.METHOD, "html");
//			    FileOutputStream fos = new FileOutputStream(outputFile);
//				transformer.transform(new DOMSource(doc), new StreamResult(fos));
				
				//Closing file stream
//				fos.close();
			}
		}
		
		
//		
//		String filepath= "C:\\Users\\User\\Desktop\\nand2tetris\\nand2tetris\\projects\\10\\Square\\SquareGame.jack";
//		commentRemover commentRemove= new commentRemover();
//		String fileNC= commentRemove.whitespaceremove(filepath);
//		jackTokenizer tokenize= new jackTokenizer(fileNC);
//		System.out.println(fileNC);
//		compilationEngine engine= new compilationEngine(tokenize);
//		Document doc= engine.compileClass();
//		
//		
//	    File outputFile= new File("C:\\Users\\User\\Desktop\\nand2tetris\\nand2tetris\\projects\\10\\Square\\SquareGameXML.xml");
//	    TransformerFactory tf = TransformerFactory.newInstance();
//	    Transformer transformer= tf.newTransformer();
//	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//	    transformer.setOutputProperty(OutputKeys.METHOD, "html");
//	    FileOutputStream fos = new FileOutputStream(outputFile);
//		transformer.transform(new DOMSource(doc), new StreamResult(fos));
//		fos.close();
		
//		File outputFile= new File("C:\\Users\\User\\Desktop\\nand2tetris\\nand2tetris\\projects\\10\\ExpressionLessSquare\\maintest.xml");
//		FileOutputStream fos = new FileOutputStream(outputFile);
//		XMLStreamWriter writer = XMLOutputFactory.newFactory()
//		        .createXMLStreamWriter(fos);
//		Transformer transformer = TransformerFactory.newInstance().newTransformer();
//		
//	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//	    
//	    transformer.transform(new DOMSource(doc), new StAXResult(writer));
//	    fos.close();
	}

}
