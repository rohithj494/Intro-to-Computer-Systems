--------------------
Compile and Run Instructions
--------------------

To compile using the nand2tetris compiler, pass the path to the src folder as an argument for it. This will create .vm files
inside the folder. Using this folder as an input for the vm Emulator allows you to run the program. Run the program in the
emulator at the highest speed and with no animation, with output as screen.

-----------
Description
-----------

The program attempts to emulate the Google Chrome Dinosaur game, where the player is a dinosaur and has to jump over cactii which
become faster gradually. 
The player has to press the UP arrow key to jump.
When there is a collision with a cactus, the game is over and he/she is given a choice to retry or exit.

------------
Shortcomings
------------
* As far as complexity goes, this game is exceedingly simple. There is only one cactus in any given moment on the screen. 
This is because dynamic object creation was to difficult for me to work with. Simple game --> Boring game.
* The movement of the object relative to the cactus is slightly jittery when the dino jumps. This is because of a problem in 
coordinating the movement of the two elements in a serial execution environment.
* Sometimes, an obstacles takes an extra timestep to be disposed so theres a slight lag there.
* Dull aesthetic: the game is just two rectangles and looks pretty dull. I added some clouds in the end but 
they just look like random lines. 

------------
References
------------

* Random number generator (by no means random) taken from:
https://gist.github.com/ybakos/7ca67fcfd07477a9550b- Original creator of the algorithm credited in Random.jack