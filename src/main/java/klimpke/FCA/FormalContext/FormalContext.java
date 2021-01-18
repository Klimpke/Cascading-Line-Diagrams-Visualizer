package klimpke.FCA.FormalContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import klimpke.FCA.General.ConceptLattice;

public class FormalContext {

	Map<Integer, Item> mapExtend;
	Map<Integer, Attribute> mapIntent;
	
	
	public FormalContext(DefaultTableModel table) {
		
		mapExtend = getObjectsFromTable(table);
		mapIntent = getAttributesFromTable(table);
		
		Extent.setG(mapExtend.values());
		Intent.setM(mapIntent.values());
		
		for(int row = 1; row< table.getRowCount();row++) { //For each row = object
			for(int column = 1; column < table.getColumnCount();column++) { //for each column = attribute
				String value = (String) table.getValueAt(row, column);
				if(value.equals("X")) {
					Item object = mapExtend.get(row);
					Attribute attribute = mapIntent.get(column);
					object.addAttribute(attribute);
					attribute.addObject(object);
					
				}
			}
		}
		System.out.println("Attributes: "+mapIntent.values());
	    System.out.println("Objects: "+mapExtend.values());
		
	}
	
	private Map<Integer, Attribute> getAttributesFromTable(DefaultTableModel table) {
		Map<Integer,Attribute> toReturn = new HashMap<Integer, Attribute>();
		
		for(int i = 2; i< table.getColumnCount();i++) {
			toReturn.put(i, new Attribute(i, (String) table.getValueAt(0, i)));
		}
		
		return toReturn;
	}

	private Map<Integer, Item> getObjectsFromTable(DefaultTableModel table) {
		Map<Integer, Item> toReturn = new HashMap<Integer, Item>();
		
		for(int i = 1; i< table.getRowCount();i++) {
			toReturn.put(i, new Item(i, (String) table.getValueAt(i, 1), Integer.valueOf(String.valueOf(table.getValueAt(i, 0)))));
		}
		
		return toReturn;
	}
	
	public Set<Intent> getAllExtendsViaIntersectionMethod(){
		
		//1. For each attribute m elementOf M compute the attribute extent m prime
		List<Extent> allAtributeExtends = new ArrayList<Extent>();
		for(Attribute attr: mapIntent.values()) {
			allAtributeExtends.add(attr.getExtent());
		}
		
		
		//2. & 3. For any two sets in this list, compute their intersection. 
		//   If it is not yet contained in the list, add it.
		//   Repeat until no new extends are generated.
		List<Extent> toCombine = allAtributeExtends;
		Set<Extent> alreadyCombined = new HashSet<Extent>();
		while(toCombine.size()>0) {
			Extent current = toCombine.remove(0);
			for(Extent ext: alreadyCombined) {
				Extent intersection = current.intersect(ext);
				if(!toCombine.contains(intersection)&&!alreadyCombined.contains(intersection)&&!current.equals(intersection)) {
					toCombine.add(intersection);
				}
			}
			alreadyCombined.add(current);
		}
		
		//4. If G is not yet contained in the Set add it.
		Extent G = new Extent(mapExtend);
		alreadyCombined.add(G);
		
		System.out.println("Concept Extends:");
		for(Extent ext: alreadyCombined) {
			System.out.println(ext);
		}
		
		//5. For every extend in this A in the list compute the corresponding intent A prime.
		Set<Intent> toReturn = new HashSet<Intent>();
		for(Extent e: alreadyCombined) {
			toReturn.add(e.prime());
		}
		
		System.out.println("Concept Intents: ");
		for(Intent intent: toReturn) {
			System.out.println(intent);
		}
		
		return toReturn;		
	}
	
	public ConceptLattice getConceptLattice() {
		return new ConceptLattice(getAllExtendsViaIntersectionMethod());
	}


}
