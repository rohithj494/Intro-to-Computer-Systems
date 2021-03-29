// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

//Initialising the memory location i to the beginning of screen
@SCREEN
D=A
@i
M=D

//Main loop which checks if keyboard input is blank
(MAIN)
@KBD
D=M
@CLEAR
D;JEQ
@FILL
0;JMP


//Function that fills in the current pixel black, then increments after checking for overflow
(FILL)

//Colouring a pixel black
@i
A=M 		//Set the memory location to the contents of i (i is a pointer to a screen location)
M=-1

//If i is greater than of equal to keyboard-1, then dont increment and jump back to main
@i
D=M
@KBD
D=D-A
D=D+1
@MAIN
D;JGE

//Increment the value of i and jump to main
@i
M=M+1
@MAIN
0;JMP

//Function that clears the current location white, then decrements after checking for underflow
(CLEAR)

//Colouring a pixel white
@i
A=M 		
M=0

//if i is less than or equal to screen, dont decrement and jump to main
@i
D=M
@SCREEN
D=D-A
@MAIN
D;JLE

//Decrement the value of i and jump to main
@i
M=M-1
@MAIN
0;JMP