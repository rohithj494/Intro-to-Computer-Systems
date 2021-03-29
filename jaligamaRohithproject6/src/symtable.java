

import java.util.HashMap;

public class symtable {
	//Symbol table pre initialised with the default memory locations. This program shares the same table
	//for labels and variables.
	static HashMap<String, Integer> symtable= new HashMap<String, Integer>(){
		{
			put("SP", 0);
			put("LCL", 1);
			put ("ARG", 2);
			put ("THIS", 3);
			put("THAT", 4);
			put("R0", 0);
			put("R1", 1);
			put("R2", 2);
			put("R3", 3);
			put("R4", 4);
			put("R5", 5);
			put("R6", 6);
			put("R7", 7);
			put("R8", 8);
			put("R9", 9);
			put("R10", 10);
			put("R11", 11);
			put("R12", 12);
			put("R13", 13);
			put("R14", 14);
			put("R15", 15);
			put("SCREEN", 16384);
			put ("KBD", 24576);
		}
	};
	
	//Returns true if the key is present in the table
	public boolean has (String key) {
		if (symtable.containsKey(key))
			return true;
		else
			return false;
	}
	
	//Adds a key, value pair to the table
	public void addSym (String key, int value) {
		symtable.put(key, value);
	}
	
	//Returns the value related to a key
	public int getAdd (String key) {
		return symtable.get(key);
	}
	
}
