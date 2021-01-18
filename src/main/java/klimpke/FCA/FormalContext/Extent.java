package klimpke.FCA.FormalContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import klimpke.FCA.General.ConceptLattice;
import klimpke.FCA.General.DrawStyle;
import klimpke.FCA.General.Position;

/**
 * This class represents the extent A of a formal concept (A,B).
 * @author Klimpke
 */
public class Extent {

	
	Map<Integer, Item> objects;
	private static Collection<Item> G;
	private Set<Extent> predecessors; //used for lattice creation
	private Set<Extent> successors;
	private int layer = -1;
	private Position[] pos = new Position[6];
	private boolean dragging = false;
	
	public Extent(Set<Item> items) {
		objects = new HashMap<Integer, Item>();
		predecessors = new HashSet<>();
		successors = new HashSet<>();
		for(Item item: items) {
			objects.put(item.getId(), item);
		}
	}
	
	public Extent(Item item) {
		objects = new HashMap<Integer, Item>();
		predecessors = new HashSet<>();
		successors = new HashSet<>();
		objects.put(item.getId(), item);
	}
	
	
	public Extent(Map<Integer, Item> items) {
		objects = items;
		predecessors = new HashSet<>();
		successors = new HashSet<>();
	}
	
	public Extent(Collection<Item> items) {
		objects = new HashMap<Integer, Item>();
		predecessors = new HashSet<>();
		successors = new HashSet<>();
		for(Item item: items) {
			objects.put(item.getId(), item);
		}
	}
	
	/**
	 * G is the Set of all Objects in the Context.
	 */
	public static void setG(Collection<Item> collection) {
		G = collection;
	}
	
	public static Collection<Item> getG(){
		return G;
	}

	public Collection<Item> getAllObjects(){
		return objects.values();
	}
	
	public Intent getIntent() {
		
		if(objects.values().size()==0) {
			return new Intent(Intent.getM());
		}
		
		Intent intent = null;
		for(Item object: objects.values()) {
			if(intent == null) {
				intent = object.getIntent();
			}else {
				intent = intent.intersect(object.getIntent());
			}
		}
		return intent;
	}
	
	public Intent prime() {
		return getIntent();
	}
	
	/**
	 * Two extents are equal if they contain the same objects.
	 */
	@Override
    public boolean equals(Object o) { 
     
        if (o == this) { 
            return true; 
        } 
  
        if (!(o instanceof Extent)) { 
            return false; 
        } 
          
        Extent e = (Extent) o; 
        
        if(this.getAllObjects().size() != e.getAllObjects().size()) {
        	return false;
        }
        
        for(Item item: e.getAllObjects()) {
        	if(!objects.containsKey(item.getId()) || !objects.get(item.getId()).equals(item)){
        		return false;
        	}
        }
        
        return true;
    }

	public Extent intersect(Extent extent) {
		Set<Item> toReturn = new HashSet<Item>();
		for(Item obj: objects.values()) {
			if(extent.containsItem(obj)) {
				toReturn.add(obj);
			}
		}
		return new Extent(toReturn);
	}
	
	public boolean containsItem(Item obj) {
		return objects.values().contains(obj);
	}

	@Override
	public String toString() {
		String toReturn = "Extent : [{";
		
		for(Item item: objects.values()) {
			toReturn += item.getName().trim()+", ";
		}
		if(objects.values().size()>0)
			toReturn = toReturn.substring(0, toReturn.length()-2);
		toReturn += "}:{";
		
		for(Attribute atr: prime().getAllAttributes()) {
			toReturn += atr.getName()+", ";
		}
		if(prime().getAllAttributes().size()>0)
			toReturn = toReturn.substring(0, toReturn.length()-2);
		toReturn += "}]";
		
		return toReturn;
	}
	
	
	public String toExtentString() {
		String toReturn = "Extent : {";
		
		for(Item item: objects.values()) {
			toReturn += item.getName().trim()+", ";
		}
		if(objects.values().size()>0)
			toReturn = toReturn.substring(0, toReturn.length()-2);
		toReturn += "}";
				
		return toReturn;
	}
	
	public String toIntentString() {
		String toReturn = "Intent : {";
		
		for(Attribute atr: prime().getAllAttributes()) {
			toReturn += atr.getName()+", ";
		}
		if(prime().getAllAttributes().size()>0)
			toReturn = toReturn.substring(0, toReturn.length()-2);
		toReturn += "}";
		
		return toReturn;
	}
	
	
	@Override
	public int hashCode() {
		int hash = 0;
		for(Item item: objects.values()) {
			hash += Math.pow(2, item.getId());
		}
		return hash;
	}

	public int getWeight() {
		int toReturn = 0;
		for(Item obj: objects.values()) {
			toReturn += obj.getWeight();
		}
		return toReturn;
	}
	
