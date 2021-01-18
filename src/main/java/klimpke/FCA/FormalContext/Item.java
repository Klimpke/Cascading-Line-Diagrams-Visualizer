package klimpke.FCA.FormalContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an object as used in formal concept analysis.
 * @author Klimpke
 */
public class Item {

	private int id;
	private int weight = 1;
	private String name;
	
	Set<Attribute> intent;
	
	public Item(int id, String name, int weight) {
		this.id = id;
		this.name = name;
		this.intent = new HashSet<Attribute>();
		this.weight = weight;
	}
	
	public void addAttribute(Attribute attribute) {
		intent.add(attribute);
	}
	
	public Intent getIntent(){
		return new Intent(intent);
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name.trim();
	}
	
	@Override
    public int hashCode() {
        return id;
    }
	
	@Override
	public String toString() {
		String toReturn = "["+id+" ("+name+"): ";
		for(Attribute attr: intent) {
			toReturn+= attr.getName()+",";
		}
		toReturn = toReturn.substring(0, toReturn.length()-1)+"]";
		
		return toReturn;
	}

	public int getWeight() {
		return weight;
	}
}