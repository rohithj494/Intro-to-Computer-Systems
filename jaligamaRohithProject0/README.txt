--------------------
Compile Instructions
--------------------

Navigate to the src folder and run "javac ./noWhiteSpace.java"

----------------
Run Instructions
----------------

From the same folder, run "java noWhiteSpace 'input path'" where input path is the path to the <input>.in file.
The path can be absolute, or relative as long as it is valid from the current directory.

-----------
Description
-----------

The program takes a file <filename>.in and creates a new file <filename>.out with all the whitespaces and the comments removed.
The code has been tested for the following functionalities:
* Whitespaces (tabs and spaces)
* Blank lines
* Single line comment
    * With nothing else in the line
    * With code before the comment
* Multi Line comment
    * With nothing immediately before and after the comment
    * With code immediately before and immediately after the comment

* Input file in the same directory as the excutable
* Input file in a different directory
* Input file accessed through absolute and relative path


------------
Shortcomings
------------
* The code works for all functionality requirements tested so far, not sure if testing was exhaustive. 
* Code does not compile from a different directory than where the .java file is (Probably has something to do with setting the class path variable).