package klimpke.FCA.FormalContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the intent B of a formal concept (A,B).
 * @author Klimpke
 */
public class Intent {
	
	Map<Integer, Attribute> attributes;
	private static Collection<Attribute> M;  
	
	public Intent(Set<Attribute> attr) {
		attributes = new HashMap<Integer, Attribute>();
		for(Attribute attribute: attr) {
			attributes.put(attribute.getId(), attribute);
		}
	}
	
	public Intent(Attribute attr) {
		attributes = new HashMap<Integer, Attribute>();
		attributes.put(attr.getId(), attr);
	}
	
	public Intent(Map<Integer, Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public Intent(Collection<Attribute> attr) {
		attributes = new HashMap<Integer, Attribute>();
		for(Attribute attribute: attr) {
			attributes.put(attribute.getId(), attribute);
		}
	}
	
	/**
	 * M is the Set of all Attributes in the Context.
	 */
	public static void setM(Collection<Attribute> collection) {
		M = collection;
	}
	
	public static Collection<Attribute> getM() {
		return M;
	}

	public Collection<Attribute> getAllAttributes(){
		return attributes.values();
	}
	
	public Extent getExtent() {
		
		if(attributes.values().size()==0) {
			return new Extent(Extent.getG()); 
		}
		
		Extent extent = null;
		for(Attribute attr: attributes.values()) {
			if(extent == null) {
				extent = attr.getExtent();
			}else {
				extent = extent.intersect(attr.getExtent());
			}
		}
		if(extent == null) {
			extent = new Extent(new HashSet<Item>());
		}
		return extent;
	}
	
	public Extent prime() {
		return getExtent();
	}
	
	/**
	 * Two Intents are equal if they contain the same attributes.
	 */
	@Override
    public boolean equals(Object o) { 
     
        if (o == this) { 
            return true; 
        } 
  
        if (!(o instanceof Intent)) { 
            return false; 
        } 
          
        Intent i = (Intent) o; 
        
        if(this.getAllAttributes().size() != i.getAllAttributes().size()) {
        	return false;
        }
        
        for(Attribute attr: i.getAllAttributes()) {
        	if(!attributes.get(attr.getId()).equals(attr)){
        		return false;
        	}
        }
        
        return true;
    }

	public Intent intersect(Intent intent) {
		Collection<Attribute> attr= intent.getAllAttributes();
		attr.retainAll(attributes.values());
		return new Intent(attr);
	}

	@Override
	public String toString() {
		
		String toReturn = "Intent : [{";
		
		for(Attribute attr: attributes.values()) {
			toReturn += attr.getName()+", ";
		}
		if(attributes.values().size()>0)
			toReturn = toReturn.substring(0, toReturn.length()-2);
		toReturn += "}:{";
		
		for(Item object: prime().getAllObjects()) {
			toReturn += object.getName()+", ";
		}
		if(prime().getAllObjects().size()>0)
			toReturn = toReturn.substring(0, toReturn.length()-2);
		toReturn += "}]";
		
		return toReturn;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for(Attribute attr: attributes.values()) {
			hash += Math.pow(2, attr.getId());
		}
		return hash;
	}
}