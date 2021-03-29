--------------------
Compile Instructions
--------------------

Navigate to the src folder and run "javac ./*.java"

----------------
Run Instructions
----------------

From the same folder, run "java VMdriver 'input path'" where input path is the path to the <input>.vm file or the folder that
contains the .vm files that need to be translated.
The path can be absolute, or relative as long as it is valid from the current directory.

-----------
Description
-----------

The program takes a .vm file or a folder with multiple .vm files and creates a new file <filename>.asm with the VM code converted to Hack assembly code.
The code has been tested for the following functionalities:
* Removing comments from the original .vm files. These new files are passed on as strings to the main driver class. Removing
  whitespaces would mess up the code, so I blanked out that section in the whiteSpaceRemover code.
* Parsing the files and converting each instruction from each .vm file to assembly code.
* Creating a new .asm file in the same directory as the .vm file or in the directory of the .vm files with the same name as the directory.
* Tests passed:
	* ProgramFlow
		*BasicLoop
		*FibonacciSeries
	* FunctionCalls
		*SimpleFunction
The above tests fail if the bootstrap code is present in the final file. To check for the above programs, simply comment out the 
section marked as Bootstrap section in VMdriver.java
The tests below pass with the bootstrap section NOT commented out.
		* FibonacciElement
		* NestedCall
		* StaticsTest

------------
Shortcomings
------------
* I could have reduced some redundant code here and there, as advised by project 7 grader. However, I did attempt to reduce
redundancies in the code added for this project.