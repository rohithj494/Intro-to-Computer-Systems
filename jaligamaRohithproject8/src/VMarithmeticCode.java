
public class VMarithmeticCode {
	//variables to use for gt, gt, eq statements so that there are no clashes.
	static int trueCount=1, falseCount=1, continueCount=1;

	//Returns the appropriate asm code for the arithmetic statement
	public static String aCode (String command) {
		String code="";
		switch (command) {
		case ("add"):
			code= "@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "A=A-1\n"
					+ "M=D+M\n";
		break;
		case ("sub"):
			code= "@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "A=A-1\n"
					+ "M=M-D\n";
		break;
		case ("neg"):
			code= "@SP\n"
					+ "A=M-1\n"
					+ "M=-M\n";
		break;
		case ("eq"):
			code= "@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "A=A-1\n"
					+ "D=M-D\n"
					+ "@TRUE"+trueCount+"\n"
					+ "D;JEQ\n"
					+ "@SP\n"
					+ "A=M-1\n"
					+ "M=0\n"
					+ "@CONTINUE"+continueCount+"\n"
					+"0;JMP\n"
					+"(TRUE"+trueCount+")\n"
					+"@SP\n"
					+"A=M-1\n"
					+ "M=-1\n"
					+ "(CONTINUE"+continueCount+")\n";
		trueCount++;
		continueCount++;
		break;
		case ("gt"):
			code= "@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "A=A-1\n"
					+ "D=M-D\n"
					+ "@FALSE"+falseCount+"\n"
					+ "D;JLE\n"
					+ "@SP\n"
					+ "A=M-1\n"
					+ "M=-1\n"
					+ "@CONTINUE"+continueCount+"\n"
					+"0;JMP\n"
					+"(FALSE"+falseCount+")\n"
					+"@SP\n"
					+"A=M-1\n"
					+ "M=0\n"
					+ "(CONTINUE"+continueCount+")\n";
		falseCount++;
		continueCount++;
		break;
		case ("lt"):
			code= "@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "A=A-1\n"
					+ "D=M-D\n"
					+ "@FALSE"+falseCount+"\n"
					+ "D;JGE\n"
					+ "@SP\n"
					+ "A=M-1\n"
					+ "M=-1\n"
					+ "@CONTINUE"+continueCount+"\n"
					+ "0;JMP\n"
					+ "(FALSE"+falseCount+")\n"
					+ "@SP\n"
					+ "A=M-1\n"
					+ "M=0\n"
					+ "(CONTINUE"+continueCount+")\n";
		falseCount++;
		continueCount++;
		break;
		case ("and"):
			code= "@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "A=A-1\n"
					+ "D=D&M\n"
					+ "@SP\n"
					+ "A=M-1\n"
					+ "M=D\n";
		break;
		case ("or"):
			code= "@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "A=A-1\n"
					+ "D=D|M\n"
					+ "@SP\n"
					+ "A=M-1\n"
					+ "M=D\n";
		break;
		case ("not"):
			code= "@SP\n"
					+ "A=M-1\n"
					+ "M=!M\n";
		break;
		}
		return code;
	}
}
