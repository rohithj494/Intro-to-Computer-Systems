
public class VMfunctionFlow {
	public static int functioncounter=1;

	//Function that returns asm code for a return statement
	public static String returncode(String functionName) {
		String code="";
		//Setting FRAME to current LCL
		code="@LCL\n"
				+ "D=M\n"
				+ "@FRAME\n"
				+ "M=D\n"
				//Setting RET to *(FRAME-5)
				+ "@FRAME\n"
				+ "D=M\n"
				+ "@5\n"
				+ "A=D-A\n"
				+ "D=M\n"
				+ "@RET\n"
				+ "M=D\n"
				//Setting *ARG to pop()
				+ "@SP\n"
				+ "AM=M-1\n"
				+ "D=M\n"
				+ "@ARG\n"
				+ "A=M\n"
				+ "M=D\n"
				//Setting SP to ARG+1
				+ "@ARG\n"
				+ "D=M+1\n"
				+ "@SP\n"
				+ "M=D\n"
				//Setting THAT, THIS, ARG and LCL to their values before the function call
				//That
				+ "@FRAME\n"
				+ "A=M-1\n"
				+ "D=M\n"
				+ "@THAT\n"
				+ "M=D\n"
				//This
				+ "@FRAME\n"
				+ "D=M\n"
				+ "@2\n"
				+ "A=D-A\n"
				+ "D=M\n"
				+ "@THIS\n"
				+ "M=D\n"
				//Arg
				+ "@FRAME\n"
				+ "D=M\n"
				+ "@3\n"
				+ "A=D-A\n"
				+ "D=M\n"
				+ "@ARG\n"
				+ "M=D\n"
				//Lcl
				+ "@FRAME\n"
				+ "D=M\n"
				+ "@4\n"
				+ "A=D-A\n"
				+ "D=M\n"
				+ "@LCL\n"
				+ "M=D\n"
				//Unconditional jump to return address
				+ "@RET\n"
				+ "A=M\n"
				+ "0;JMP\n";
		return code;

	}

	//Function that returns asm code for a function call statement
	public static String callcode(String functionName, int numArgs) {
		String code="";
		//pushing the return address on to the stack
		code="@"+functionName+""+functioncounter+"\n"
				+ "D=A\n"
				+ "@SP\n"
				+ "A=M\n"
				+ "M=D\n"
				+ "@SP\n"
				+ "M=M+1\n";

		//pushing lcl, arg, this and that onto the stack
		String commonCode= "D=M\n"
				+ "@SP\n"
				+ "A=M\n"
				+ "M=D\n"
				+ "@SP\n"
				+ "M=M+1\n";
		code= code+ "@LCL\n" + commonCode;
		code= code+ "@ARG\n" + commonCode;
		code= code+ "@THIS\n" + commonCode;
		code= code+ "@THAT\n" + commonCode;

		//setting ARG to new value
		code= code+ "@SP\n"
				+ "D=M\n"
				+ "@"+numArgs+"\n"
				+ "D=D-A\n"
				+ "@5\n"
				+ "D=D-A\n"
				+ "@ARG\n"
				+ "M=D\n";

		//Setting LCL to new value
		code=code+"@SP\n"
				+ "D=M\n"
				+ "@LCL\n"
				+ "M=D\n";

		//Jumping to the function
		code=code+"@"+functionName+"\n"
				+ "0;JMP\n";

		//Label for return
		code=code+"("+functionName+""+functioncounter+")\n";
		functioncounter++;
		return code;
	}

	//Function that returns asm code for a function definition statement
	public static String functioncode(String functionName, int numVars) {
		String code= "";
		code= "("+functionName+")\n";
		int k=0;
		//Allocating space needed for the local variables
		while (k<numVars) {
			code= code+"@SP\n"
					+ "A=M\n"
					+ "M=0\n"
					+ "@SP\n"
					+ "M=M+1\n";
			k++;
		}
		return code;
	}
}
