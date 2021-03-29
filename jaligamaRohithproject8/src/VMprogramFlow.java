
public class VMprogramFlow {

	//Function that returns asm code for a label
	public static String labelcode(String label) {
		String code="";
		code = "("+label+")\n";
		return code;
	}


	//Function that returns asm code for a goto statement
	public static String gotocode(String label) {
		String code= "";
		code= "@"+label+"\n"+
				"0;JMP\n";
		return code;
	}

	//Function that returns asm code for an ifgoto statement
	public static String ifcode(String label) {
		String code="";
		code= "@SP\n"
				+ "AM=M-1\n"
				+ "D=M\n"
				+ "@"+label+"\n"
				+ "D;JNE\n";
		return code;
	}
}
