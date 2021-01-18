package klimpke.FCA.FormalContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Klimpke
 */
public class Attribute {

	private int id;
	private String name;
	
	Set<Item> extent;
	
	public Attribute(int id, String name) {
		this.id = id;
		this.name = name;
		this.extent = new HashSet<Item>();
	}
	
	public void addObject(Item object) {
		extent.add(object);
	}
	
	public Extent getExtent(){
		return new Extent(extent);
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
		for(Item ex: extent) {
			toReturn+= ex.getName()+",";
		}
		toReturn = toReturn.substring(0, toReturn.length()-1)+"]";
		
		return toReturn;
	}
}
