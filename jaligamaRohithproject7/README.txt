--------------------
Compile Instructions
--------------------

Navigate to the src folder and run "javac ./*.java"

----------------
Run Instructions
----------------

From the same folder, run "java VMdriver 'input path'" where input path is the path to the <input>.vm file.
The path can be absolute, or relative as long as it is valid from the current directory.

-----------
Description
-----------

The program takes a file <filename>.vm and creates a new file <filename>.asm with the VM code converted to Hack assembly code.
The code has been tested for the following functionalities:
* Removing comments from the original .asm file. This new file is passed on as a string to the main driver class. Removing
  whitespaces would mess up the code, so I blanked out that section in the whiteSpaceRemover code.
* Parsing the file and converting each instruction to assembly code.
* Creating a new .asm file in the same directory as the .vm file.
* Tests passed:
	* SimpleAdd
	* StackTest
	* BasicTest
	* PointerTest
	* StackTest

------------
Shortcomings
------------
* Based on the text, I added some clauses that don't really matter until functionality for project 8 is added (like checking if 
a statement is a return statement)