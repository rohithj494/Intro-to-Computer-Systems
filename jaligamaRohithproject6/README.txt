--------------------
Compile Instructions
--------------------

Navigate to the src folder and run "javac ./*.java"

----------------
Run Instructions
----------------

From the same folder, run "java driver 'input path'" where input path is the path to the <input>.in file.
The path can be absolute, or relative as long as it is valid from the current directory.

-----------
Description
-----------

The program takes a file <filename>.asm and creates a new file <filename>.hack with the Hack assembly code converted to machine code.
The code has been tested for the following functionalities:
* Removing comments and whitespaces from the original .asm file. This new file is passed on as a string to the main driver class.
* Parsing the file and converting each instruction to machine code.
* Creating a new .hack file in the same directory as the .asm file.
* Tests passed:
	* Add
	* MaxL
	* Max
	* PongL
	* Pong
	* RectL
	* Rect

------------
Shortcomings
------------
* The code works for all functionality requirements tested so far, not sure if testing was exhaustive. 
* The symbol table is shared for line numbers and variable addresses, so there could be a clash, although I don't think that would be an error.