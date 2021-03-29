
public class VMmemoryAccessCode {

	//Function that calls either push or pop
	public static String pushPop(String command, String segment, int number) {
		if (command.equals("push"))
			return pushCode(segment, number);
		else
			return popCode(segment,  number);
	}

	//Function that returns the assembly code for the push statement
	public static String pushCode(String segment, int number) {
		String code= "";
		switch (segment) {
		case ("constant"):
			code= "@"+number+"\n"
					+ "D=A\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=D\n"
					+ "@SP\n"
					+ "M=M+1\n";
		break;
		case ("local"):
			code="@LCL\n"
					+ "D=M\n"
					+ "@"+number+"\n"
					+ "A=A+D\n"
					+ "D=M\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=D\n"
					+ "@SP\n"
					+ "M=M+1\n";
		break;
		case ("argument"):
			code="@ARG\n"
					+ "D=M\n"
					+ "@"+number+"\n"
					+ "A=A+D\n"
					+ "D=M\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=D\n"
					+ "@SP\n"
					+ "M=M+1\n";
		break;
		case ("this"):
			code="@THIS\n"
					+ "D=M\n"
					+ "@"+number+"\n"
					+ "A=A+D\n"
					+ "D=M\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=D\n"
					+ "@SP\n"
					+ "M=M+1\n";
		break;
		case ("that"):
			code="@THAT\n"
					+ "D=M\n"
					+ "@"+number+"\n"
					+ "A=A+D\n"
					+ "D=M\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=D\n"
					+ "@SP\n"
					+ "M=M+1\n";
		break;
		case ("static"):
			String filename= VMdriver.filename();
		code="@"+filename+""+number+"\n"
				+ "D=M\n"
				+ "@SP\n"
				+ "A=M\n"
				+ "M=D\n"
				+ "@SP\n"
				+ "M=M+1\n";
		break;
		case ("temp"):
			code="@R"+""+(number+5)+"\n"
					+ "D=M\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=D\n"
					+ "@SP\n"
					+ "M=M+1\n";
		break;
		case ("pointer"):
			if (number==0) {
				code="@THIS\n"
						+ "D=M\n"
						+ "@SP\n"
						+ "A=M\n"
						+ "M=D\n"
						+ "@SP\n"
						+ "M=M+1\n";
			}
			else {
				code="@THAT\n"
						+ "D=M\n"
						+ "@SP\n"
						+ "A=M\n"
						+ "M=D\n"
						+ "@SP\n"
						+ "M=M+1\n";
			}

		break;
		}	
		return code;
	}

	//Function that returns the assembly code for the pop instruction.
	public static String popCode(String segment, int number) {
		String code= "";
		switch (segment) {
		case ("local"):
			code="@LCL\n"
					+ "D=M\n"
					+ "@"+number+"\n"
					+ "D=A+D\n"
					+ "@R13\n"
					+ "M=D\n"
					+ "@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "@R13\n"
					+ "A=M\n"
					+ "M=D\n";
		break;
		case ("argument"):
			code="@ARG\n"
					+ "D=M\n"
					+ "@"+number+"\n"
					+ "D=A+D\n"
					+ "@R13\n"
					+ "M=D\n"
					+ "@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "@R13\n"
					+ "A=M\n"
					+ "M=D\n";
		break;
		case ("this"):
			code="@THIS\n"
					+ "D=M\n"
					+ "@"+number+"\n"
					+ "D=A+D\n"
					+ "@R13\n"
					+ "M=D\n"
					+ "@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "@R13\n"
					+ "A=M\n"
					+ "M=D\n";
		break;
		case ("that"):
			code="@THAT\n"
					+ "D=M\n"
					+ "@"+number+"\n"
					+ "D=A+D\n"
					+ "@R13\n"
					+ "M=D\n"
					+ "@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "@R13\n"
					+ "A=M\n"
					+ "M=D\n";
		break;
		case ("static"):
			String filename= VMdriver.filename();
		code="@SP\n"
				+ "AM=M-1\n"
				+ "D=M\n"
				+ "@"+filename+""+number+"\n"
				+ "M=D\n";
		break;
		case ("temp"):
			code="@SP\n"
					+ "AM=M-1\n"
					+ "D=M\n"
					+ "@R"+""+(number+5)+"\n"
					+ "M=D\n";
		break;
		case ("pointer"):
			if (number==0) {
				code="@SP\n"
						+ "AM=M-1\n"
						+ "D=M\n"
						+ "@THIS\n"
						+ "M=D\n";
			}
			else {
				code="@SP\n"
						+ "AM=M-1\n"
						+ "D=M\n"
						+ "@THAT\n"
						+ "M=D\n";
			}

		break;
		}	
		return code;
	}
}
