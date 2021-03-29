import java.util.HashMap;

public class symbolTable {
	
	public HashMap<String, symInfo> classTable;
	public HashMap<String, symInfo> subroutineTable;
	public HashMap<String, Integer> count;
	
	
	public symbolTable() {
		//Symbol table for an entire class
		classTable= new HashMap<String, symInfo>();
		
		//Symbol table for a particular subroutine
		subroutineTable= new HashMap<String, symInfo>();
		
		//Holds the number of each type of variable encountered
		count= new HashMap<String, Integer>();
		
		count.put("argument", 0);
		count.put("this", 0);
		count.put("static", 0);
		count.put("local", 0);
	}
	
	public void startSubroutine(){
		
		//Starts a new symbol table for a subroutine
        subroutineTable.clear();
        count.put("local",0);
        count.put("argument",0);
    }
	
//Maps a variable to its information: type, kind and index.
//Static and field(this) are added to class table, rest to subroutine table
public void define(String name, String type, String kind) {
		if (kind.equals("static")||kind.equals("this")) {
			int index= count.get(kind);
			symInfo symbol= new symInfo(type, kind, index);
			count.put(kind, index+1);
			classTable.put(name, symbol);
		}
		else if (kind.equals("argument")||kind.equals("local")) {
			int index= count.get(kind);
			symInfo symbol= new symInfo(type, kind, index);
			count.put(kind, index+1);
			subroutineTable.put(name, symbol);
		}
	}

//Searches for the indentifier in both the tables and returns the information if found
public symInfo lookup(String identifier) {
	if (subroutineTable.get(identifier)!=null) {
		return subroutineTable.get(identifier);
	} 
	else if (classTable.get(identifier)!=null) {
		return classTable.get(identifier);
	}
	else return null;
}
}
