import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class compilationEngine {
	public jackTokenizer jTokenizer;
	public Document document;
	public Element root;
	
	//Main constructor that uses the tokenizer object
	public compilationEngine(jackTokenizer tokenizer) {
		jTokenizer= tokenizer;
	}
	
	//Function used to create the XML node that is used to append to the tree
	public Element createXMLnode(String tokenType) {
		Element temp;
		temp= document.createElement(tokenType);
		temp.setTextContent(" "+jTokenizer.returnTokenVal(tokenType)+" ");
		return temp;
	}
	
	//"Main" function of the class. Returns an XML document 
	public Document compileClass() throws ParserConfigurationException, DOMException, CloneNotSupportedException {
		Element ele= null;
		String token;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();
		document= builder.newDocument();
		
		//Root level XML
		root= document.createElement("class");
		document.appendChild(root);
		
		//First token is "class"
		jTokenizer.advance();
		ele= createXMLnode("keyword");
		root.appendChild(ele);
		
		//Second token is the class name
		jTokenizer.advance();
		ele= createXMLnode("identifier");
		root.appendChild(ele);
		
		//Third token is '}'
		jTokenizer.advance();
		ele= createXMLnode("symbol");
		root.appendChild(ele);
		
		//Fourth token is the datatype of the class var declaration
		jTokenizer.advance();
		String tokenType;
		token= jTokenizer.returnTokenVal("keyword");
		
		do {
			//Declare the class variables
			if (token.matches("field|static")) {
				compileClassVarDec();
				
			}
			//Subroutine Declaration
			if (token.matches("constructor|function|method")) {
				compileSubroutine();
			}
			if (jTokenizer.hasNext()) {
				jTokenizer.advance();
				tokenType= jTokenizer.tokenType();
				token= jTokenizer.returnTokenVal(tokenType);
			}
			
		}while(!token.equals("}"));
		
		//Token "}"
		ele= createXMLnode("symbol");
		root.appendChild(ele);
		return document;
	}
	
	
	//Appends the XML for class variable declarations
	//At time of exit, the tokenizer is at the last token of the declaration (;)
	public void compileClassVarDec() {
		Element ele= null;
		String token, tokenType;
		tokenType= jTokenizer.tokenType();
		token= jTokenizer.returnTokenVal(tokenType);
		
		Element parentXML = document.createElement("classVarDec");
		ele= createXMLnode("keyword");
		parentXML.appendChild(ele);
		
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		token= jTokenizer.returnTokenVal(tokenType);
		ele= createXMLnode(tokenType);
		parentXML.appendChild(ele);
		
		jTokenizer.advance();
		while (!jTokenizer.returnTokenVal(tokenType).equals(";")) {
			tokenType= jTokenizer.tokenType();
			token= jTokenizer.returnTokenVal(tokenType);
			ele= createXMLnode(tokenType);
			parentXML.appendChild(ele);
			jTokenizer.advance();
		}
		
		//The ending symbol of the line (;)
		ele= createXMLnode("symbol");
		parentXML.appendChild(ele);
		root.appendChild(parentXML);
		}
	
	
	//Appends the XML for a subroutine to the parent
	//The tokenizer points to the last token of the subroutine on exit (})
	public void compileSubroutine() throws DOMException, CloneNotSupportedException {
		Element ele= null;
		String token, tokenType;
		tokenType= jTokenizer.tokenType();
		token= jTokenizer.returnTokenVal(tokenType);
		
		Element parentXML= document.createElement("subroutineDec");
		
		//method/constructor/funtion
		ele= createXMLnode(tokenType);
		parentXML.appendChild(ele);
		
		//Return type of the subroutine
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		token= jTokenizer.returnTokenVal(tokenType);
		ele=createXMLnode(tokenType);
		parentXML.appendChild(ele);
		
		//Name of the subroutine
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		token= jTokenizer.returnTokenVal(tokenType);
		ele=createXMLnode(tokenType);
		parentXML.appendChild(ele);
		
		//The character "("
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		token= jTokenizer.returnTokenVal(tokenType);
		ele=createXMLnode(tokenType);
		parentXML.appendChild(ele);
		
		//the list of parameters
		parentXML.appendChild(compileParameterList());
		
		//the symbol ")"
		token= jTokenizer.returnTokenVal();
		tokenType= jTokenizer.tokenType();
		ele=createXMLnode(tokenType);
		parentXML.appendChild(ele);
		
		//Body of subroutine
		Element body= document.createElement("subroutineBody");
		parentXML.appendChild(body);
		
		//Character "{"
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		ele=createXMLnode(tokenType);
		body.appendChild(ele);
		
		//The body of the subroutine that contains declarations and statements
		jTokenizer.advance();
		token= jTokenizer.returnTokenVal();
		while(!token.equals("}")) {
			tokenType= jTokenizer.tokenType();
			token= jTokenizer.returnTokenVal(tokenType);
			
			//XML for variable declarations inside the funtion
			if (token.equals("var")) {
				body.appendChild(compileVarDec());
				jTokenizer.advance();
			}
			
			//XML for statements inside the function
			else if (token.matches("let|if|while|do|return")) {
				body.appendChild(compileStatements());
			}
			
		}
		
		//Closing char "}"
		tokenType= jTokenizer.tokenType();
		ele=createXMLnode(tokenType);
		body.appendChild(ele);
		
		root.appendChild(parentXML);
	}
	
	
	
	//Appends the XML for a parameter list that occurs inside a subroutine declaration.
	public Element compileParameterList() {
		Element ele= null;
		String token, tokenType;
		Element parameterListParent= document.createElement("parameterList");
		
		
		//The first parameter type, or ")" if the parameter list is empty
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		token=jTokenizer.returnTokenVal(tokenType);
		if (!token.equals(")")) {
			ele=createXMLnode(tokenType);
			parameterListParent.appendChild(ele);
		}
		else {
//-------------------------Adding a newline character, otherwise the textcomparer
//--------------------unfortunately fails a self closing tag
			parameterListParent.setTextContent("\n");
			return parameterListParent;
		}
		
		//Parameters
		while(!token.equals(")")) {
			jTokenizer.advance();
			tokenType= jTokenizer.tokenType();
			token=jTokenizer.returnTokenVal(tokenType);
			if (!token.equals(")")) {
				ele=createXMLnode(tokenType);
				parameterListParent.appendChild(ele);
			}
		}
		
		return parameterListParent;
		
	}
	
	//Appends the XML for a line of variable declarations inside a subroutine
	public Element compileVarDec() {
		Element ele= null;
		String token, tokenType;
		
		Element varDecParent= document.createElement("varDec");
		
		//"var"
		tokenType=jTokenizer.tokenType();
		ele= createXMLnode(tokenType);
		varDecParent.appendChild(ele);
		
		//The type of the var
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		token=jTokenizer.returnTokenVal(tokenType);
		ele=createXMLnode(tokenType);
		varDecParent.appendChild(ele);
		
		//The variable identifiers themselves until ;
		
		
		while (!token.equals(";")) {
			jTokenizer.advance();
			tokenType= jTokenizer.tokenType();
			token= jTokenizer.returnTokenVal(tokenType);
			ele= createXMLnode(tokenType);
			varDecParent.appendChild(ele);
		}
		
		return varDecParent;
		
	}
	
	//Appends XML for the respective statement and iteratively checks for more
	//statements in the same block
	public Element compileStatements() throws CloneNotSupportedException {
		Element ele= null;
		String token, tokenType;
		
		boolean end=false;
		tokenType= jTokenizer.tokenType();
		token=jTokenizer.returnTokenVal(tokenType);
		Element statementsParent= document.createElement("statements");
		
		do {
			if (token.equals("let")) {
				ele= compileLet();
			}
			else if (token.equals("do")) {
				ele= compileDo();
			}
			else if (token.equals("if")) {
				ele= compileIf();
			}
			else if (token.equals("while")) {
				ele= compileWhile();
			}
			else if (token.equals("return")) {
				ele= compileReturn();
			}
			if (ele!= null)
			statementsParent.appendChild(ele);
			
			jTokenizer.advance();
			tokenType= jTokenizer.tokenType();
			token=jTokenizer.returnTokenVal(tokenType);
			if (!token.matches("let|if|while|do|return"))
				end= true;
		}while(!end);
		
		return statementsParent;
	}
	
	//Appends XML for a do statement.
	public Element compileDo() {
		Element ele= null;
		String token, tokenType;
		Element doParent= document.createElement("doStatement");
		
		
		//keyword do
		tokenType= jTokenizer.tokenType();
		token= jTokenizer.returnTokenVal();
		ele= createXMLnode(tokenType);
		doParent.appendChild(ele);
		
		//Name of subroutine
		jTokenizer.advance();
		token= jTokenizer.returnTokenVal();
		
		//Name can be multiple tokens long(class.method())
		do {
			ele= createXMLnode(jTokenizer.tokenType());
			doParent.appendChild(ele);
			
			jTokenizer.advance();
			token= jTokenizer.returnTokenVal();
		}while(!token.equals("("));
		
		//(
		ele= createXMLnode(jTokenizer.tokenType());
		doParent.appendChild(ele);
		
		//Expression List
		jTokenizer.advance();
		doParent.appendChild(compileExpressionList());
		
		//)
		ele=createXMLnode(jTokenizer.tokenType());
		doParent.appendChild(ele);
		
		//;
		jTokenizer.advance();
		ele=createXMLnode(jTokenizer.tokenType());
		doParent.appendChild(ele);
		
		return doParent;
	}
	
	//Appends XML for a let statement
	public Element compileLet() {
		Element ele= null;
		String token, tokenType;
		
		Element letParent= document.createElement("letStatement");
		
		//let
		tokenType= jTokenizer.tokenType();
		ele= createXMLnode(tokenType);
		letParent.appendChild(ele);
		
		//identifier
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		ele= createXMLnode(tokenType);
		letParent.appendChild(ele);
		
		//Checks if the variable is an array element
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		token=jTokenizer.returnTokenVal();
		if (token.equals("[")) {
			
			//[
			ele= createXMLnode(tokenType);
			letParent.appendChild(ele);
			
			//Expression that comes after [
			jTokenizer.advance();
			letParent.appendChild(compileExpression());
			
			//]
			jTokenizer.advance();
			tokenType= jTokenizer.tokenType();
			ele= createXMLnode(tokenType);
			letParent.appendChild(ele);
			jTokenizer.advance();
		}
		
		//=
		tokenType=jTokenizer.tokenType();
		ele= createXMLnode(tokenType);
		letParent.appendChild(ele);
		
		//expression on the right side of the equals
		jTokenizer.advance();
		letParent.appendChild(compileExpression());
		
		//;
		jTokenizer.advance();
		ele=createXMLnode(jTokenizer.tokenType());
		letParent.appendChild(ele);
		return letParent;
	}
	
	//Appends XML for a while block. Everything including the ending "}" is in this block
	public Element compileWhile() throws DOMException, CloneNotSupportedException {
		Element ele= null;
		String token, tokenType;
		
		Element whileParent= document.createElement("whileStatement");
		
		//while keyword
		tokenType= jTokenizer.tokenType();
		ele= createXMLnode(tokenType);
		whileParent.appendChild(ele);
		
		//(
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		ele= createXMLnode(tokenType);
		whileParent.appendChild(ele);
		
		
		//expression
		jTokenizer.advance();
		whileParent.appendChild(compileExpression());
		
		//)
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		ele= createXMLnode(tokenType);
		whileParent.appendChild(ele);
		
		//{
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		ele= createXMLnode(tokenType);
		whileParent.appendChild(ele);
		
		//Statements inside the while loop
		jTokenizer.advance();
		whileParent.appendChild(compileStatements());
		
		//}
		tokenType= jTokenizer.tokenType();
		ele= createXMLnode(tokenType);
		whileParent.appendChild(ele);
		
		return whileParent;
		
	}
	
	//XML for a return statement
	public Element compileReturn() {
		Element ele= null;
		String token, tokenType;
		
		Element returnParent= document.createElement("returnStatement");
		
		//return keyword
		tokenType= jTokenizer.tokenType();
		ele= createXMLnode(tokenType);
		returnParent.appendChild(ele);
		
		jTokenizer.advance();
		token= jTokenizer.returnTokenVal();
		
		//Return can either just be nothing or a variable/expression etc.
		if (token.equals(";")) {
			tokenType= jTokenizer.tokenType();
			ele=createXMLnode(tokenType);
			returnParent.appendChild(ele);
			
			return returnParent;
		}
		else {
			returnParent.appendChild(compileExpression());
		}
		
		//;
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		ele=createXMLnode(tokenType);
		returnParent.appendChild(ele);
		
		
		return returnParent;
	}
	
	//XML for an if block. Includes an else block if exists
	public Element compileIf() throws CloneNotSupportedException {
		Element ele= null;
		String token, tokenType;
		
		Element ifParent= document.createElement("ifStatement");
		
		
		//if
		tokenType= jTokenizer.tokenType();
		ele=createXMLnode(tokenType);
		ifParent.appendChild(ele);
		
		//(
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		ele=createXMLnode(tokenType);
		ifParent.appendChild(ele);
		
		//expression
		jTokenizer.advance();
		ifParent.appendChild(compileExpression());
		
		//)
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		ele=createXMLnode(tokenType);
		ifParent.appendChild(ele);
		
		//{
		jTokenizer.advance();
		tokenType= jTokenizer.tokenType();
		ele=createXMLnode(tokenType);
		ifParent.appendChild(ele);
		
		//statement inside the if block
		jTokenizer.advance();
		ifParent.appendChild(compileStatements());
		
		//}
		tokenType= jTokenizer.tokenType();
		ele=createXMLnode(tokenType);
		ifParent.appendChild(ele);
		
		//else
		
		//Interesting coding challenge. I had to look ahead by one token to 
		//check if there was an else block, but if it didn't exists, I would be 
		//one token ahead of the XML.
		//Built a clone of the present tokenizer and used it to look ahead, 
		//if an else exists, advance the main tokenizer as well
		jackTokenizer clone= new jackTokenizer(jTokenizer);
		clone.advance();
		
		token= clone.returnTokenVal();
		if(token.equals("else")) {
			
			//else
			jTokenizer.advance();
			tokenType= jTokenizer.tokenType();
			ele= createXMLnode(tokenType);
			ifParent.appendChild(ele);
			
			//{
			jTokenizer.advance();
			tokenType= jTokenizer.tokenType();
			ele=createXMLnode(tokenType);
			ifParent.appendChild(ele);
			
			//statements inside the else block
			jTokenizer.advance();
			ifParent.appendChild(compileStatements());
			
			//}
			tokenType= jTokenizer.tokenType();
			ele=createXMLnode(tokenType);
			ifParent.appendChild(ele);
			
			return ifParent;
		}
		else {
			return ifParent;
		}
	}
	
	//XML code for an expression term (op term )*
	public Element compileExpression() {
		Element ele= null;
		String token, tokenType;
		boolean op;
		Element expressionParent= document.createElement("expression");
		
		//if the term-op-term-op cycle breaks, that means its the end of the expression
		do {
			
			//At least one term
			expressionParent.appendChild(compileTerm());
			
			jackTokenizer clone= new jackTokenizer(jTokenizer);
			clone.advance();
			token= clone.presentToken;
			
			// zero or more ops
			if (token.matches("\\+|-|\\*|/|\\&|\\||<|=|>|~")) {
				jTokenizer.advance();
				tokenType= jTokenizer.tokenType();
				ele= createXMLnode(tokenType);
				expressionParent.appendChild(ele);
				jTokenizer.advance();
				op=true;
			}
			else {
				op=false;
			}
		}while (op);
		
		return expressionParent;
	}
	
	
	//XML code for a term. Recursively calls the constituent functions as described in the text
	public Element compileTerm() {
		Element ele= null;
		String token, tokenType;
		
		Element termParent= document.createElement("term");
		
		token= jTokenizer.returnTokenVal();
		tokenType= jTokenizer.tokenType();
		
		//Case 1: ( expression )
		
		if (token.equals("(")) {
			//(
			ele= createXMLnode(tokenType);
			termParent.appendChild(ele);
			
			//exp
			jTokenizer.advance();
			termParent.appendChild(compileExpression());
			
			//)
			jTokenizer.advance();
			tokenType= jTokenizer.tokenType();
			ele= createXMLnode(tokenType);
			termParent.appendChild(ele);
			
		}
		
		//Case 2: unaryOp term
		else if (token.matches("\\-|~")){
			
			//unary op
			tokenType=jTokenizer.tokenType();
			ele=createXMLnode(tokenType);
			termParent.appendChild(ele);
			
			//term
			jTokenizer.advance();
			termParent.appendChild(compileTerm());
			
		}
		//Any constant or keyword
		else if (tokenType.matches("keyword|integerConstant|stringConstant")) {
			ele=createXMLnode(tokenType);
			termParent.appendChild(ele);
			
		}
		
		//Variable, Variable[expression] or subroutineCall
		else if (tokenType.equals("identifier")){
			ele= createXMLnode(tokenType);
			termParent.appendChild(ele);
			jackTokenizer clone= new jackTokenizer(jTokenizer);
			clone.advance();
			token=clone.returnTokenVal();
			
			//Case 1: Array dereferencing
			if (token.equals("[")) {
				jTokenizer.advance();
				tokenType=jTokenizer.tokenType();
				ele=createXMLnode(tokenType);
				termParent.appendChild(ele);
				
				
				//Exp
				jTokenizer.advance();
				termParent.appendChild(compileExpression());
				
				//]
				jTokenizer.advance();
				tokenType=jTokenizer.tokenType();
				ele= createXMLnode(tokenType);
				termParent.appendChild(ele);
			}
			
			//Case 2: class.subroutine call
			else if (token.equals(".")) {
				//.
				jTokenizer.advance();
				tokenType= jTokenizer.tokenType();
				ele=createXMLnode(tokenType);
				termParent.appendChild(ele);
				
				//subroutine name
				jTokenizer.advance();
				tokenType= jTokenizer.tokenType();
				ele=createXMLnode(tokenType);
				termParent.appendChild(ele);
				
				//(
				jTokenizer.advance();
				tokenType= jTokenizer.tokenType();
				ele=createXMLnode(tokenType);
				termParent.appendChild(ele);
				
				//expressionList
				jTokenizer.advance();
				termParent.appendChild(compileExpressionList());
				
				//)
				tokenType= jTokenizer.tokenType();
				ele=createXMLnode(tokenType);
				termParent.appendChild(ele);
			}
			
			//Case 3: function call
			else if (token.equals("(")) {
				//(
				jTokenizer.advance();
				tokenType= jTokenizer.tokenType();
				ele=createXMLnode(tokenType);
				termParent.appendChild(ele);
				
				//expression list
				jTokenizer.advance();
				termParent.appendChild(compileExpressionList());
				
				//)
				tokenType= jTokenizer.tokenType();
				ele=createXMLnode(tokenType);
				termParent.appendChild(ele);
			}
			}
		return termParent;
		}
	
	
	//Iterates through a series of expressions and add XML code for each of them.
	public Element compileExpressionList() {
		Element ele= null;
		String token, tokenType;
		boolean contFlag= false;
		
		Element expListParent= document.createElement("expressionList");
		token= jTokenizer.returnTokenVal();
		if (token.equals(")")) {
			expListParent.setTextContent("\n");
			return expListParent;
		}
		do {
			expListParent.appendChild(compileExpression());
			
			jTokenizer.advance();
			token= jTokenizer.returnTokenVal();
			
			if (token.equals(",")) {
				tokenType= jTokenizer.tokenType();
				ele= createXMLnode(tokenType);
				expListParent.appendChild(ele);
				jTokenizer.advance();
				contFlag=true;
			}
			else {
				contFlag=false;
			}
		}while (contFlag);
		return expListParent;
	}
}