	public double getLogWeight() {
		return Math.log(getWeight()/ConceptLattice.getTotalWeight(false))/Math.log(2.0);
	}
	
	public double getWeight(DrawStyle style) {
		switch(style) {
		case CASCADING:
		case LOGARITHMIC:
			return getLogWeight();
		default:
			break;
		
		}
		return Double.NaN;
	}

	/**
	 * Unweighted!
	 */
	public int getNumberOfObjects() {
		return objects.values().size();
	}

	public boolean hasSubExtent(Extent e) {
		return objects.values().containsAll(e.getAllObjects());
	}
	
	public boolean isMissingPredecessors() {
		if(predecessors.size()==0) {
			return true;
		}
		
		Set<Object> allPredecessorObjects = new HashSet<>();
		Set<Attribute> allPredecessorAttributes = new HashSet<>();
		
		for(Extent e: predecessors) {
			allPredecessorObjects.addAll(e.getAllObjects());
			allPredecessorAttributes.addAll(e.getIntent().getAllAttributes());
		}
		
		boolean objectsComplete = allPredecessorObjects.containsAll(objects.values());
		boolean attributesComplete = getIntent().getAllAttributes().containsAll(allPredecessorAttributes);
		
		return !(objectsComplete && attributesComplete);
	}
	
	public boolean hasSuccessors() {
		return successors.size() != 0;
	}

	public void addPredecessor(Extent e) {
		predecessors.add(e);
	}
	
	public Set<Extent> getPredecessors(){
		return predecessors;
	}
	
	public int getNumberOfPredecessors() {
		return predecessors.size();
	}
	
	public void addSuccessor(Extent e) {
		successors.add(e);
	}
	
	public Set<Extent> getSuccessors(){
		return successors;
	}

	public boolean hasObjectLabel() {
		return successors.size()==1 || (isBottomExtent() && this.getAllObjects().size()>0);
	}

	public boolean hasAttributeLabel() {
		return predecessors.size()==1 || (isTopExtent() && this.getIntent().getAllAttributes().size()>0);
	}

	public Set<Attribute> getIrreducibleAttributes() {
		Set<Attribute> toReturn = new HashSet<>(getIntent().getAllAttributes());
		for(Extent pre: getPredecessors()) {
			for(Attribute a: pre.getIntent().getAllAttributes()) {
				if(toReturn.contains(a)) {
					toReturn.remove(a);
				}
			}
		}
		return toReturn;
	}
	
	public List<String> attributeLabel() {
		Collection<Attribute> label = getIntent().getAllAttributes();
		for(Extent predecessor: predecessors) {
			for(Attribute a: predecessor.getIntent().getAllAttributes()) {
				if(label.contains(a)) {
					label.remove(a);
				}
			}
		}
		
		List<String> toReturn = new ArrayList<>();
		if(label.size()>0) {
			for(Attribute a: label) {
				toReturn.add(a.getName());
			}
		}
		return toReturn;
	}

	public List<String> objectLabel() {
		Set<Item> toRemove = new HashSet<Item>();
		for(Extent successor: successors) {
			toRemove.addAll(successor.getAllObjects());
		}
		
		Set<Item> label = new HashSet<>();
		for(Item i : getAllObjects()) {
			if(!toRemove.contains(i)) {
				label.add(i);
			}
		}
		
		List<String> toReturn = new ArrayList<>();
		if(label.size()>0) {
			for(Item i: label) {
				toReturn.add(i.getName());
			}
		}
		return toReturn;
	}

	public int getNumberOfSuccessors() {
		return successors.size();
	}
	
	public boolean isTopExtent() {
		return getAllObjects().size() == G.size();
	}
	
	public boolean isBottomExtent() {
		return getIntent().getAllAttributes().size() == Intent.getM().size();
	}

	public void setLayer(int l) {
		this.layer = l;
	}
	
	public int getLayer() {
		return this.layer;
	}
	
	public boolean hasLayer() {
		return this.layer != -1;
	}

	/**
	 * Given that the given Extent already has successors.
	 * If the successors do not cover all attributes than it is possible, that there is still a predecessor missing.
	 * If the given Extent fills this role than it is a needed predecessor.
	 */
	public boolean isMissingPredecessor(Extent suc) {
		if(predecessors.size()==0) {
			return true;
		}
		
		Set<Attribute> pre = new HashSet<>();
		for(Extent e: predecessors) {
			pre.addAll(e.getIntent().getAllAttributes());
		}
		
		Collection<Attribute> current = getIntent().getAllAttributes();
		current.removeAll(pre);
		int currentDiff = current.size();
		
		current.removeAll(suc.getIntent().getAllAttributes());
		int newDiff = current.size();
		
		return newDiff < currentDiff;
	}
	
	public Position getPos(DrawStyle s) {
		return pos[s.id()];
	}

