
//Separate class that only has 3 data members to make mapping of variables to their info easier.
//Alternatives are using nested maps, using multiple maps etc.
public class symInfo {
	
	public String type;
	public String kind;
	public int index;
	
	public symInfo(String type, String kind, int index) {
		this.type= type;
		this.kind= kind;
		this.index= index;
	}
	
	
	
}
