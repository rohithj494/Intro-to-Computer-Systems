import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class compilationEngine {
	public jackTokenizer jTokenizer;
	public symbolTable symTable;
	public vmWriter writer;

	public Document document;
	public Element root;

	public String className;
	public String subroutineName;
	public int labelCount;

	// Main constructor that uses the tokenizer object
	public compilationEngine(jackTokenizer tokenizer, File out) throws FileNotFoundException {
		jTokenizer = tokenizer;
		writer = new vmWriter(out);
		symTable = new symbolTable();
	}

	// Function used to create the XML node that is used to append to the tree
	public Element createXMLnode(String tokenType) {
		Element temp;
		temp = document.createElement(tokenType);
		temp.setTextContent(" " + jTokenizer.returnTokenVal(tokenType) + " ");
		return temp;
	}

	// "Main" function of the class. Returns an XML document
	public Document compileClass() throws ParserConfigurationException, DOMException, CloneNotSupportedException {
		Element ele = null;
		String token;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();
		document = builder.newDocument();

		// Root level XML
		root = document.createElement("class");
		document.appendChild(root);

		// First token is "class"
		jTokenizer.advance();
		ele = createXMLnode("keyword");
		root.appendChild(ele);

		// Second token is the class name
		jTokenizer.advance();
		className = jTokenizer.returnTokenVal();
		ele = createXMLnode("identifier");
		root.appendChild(ele);

		// Third token is '{'
		jTokenizer.advance();
		ele = createXMLnode("symbol");
		root.appendChild(ele);

		// Fourth token is the datatype of the class var declaration
		jTokenizer.advance();
		String tokenType;
		token = jTokenizer.returnTokenVal("keyword");

		do {
			// Declare the class variables
			if (token.matches("field|static")) {
				compileClassVarDec();

			}
			// Subroutine Declaration
			if (token.matches("constructor|function|method")) {
				compileSubroutine();
			}
			if (jTokenizer.hasNext()) {
				jTokenizer.advance();
				tokenType = jTokenizer.tokenType();
				token = jTokenizer.returnTokenVal(tokenType);
			}

		} while (!token.equals("}"));

		// Token "}"
		ele = createXMLnode("symbol");
		root.appendChild(ele);

		writer.close();
		return document;
	}

	// Appends the XML for class variable declarations
	// At time of exit, the tokenizer is at the last token of the declaration (;)
	public void compileClassVarDec() {
		Element ele = null;
		String token, tokenType;
		String type, kind, name;
		tokenType = jTokenizer.tokenType();
		token = jTokenizer.returnTokenVal(tokenType);

		Element parentXML = document.createElement("classVarDec");
		ele = createXMLnode("keyword");
		kind = jTokenizer.returnTokenVal();

		// Mapping a jack keyword to a vm keyword
		if (kind.equals("var")) {
			kind = "local";
		} else if (kind.equals("arg")) {
			kind = "argument";
		} else if (kind.equals("field")) {
			kind = "this";
		}
		parentXML.appendChild(ele);

		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		token = jTokenizer.returnTokenVal(tokenType);
		type = jTokenizer.returnTokenVal();
		ele = createXMLnode(tokenType);
		parentXML.appendChild(ele);

		jTokenizer.advance();
		while (!jTokenizer.returnTokenVal(tokenType).equals(";")) {
			tokenType = jTokenizer.tokenType();
			token = jTokenizer.returnTokenVal(tokenType);
			ele = createXMLnode(tokenType);
			parentXML.appendChild(ele);
			jTokenizer.advance();
			if (tokenType.equals("identifier")) {
				// Adding the variable to the symbol table
				symTable.define(token, type, kind);
			}
		}

		// The ending symbol of the line (;)
		ele = createXMLnode("symbol");
		parentXML.appendChild(ele);
		root.appendChild(parentXML);
	}

	// Appends the XML for a subroutine to the parent
	// The tokenizer points to the last token of the subroutine on exit (})
	public void compileSubroutine() throws DOMException, CloneNotSupportedException {
		Element ele = null;
		String token, tokenType;
		String keyword, returnType;
		tokenType = jTokenizer.tokenType();
		token = jTokenizer.returnTokenVal(tokenType);

		Element parentXML = document.createElement("subroutineDec");

		symTable.startSubroutine();

		// method/constructor/funtion
		ele = createXMLnode(tokenType);
		keyword = jTokenizer.returnTokenVal();
		parentXML.appendChild(ele);

		if (keyword.equals("method")) {

			// argument 0 for a method is always a "this" object of the present class
			symTable.define("this", className, "argument");
		}

		// Return type of the subroutine
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		token = jTokenizer.returnTokenVal(tokenType);
		returnType = token;
		ele = createXMLnode(tokenType);
		parentXML.appendChild(ele);

		// Name of the subroutine
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		token = jTokenizer.returnTokenVal(tokenType);
		subroutineName = token;
		ele = createXMLnode(tokenType);
		parentXML.appendChild(ele);

		// The character "("
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		token = jTokenizer.returnTokenVal(tokenType);
		ele = createXMLnode(tokenType);
		parentXML.appendChild(ele);

		// the list of parameters
		parentXML.appendChild(compileParameterList());

		// the symbol ")"
		token = jTokenizer.returnTokenVal();
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		parentXML.appendChild(ele);

		// Body of subroutine
		Element body = document.createElement("subroutineBody");
		parentXML.appendChild(body);

		// Character "{"
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		body.appendChild(ele);

		// The body of the subroutine that contains declarations and statements
		jTokenizer.advance();
		token = jTokenizer.returnTokenVal();
		boolean declared = false;
		while (!token.equals("}")) {
			tokenType = jTokenizer.tokenType();
			token = jTokenizer.returnTokenVal(tokenType);

			// XML for variable declarations inside the funtion
			if (token.equals("var")) {
				body.appendChild(compileVarDec());
				jTokenizer.advance();
			}

			// XML for statements inside the function, and the declaration
			else if (token.matches("let|if|while|do|return")) {

				// If the first statement has been encountered,
				// Variable declaration has ended. Function Declaration
				// can be written now
				if (!declared) {

					// Writing the VM code for function declaration
					writer.writeFunction(className + "." + subroutineName, symTable.count.get("local"));

					if (keyword.equals("method")) {
						// If the subroutine is a method
						writer.writePush("argument", 0);
						writer.writePop("pointer", 0);
					} else if (keyword.equals("constructor")) {
						// If subroutine is a constructor
						writer.writePush("constant", symTable.count.get("this"));
						writer.writeCall("Memory.alloc", 1);
						writer.writePop("pointer", 0);
					}
					declared = true;
				}
				// Write the VM code for the statements after variable declaration
				body.appendChild(compileStatements());
			}

		}

		// Closing char "}"
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		body.appendChild(ele);

		root.appendChild(parentXML);
	}

	// Appends the XML for a parameter list that occurs inside a subroutine
	// declaration.
	public Element compileParameterList() {
		Element ele = null;
		String token, tokenType;
		String type, argument;
		Element parameterListParent = document.createElement("parameterList");

		// The first parameter type, or ")" if the parameter list is empty
		jackTokenizer clone = new jackTokenizer(jTokenizer);
		clone.advance();
		tokenType = clone.tokenType();
		token = clone.returnTokenVal(tokenType);
		type = token;
		if (!token.equals(")")) {
			ele = createXMLnode(tokenType);
			parameterListParent.appendChild(ele);
		} else {
			// -------------------------Adding a newline character, otherwise the
			// textcomparer
			// --------------------unfortunately fails a self closing tag
			// parameterListParent.setTextContent("\n");
			jTokenizer.advance();
			return parameterListParent;
		}

		// Parameters
		while (!token.equals(")")) {
			// Since the list is of the form '('(type identifier) , ?)
			// the first segment is type, second is the identifier, third is a comma or )

			jTokenizer.advance();
			type = jTokenizer.returnTokenVal();

			jTokenizer.advance();
			tokenType = jTokenizer.tokenType();
			argument = jTokenizer.returnTokenVal(tokenType);

			symTable.define(argument, type, "argument");

			jTokenizer.advance();
			token = jTokenizer.returnTokenVal();
			if (!token.equals(")")) {
				ele = createXMLnode(tokenType);
				parameterListParent.appendChild(ele);
			}
		}

		return parameterListParent;

	}

	// Appends the XML for a line of variable declarations inside a subroutine
	public Element compileVarDec() {
		Element ele = null;
		String type;
		String token, tokenType;

		Element varDecParent = document.createElement("varDec");

		// "var"
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		varDecParent.appendChild(ele);

		// The type of the var
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		token = jTokenizer.returnTokenVal(tokenType);
		type = token;
		ele = createXMLnode(tokenType);
		varDecParent.appendChild(ele);

		// The variable identifiers themselves until ;

		while (!token.equals(";")) {
			jTokenizer.advance();
			tokenType = jTokenizer.tokenType();
			token = jTokenizer.returnTokenVal(tokenType);
			ele = createXMLnode(tokenType);
			varDecParent.appendChild(ele);
			if (!token.equals(",") && !token.equals(";")) {
				// Adding the variables to the symboltable
				symTable.define(token, type, "local");
			}
		}

		return varDecParent;

	}

	// Appends XML for the respective statement and iteratively checks for more
	// statements in the same block
	public Element compileStatements() throws CloneNotSupportedException {
		Element ele = null;
		String token, tokenType;

		boolean end = false;
		tokenType = jTokenizer.tokenType();
		token = jTokenizer.returnTokenVal(tokenType);
		Element statementsParent = document.createElement("statements");

		do {
			if (token.equals("let")) {
				ele = compileLet();
			} else if (token.equals("do")) {
				ele = compileDo();
			} else if (token.equals("if")) {
				ele = compileIf();
			} else if (token.equals("while")) {
				ele = compileWhile();
			} else if (token.equals("return")) {
				ele = compileReturn();
			}
			if (ele != null)
				statementsParent.appendChild(ele);

			jTokenizer.advance();
			tokenType = jTokenizer.tokenType();
			token = jTokenizer.returnTokenVal(tokenType);
			if (!token.matches("let|if|while|do|return"))
				end = true;
		} while (!end);

		return statementsParent;
	}

	// Appends XML for a do statement.
	public Element compileDo() {
		Element ele = null;
		String token, tokenType;
		Element doParent = document.createElement("doStatement");
		String subName = "";
		int nArgs;
		boolean method = false;

		// keyword do
		tokenType = jTokenizer.tokenType();
		token = jTokenizer.returnTokenVal();
		ele = createXMLnode(tokenType);
		doParent.appendChild(ele);

		// Name of subroutine
		jTokenizer.advance();
		token = jTokenizer.returnTokenVal();
		subName += token;

		// Full name of the subroutine
		// Name can be multiple tokens long(class.method())
		do {
			ele = createXMLnode(jTokenizer.tokenType());
			doParent.appendChild(ele);

			jTokenizer.advance();
			token = jTokenizer.returnTokenVal();
			subName += token;
		} while (!token.equals("("));
		subName = subName.substring(0, subName.length() - 1);

		// If there is no ".", it is a method, push pointer to this
		// Update name with the full name of the method
		if (!subName.contains(".")) {
			subName = className + "." + subName;
			writer.writePush("pointer", 0);
			method = true;
		}
		
		//If there is a ".", if the first part is a variable, then it is a method
		else {
			String firstName = subName.substring(0, subName.indexOf('.'));
			if (symTable.lookup(firstName) != null) {
				method = true;
				writer.writePush(symTable.lookup(firstName).kind, symTable.lookup(firstName).index);
				subName = symTable.lookup(firstName).type + "." + subName.substring(subName.indexOf('.') + 1);
			}
		}
		// (
		ele = createXMLnode(jTokenizer.tokenType());
		doParent.appendChild(ele);

		// Expression List
		jTokenizer.advance();
		Element expList = compileExpressionList();
		doParent.appendChild(expList);
		nArgs = expList.getChildNodes().getLength();

		// )
		ele = createXMLnode(jTokenizer.tokenType());
		doParent.appendChild(ele);

		// ;
		jTokenizer.advance();
		ele = createXMLnode(jTokenizer.tokenType());
		doParent.appendChild(ele);

		// Writing the VM code for the function call
		//If it is a method, the number of args = numper of parameters+this
		if (method) {
			nArgs++;
		}
		//Since do statement, we can pop the result to a temp value
		writer.writeCall(subName, nArgs);
		writer.writePop("temp", 0);
		return doParent;
	}

	// Appends XML for a let statement
	public Element compileLet() {
		Element ele = null;
		String token, tokenType;
		String varName;
		boolean array = false;
		Element letParent = document.createElement("letStatement");

		// let
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		letParent.appendChild(ele);

		// identifier
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		varName = jTokenizer.returnTokenVal();
		ele = createXMLnode(tokenType);
		letParent.appendChild(ele);

		// Checks if the variable is an array element
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		token = jTokenizer.returnTokenVal();
		if (token.equals("[")) {
			array = true;
			//Pushing base address
			writer.writePush(symTable.lookup(varName).kind, symTable.lookup(varName).index);
			// [
			ele = createXMLnode(tokenType);
			letParent.appendChild(ele);

			// Pushing the offset, the expression that comes after [
			jTokenizer.advance();
			letParent.appendChild(compileExpression());
			
			// ]
			jTokenizer.advance();
			tokenType = jTokenizer.tokenType();
			ele = createXMLnode(tokenType);
			letParent.appendChild(ele);
			jTokenizer.advance();
			
			//Adding the two to find the address of the element
			writer.writeArithmetic("add");
		}

		// =
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		letParent.appendChild(ele);

		// compiling the expression on the right side of the equals
		jTokenizer.advance();
		letParent.appendChild(compileExpression());

		// ;
		jTokenizer.advance();
		ele = createXMLnode(jTokenizer.tokenType());
		letParent.appendChild(ele);
		
		//If it was an array, we have to push the result of the expression to the address
		//of the specific element
		if (array) {
			// *(base+offset)=expression
			writer.writePop("temp", 0);

			// base +index->that
			writer.writePop("pointer", 1);

			// Expression -> *(base+index)
			writer.writePush("temp", 0);
			writer.writePop("that", 0);
		} 
		//If not array, just pop it to the variable directly
		else {
			writer.writePop(symTable.lookup(varName).kind, symTable.lookup(varName).index);
		}

		return letParent;
	}

	// Appends XML for a while block. Everything including the ending "}" is in this
	// block
	public Element compileWhile() throws DOMException, CloneNotSupportedException {
		Element ele = null;
		String token, tokenType;
		
		//Unique labels created for jumping around
		String loopLabel = label();
		String exitLabel = label();

		Element whileParent = document.createElement("whileStatement");

		// while keyword
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		whileParent.appendChild(ele);

		// VML Top of the loop
		writer.writeLabel(loopLabel);

		// (
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		whileParent.appendChild(ele);

		// expression
		jTokenizer.advance();
		whileParent.appendChild(compileExpression());

		// )
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		whileParent.appendChild(ele);

		// if condition fails, go to exit
		writer.writeArithmetic("not");
		writer.writeIf(exitLabel);
		
		// {
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		whileParent.appendChild(ele);

		// Statements inside the while loop
		jTokenizer.advance();
		whileParent.appendChild(compileStatements());

		// }
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		whileParent.appendChild(ele);
		
		//Go back to the start of the loop for condition to be checked
		writer.writeGoto(loopLabel);
		writer.writeLabel(exitLabel);

		return whileParent;

	}

	// XML for a return statement
	public Element compileReturn() {
		Element ele = null;
		String token, tokenType;

		Element returnParent = document.createElement("returnStatement");

		// return keyword
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		returnParent.appendChild(ele);

		jTokenizer.advance();
		token = jTokenizer.returnTokenVal();

		// Return can either just be nothing or a variable/expression etc.
		if (token.equals(";")) {
			// Empty return, pushing 0 on stack
			writer.writePush("constant", 0);

			tokenType = jTokenizer.tokenType();
			ele = createXMLnode(tokenType);
			returnParent.appendChild(ele);

			writer.writeReturn();
			return returnParent;
		} 
		
		//Push the expression onto the stack
		else {
			returnParent.appendChild(compileExpression());
		}

		// ;
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		returnParent.appendChild(ele);
		
		//Write return statement
		writer.writeReturn();

		return returnParent;
	}

	// XML for an if block. Includes an else block if exists
	public Element compileIf() throws CloneNotSupportedException {
		Element ele = null;
		String token, tokenType;
		
		//Unique labels for jumping around
		String elseLabel = label();
		String endLabel = label();

		Element ifParent = document.createElement("ifStatement");

		// if
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		ifParent.appendChild(ele);

		// (
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		ifParent.appendChild(ele);

		// expression
		jTokenizer.advance();
		ifParent.appendChild(compileExpression());

		// )
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		ifParent.appendChild(ele);
		
		//If condition fails, go to else part of the block
		writer.writeArithmetic("not");
		writer.writeIf(elseLabel);

		// {
		jTokenizer.advance();
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		ifParent.appendChild(ele);

		// statement inside the if block
		jTokenizer.advance();
		ifParent.appendChild(compileStatements());

		// }
		tokenType = jTokenizer.tokenType();
		ele = createXMLnode(tokenType);
		ifParent.appendChild(ele);

		// if done, go to end
		writer.writeGoto(endLabel);
		
		// Else statements start here
		writer.writeLabel(elseLabel);

		// else

		// Interesting coding challenge. I had to look ahead by one token to
		// check if there was an else block, but if it didn't exists, I would be
		// one token ahead of the XML.
		// Built a clone of the present tokenizer and used it to look ahead,
		// if an else exists, advance the main tokenizer as well
		jackTokenizer clone = new jackTokenizer(jTokenizer);
		clone.advance();

		token = clone.returnTokenVal();
		if (token.equals("else")) {

			// else
			jTokenizer.advance();
			tokenType = jTokenizer.tokenType();
			ele = createXMLnode(tokenType);
			ifParent.appendChild(ele);

			// {
			jTokenizer.advance();
			tokenType = jTokenizer.tokenType();
			ele = createXMLnode(tokenType);
			ifParent.appendChild(ele);

			// statements inside the else block
			jTokenizer.advance();
			ifParent.appendChild(compileStatements());

			// }
			tokenType = jTokenizer.tokenType();
			ele = createXMLnode(tokenType);
			ifParent.appendChild(ele);

			// End of if-else block
			writer.writeLabel(endLabel);

			return ifParent;
		} 
		else {

			// End of if block

			writer.writeLabel(endLabel);
			return ifParent;
		}
	}

	// XML code for an expression term (op term )*
	public Element compileExpression() {
		Element ele = null;
		String token, tokenType;
		boolean op;
		boolean oper = false;
		String command = "";
		Element expressionParent = document.createElement("expression");

		// if the term-op-term-op cycle breaks, that means its the end of the expression
		do {

			// At least one term
			expressionParent.appendChild(compileTerm());
			
			//If an operand has been encountered, then can write the command as it is postfix
			if (oper) {
				writer.writeCommand(command);
			}
			jackTokenizer clone = new jackTokenizer(jTokenizer);
			clone.advance();
			token = clone.presentToken;

			// zero or more ops
			if (token.matches("\\+|-|\\*|/|\\&|\\||<|=|>|~")) {
				oper = true;

				switch (token) {
				case "+":
					command = "add";
					break;
				case "-":
					command = "sub";
					break;
				case "*":
					command = "call Math.multiply 2";
					break;
				case "/":
					command = "call Math.divide 2";
					break;
				case "<":
					command = "lt";
					break;
				case ">":
					command = "gt";
					break;
				case "=":
					command = "eq";
					break;
				case "&":
					command = "and";
					break;
				case "|":
					command = "or";
					break;
				}

				jTokenizer.advance();
				tokenType = jTokenizer.tokenType();
				ele = createXMLnode(tokenType);
				expressionParent.appendChild(ele);
				jTokenizer.advance();
				op = true;
			} else {
				op = false;
			}
		} while (op);

		return expressionParent;
	}

	// XML code for a term. Recursively calls the constituent functions as described
	// in the text
	public Element compileTerm() {
		Element ele = null;
		String token, tokenType;
		String varName;

		Element termParent = document.createElement("term");

		token = jTokenizer.returnTokenVal();
		tokenType = jTokenizer.tokenType();

		// Case 1: ( expression )

		if (token.equals("(")) {
			// (
			ele = createXMLnode(tokenType);
			termParent.appendChild(ele);

			// exp
			jTokenizer.advance();
			termParent.appendChild(compileExpression());

			// )
			jTokenizer.advance();
			tokenType = jTokenizer.tokenType();
			ele = createXMLnode(tokenType);
			termParent.appendChild(ele);

		}

		// Case 2: unaryOp term
		else if (token.matches("\\-|~")) {

			// unary op
			tokenType = jTokenizer.tokenType();
			String op = jTokenizer.returnTokenVal();
			ele = createXMLnode(tokenType);
			termParent.appendChild(ele);
			
			
			//Since it is postfix, the term comes first
			
			// term
			jTokenizer.advance();
			termParent.appendChild(compileTerm());

			// appending the op
			if (op.equals("~")) {
				writer.writeArithmetic("not");
			} else {
				writer.writeArithmetic("neg");
			}

		}
		
		// Any constant or keyword
		else if (tokenType.matches("keyword|integerConstant|stringConstant")) {
			ele = createXMLnode(tokenType);
			termParent.appendChild(ele);
			
			//pushing an integer constant
			if (tokenType.equals("integerConstant")) {
				writer.writePush("constant", Integer.parseInt(token));	
			}
			//For string, have to iterate along the length of the string and call string.append
			else if (tokenType.equals("stringConstant")) {
				writer.writePush("constant", token.length());
				writer.writeCall("String.new", 1);

				for (int i = 0; i < token.length(); i++) {
					writer.writePush("constant", (int) token.charAt(i));
					writer.writeCall("String.appendChar", 2);
				}
				
			} 
			//Pushing the keyword onto the stack, depending on what it is
			else if (tokenType.equals("keyword")) {
				if (token.equals("true")) {
					writer.writePush("constant", 0);
					writer.writeArithmetic("not");
				} else if (token.equals("this")) {
					writer.writePush("pointer", 0);
				} else if (token.equals("false") || token.equals("null")) {
					writer.writePush("constant", 0);
				}
			}
		}

		// Variable, Variable[expression] or subroutineCall
		else if (tokenType.equals("identifier")) {
			ele = createXMLnode(tokenType);
			termParent.appendChild(ele);
			varName = jTokenizer.returnTokenVal();
			jackTokenizer clone = new jackTokenizer(jTokenizer);
			clone.advance();
			token = clone.returnTokenVal();

			// Case 1: Array dereferencing
			if (token.equals("[")) {
				jTokenizer.advance();
				tokenType = jTokenizer.tokenType();
				ele = createXMLnode(tokenType);
				termParent.appendChild(ele);

				// push base id
				writer.writePush(symTable.lookup(varName).kind, symTable.lookup(varName).index);

				// Exp
				jTokenizer.advance();
				termParent.appendChild(compileExpression());

				// ]
				jTokenizer.advance();
				tokenType = jTokenizer.tokenType();
				ele = createXMLnode(tokenType);
				termParent.appendChild(ele);

				// base + offset
				writer.writeArithmetic("add");

				// pop into that
				writer.writePop("pointer", 1);
				// push value into stack
				writer.writePush("that", 0);
			}

			// Case 2: variable/class.subroutine call
			else if (token.equals(".")) {

				boolean method = false;

				// .
				jTokenizer.advance();
				tokenType = jTokenizer.tokenType();
				ele = createXMLnode(tokenType);
				termParent.appendChild(ele);

				// subroutine name
				jTokenizer.advance();
				tokenType = jTokenizer.tokenType();
				String subName = jTokenizer.returnTokenVal();
				ele = createXMLnode(tokenType);
				termParent.appendChild(ele);

				// (
				jTokenizer.advance();
				tokenType = jTokenizer.tokenType();
				ele = createXMLnode(tokenType);
				termParent.appendChild(ele);

				String firstName = varName;
				//Similar to the compileDo method, have to distinguish between
				//method and function
				if (symTable.lookup(firstName) != null) {
					method = true;
					writer.writePush(symTable.lookup(firstName).kind, symTable.lookup(firstName).index);
					varName = symTable.lookup(firstName).type;
				}
				// expressionList
				jTokenizer.advance();
				Element compileExpression = compileExpressionList();
				int nArgs = compileExpression.getChildNodes().getLength();
				termParent.appendChild(compileExpression);

				// Checking if method or function
				if (method) {
					nArgs++;
				}
				writer.writeCall(varName + "." + subName, nArgs);

				// )
				tokenType = jTokenizer.tokenType();
				ele = createXMLnode(tokenType);
				termParent.appendChild(ele);
			}

			// Case 3: function call
			else if (token.equals("(")) {
				// (
				jTokenizer.advance();
				tokenType = jTokenizer.tokenType();
				ele = createXMLnode(tokenType);
				termParent.appendChild(ele);

				// expression list
				jTokenizer.advance();
				Element node = compileExpressionList();
				int nArgs = node.getChildNodes().getLength();
				termParent.appendChild(node);

				// )
				tokenType = jTokenizer.tokenType();
				ele = createXMLnode(tokenType);
				termParent.appendChild(ele);

				// Writing the VML for a method call
				writer.writePush("pointer", 0);
				writer.writeCall(className + "." + varName, ++nArgs);
			}
			// Case 4: Variable name.
			else {
				writer.writePush(symTable.lookup(varName).kind, symTable.lookup(varName).index);
			}
		}
		return termParent;
	}

	// Iterates through a series of expressions and add XML code for each of them.
	public Element compileExpressionList() {
		Element ele = null;
		String token, tokenType;
		boolean contFlag = false;

		Element expListParent = document.createElement("expressionList");
		token = jTokenizer.returnTokenVal();
		if (token.equals(")")) {
			// expListParent.setTextContent("\n");
			return expListParent;
		}
		do {
			expListParent.appendChild(compileExpression());

			jTokenizer.advance();
			token = jTokenizer.returnTokenVal();

			if (token.equals(",")) {
				// tokenType= jTokenizer.tokenType();
				// ele= createXMLnode(tokenType);
				// expListParent.appendChild(ele);
				jTokenizer.advance();
				contFlag = true;
			} else {
				contFlag = false;
			}
		} while (contFlag);
		return expListParent;
	}
	
	//function to generate a unique label ever time its called
	public String label() {
		return "LABEL_" + (labelCount++);
	}
}
