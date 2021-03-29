Mult.asm
Multiplies the values in R0 and R1 and feeds the end result into R2.
Assumptions: R0 and R1 hold their values before the program begins execution (not updated after start of program).
Observations: Runtime of program is O(R0) regardless of whether it is the greater number of the two.


Fill.asm
Incrementally fills the screen with black pixels as long as any key is pressed. Starts whitening the screen from
the last black pixel as long as there is no key pressed.
Shortcomings: The whitening function starts from the screen location thats one more than the last black location. This is because whenever
a pixel is blackened, the counter is incremented. 
