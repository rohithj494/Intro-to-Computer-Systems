
import java.util.*;

public class code {
	
	//Hashmap that is used to return binary code for a jmp statement
	static HashMap<String, String> jmpmap= new HashMap<String, String>(){
		{
			put("missing", "000");
			put("JGT", "001");
			put("JEQ", "010");
			put("JGE", "011");
			put("JLT", "100");
			put("JNE", "101");
			put("JLE", "110");
			put("JMP", "111");
		}
	};
	
	//Returns a 15 bit binary number for a given integer.
	public static String symbol(String mnemonic) {
			return String.format("%015d", Long.parseLong(Integer.toBinaryString(Integer.parseInt(mnemonic))));
	}
	
	//Returns binary code for dest
	public static String destcode(String mnemonic) {
		String[] code= {"0", "0", "0"};
		if (mnemonic.contains("A")) {
			code[0]="1";
		}
		else
			code[0]= "0";
		if (mnemonic.contains("D")) {
			code[1]="1";
		}
		else
			code[1]= "0";
		if (mnemonic.contains("M")) {
			code[2]="1";
		}
		else
			code[2]= "0";
		
		return code[0]+code[1]+code[2];
	}
	
	//Returns the binary code for the jmp statement by looking up in the hashmap
	public static String jmpcode(String mnemonic) {
		String code= jmpmap.get(mnemonic);
		return code;
	}
	
	//Returns the binary code for comp
	public static String compcode(String mnemonic) {
		String a="0";
		
		//the a bit is 0 if A is used in the comp statement
		if (mnemonic.contains("M"))
			a="1";
		//Replacing M with A because they represent the same computation
		mnemonic= mnemonic.replace("M", "A");
		
		String code="";
		switch (mnemonic) {
		case "0":
			code= "101010";
			break;
		case "1":
			code= "111111";
			break;
		case "-1":
			code= "111010";
			break;
		case "D":
			code= "001100";
			break;
		case "A":
			code= "110000";
			break;
		case "-D":
			code= "001111";
			break;
		case "-A":
			code= "110011";
			break;
		case "!D":
			code= "001101";
			break;
		case "!A":
			code= "110001";
			break;
		//Addition is symmetric, therefore both cases are equivalent
		case "D+1":
		case "1+D":
			code= "011111";
			break;
		case "A+1":
		case "1+A":
			code= "110111";
			break;
		case "D-1":
			code= "001110";
			break;
		case "A-1":
			code= "110010";
			break;
		case "D+A":
		case "A+D":
			code= "000010";
			break;
		case "D-A":
			code= "010011";
			break;
		case "A-D":
			code= "000111";
			break;
		case "D&A":
		case "A&D":
			code= "000000";
			break;
		case "D|A":
		case "A|D":
			code= "010101";
			break;
		}
		return (a+""+code);
	}
	

}
