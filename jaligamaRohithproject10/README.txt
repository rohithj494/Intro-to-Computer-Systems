--------------------
Compile Instructions
--------------------

Navigate to the src folder and run "javac ./*.java"

----------------
Run Instructions
----------------

From the same folder, run "java driver 'input path'" where input path is the path to the <input>.jack file or the folder that
contains the .jack files that need to be analysed.
The path can be absolute, or relative as long as it is valid from the current directory.

-----------
Description
-----------

The program takes a .jack file or a folder with multiple .vm files and creates a new file <filename>.xml with the jack code converted to xml tree.
The code has been tested for the following functionalities:
* Removing comments from the original .jack files. I also remove leading spaces because there was an 
issue with splitting strings by spaces. These new files are passed on as strings to the main driver class. Removing
whitespaces would mess up the code, so I blanked out that section in the whiteSpaceRemover code.
* Parsing the files and saving each line of jack code in a map.
* Creating a new .xml file in a sub folder of the input folder
* Tests passed:
	* ArrayTest
	* ExpressionLessSquare
	* Square

------------
Libraries used
------------
* To create XML objects and append new nodes to them, and to write the xml as a file, I have used two libraries:

1) javax.xml- Contains the majority of code needed to work with xml objects
2) org.w3c.dom- Contains objects Document and Element that make it easy to attach information to the XML node.

I wasn't sure if these packages are available straight out of the box, but the program compiles 
without any additional installs on the linux cluster.


------------
References
------------

XML in java: https://www.tutorialspoint.com/java_xml/index.htm