	public void setPos(Position newPos, DrawStyle s) {
		this.pos[s.id()] = newPos;
	}

	public void setPos(Position newPos) {
		for(DrawStyle style: DrawStyle.values()) {
			if(style!=DrawStyle.ADDITIVE)
				setPos(newPos, style);
		}
	}
	
	public Set<Extent> getIrreduciblePredecessors2(){
		Set<Extent> irreducible = new HashSet<>();
		
		List<Extent> reducible = new ArrayList<>(this.getPredecessors());
		while(reducible.size()!=0) {
			
			Extent e = reducible.get(0);
			reducible.remove(0);
			
			if(e.isIrreducible()) {
				irreducible.add(e);
			}else {
				reducible.addAll(e.getPredecessors());
			}
			
		}
		
		
		return irreducible;
	}
	
	/**
	 * Returns all irreduciblePredecessors
	 */
	public List<Extent> getIrreduciblePredecessors3(){
		List<Extent> irreducible = new ArrayList<>();
		
		for(Extent e: getAllPredecessors()) {
			if(e.isIrreducible()) {
				irreducible.add(e);
			}
		}
			
		return irreducible;
	}
	
	/**
	 * Returns not only the direct predecessors but also the predecessors of the predecessors
	 * up until the top element.
	 */
	public Set<Extent> getAllPredecessors(){
		Set<Extent> allPredecessors = new HashSet<>();
		List<Extent> predecessorsToCheck = new ArrayList<>(getPredecessors());
		
		while(predecessorsToCheck.size()>0) {
			Extent toCheck = predecessorsToCheck.get(0);
			allPredecessors.add(toCheck);
			predecessorsToCheck.addAll(toCheck.getPredecessors());
			predecessorsToCheck.remove(toCheck);
		}
		return allPredecessors;
	}
	
	
	public Set<Extent> getIrreduciblePredecessors() {
		Set<Extent> toReturn = new HashSet<>();
		if(this.isTopExtent()) {
			return toReturn;
		}
		
		for(Extent predecessor: this.getPredecessors()) {
			if(predecessor.getIrreducibleAttributes().size()!=0) {
				toReturn.add(predecessor);
			}
			toReturn.addAll(predecessor.getIrreduciblePredecessors());
		}
		
		return toReturn;
	}
	
	public boolean isIrreducible() {
		return this.getPredecessors().size()==1;
	}

	public List<String> getAttributeLabel() {
		List<String> label = new ArrayList<String>();

		for(Attribute attr: getIntent().getAllAttributes()) {
			Intent test = new Intent(attr);
			if(test.getExtent().getAllObjects().size()==getAllObjects().size()) {
				label.add(attr.getName());
			}
		}
		
		return label;
	}

	public List<String> getObjectLabel() {
		List<String> label = new ArrayList<String>();

		for(Item obj: getAllObjects()) {
			Extent test = new Extent(obj);
			if(test.getIntent().getAllAttributes().size() == getIntent().getAllAttributes().size()) {
				label.add(obj.getName());
			}
		}
		
		return label;
	}

	public void setDragging(boolean b) {
		this.dragging = b;
	}
	
	/**
	 * Returns true if the node belonging to this Extent is currently being dragged.
	 * @return
	 */
	public boolean isDragging() {
		return dragging;
	}

	/**
	 * Only for irreducible Extents -> returns null for every other extent
	 * subtracts the vectors position with its predecessor for the given drawStyle to receive a vector
	 * -> used for additive drawing
	 */
	public Position getVector(DrawStyle style) {
		if(!this.isIrreducible()) {
			if(this.isTopExtent()) {
				return this.getPos(style);
			}
			return null;
		}
		Extent pre = new ArrayList<>(this.getPredecessors()).get(0);
		return this.getPos(style).minus(pre.getPos(style)); //subtracting the positions to get the vector
	}

	public void setAdditiveLogXBasedOnPredecessor(Extent samePredecessor, DrawStyle style) {

		Position predictedPos = new Position(samePredecessor.getPos(style).getX(),samePredecessor.getPos(style).getY());
		for(Extent pre: getPredecessors()) {
			predictedPos.add(pre.getPos(style).minus(samePredecessor.getPos(style)));
		}
		Position one = samePredecessor.getPos(style);
		Position two = predictedPos;
		
		int newX = (two.getX()-one.getX())*(getPos(style).getY()-one.getY())/(two.getY()-one.getY()) + one.getX();
		int xDiff = newX-getPos(style).getX();
		setPos(new Position(newX, getPos(style).getY()),  style);
		for(Extent suc: getSuccessors()) {
			if(suc.isIrreducible()) {
				suc.setPos(new Position(suc.getPos(style).getX()+xDiff, suc.getPos(style).getY()),style);
			}
		}
	}
}