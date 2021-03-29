--------------------
Compile Instructions
--------------------

Navigate to the src folder and run "javac ./*.java"

----------------
Run Instructions
----------------

From the same folder, run "java driver 'input path'" where input path is the path to the <input>.jack file or the folder that
contains the .jack files that need to be compiled.
The path can be absolute, or relative as long as it is valid from the current directory.

-----------
Description
-----------

This project is an extension of project 10, where the program generates code.

The program takes a .jack file or a folder with multiple .jack files and creates a new file <filename>.vm with the jack code converted to vm code.
The code has been tested for the following functionalities:
* Removing comments from the original .jack files. I also remove leading spaces because there was an 
issue with splitting strings by spaces. These new files are passed on as strings to the main driver class. Removing
whitespaces would mess up the code, so I blanked out that section in the whiteSpaceRemover code.
* Parsing the files and saving each line of jack code in a map.
* Creating a new .xml file. The new xml is created, but the part where I write the xml out to a file
  is commented out since I only wanted to show the .vm output
* Creating .vm files with the same name as the .jack files. The directory can directly be selected from the vm emulator
  to run the programs.
* Tests passed:
	* Average
	* ComplexArrays
	* ConvertToBin
	* Pong
	* Seven
	* Square

------------
Shortcomings
------------
The code works for all the files without any hitches, except for the pong program, which for some reason, runs very slowly.
The functionality is perfect, but the game is just slower. This was discussed in class as well.

------------
Libraries used
------------
* To create XML objects and append new nodes to them, and to write the xml as a file, I have used two libraries:

1) javax.xml- Contains the majority of code needed to work with xml objects
2) org.w3c.dom- Contains objects Document and Element that make it easy to attach information to the XML node.

I wasn't sure if these packages are available straight out of the box, but the program compiles 
without any additional installs on the linux cluster.

------------
Fin.
------------

Apologies for the late submission. 
Thank you to the prof and the teaching staff for an extremely fun, engaging and challenging course.