package klimpke.FCA.General;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import klimpke.FCA.FormalContext.Extent;
import klimpke.FCA.FormalContext.Intent;
import klimpke.FCA.FormalContext.Item;

/**
 * 
 * @author Klimpke
 *
 */
public class ConceptLattice {
	
	Map<Integer, Set<Extent>> lattice;

	private int widthMult = 100;
	public final static int heightMult = 100;
	
	public ConceptLattice(Set<Intent> intents){
		
		Set<Extent> extentSet = intents.stream()
					 .map(x -> x.getExtent())
					 .collect(Collectors.toSet());
	
		System.out.println("All extents: "+extentSet.size());
		
		extentSet = createExtentOrder(extentSet);

        lattice = createLattice(extentSet);
	    
		for(int i = 0; i< getNumberOfLayers();i++) {
			System.out.println("Layer "+i);
			for(Extent e: lattice.get(i)) {
				System.out.print(e+" Successors: ");
				for(Extent suc: e.getSuccessors()) {
					System.out.print(suc);
				}
				System.out.print("\n");
			}
		}
				
	}
	
	public void initializePositions(int windowHeight, int windowWidth) {

		for(Extent e: getAllExtents()) {
			e.setPos(null);
		}
		setBasicLattice(windowWidth, windowHeight);
		scaleYAxis(windowHeight);
		setAdditivePositions(windowWidth,windowHeight);//additive and additive_log initialized the same
		scaleYAxisLogarithmic(windowHeight); //For normal x and additive_log
		scaleXAxisAdditiveLogarithmic(windowWidth, windowHeight);
	}


	private Map<Integer, Set<Extent>> createLattice(Set<Extent> extentSet) {
		Map<Integer, Set<Extent>> lattice = new HashMap<>();
		for(Extent e: extentSet) {
			Set<Extent> layer = lattice.containsKey(e.getLayer())? lattice.get(e.getLayer()): new HashSet<>();
			layer.add(e);
			lattice.put(e.getLayer(), layer);
		}
		
		return lattice;
	}

	private Set<Extent> createExtentOrder(Set<Extent> unordered) {
		Set<Extent> ordered = new HashSet<>();
		
		Extent top = getTopElement(unordered);
		top.setLayer(0);
		ordered.add(top);
		
		int layer = 1;
		
		int maxNumOfEle = top.getNumberOfObjects();
		
		for(int numOfEle = maxNumOfEle - 1; numOfEle >= 0; numOfEle--) {
			boolean layerAdded = false;
			
			for(Extent e: unordered) {
				if(e.getNumberOfObjects() == numOfEle) {
					
					int successorLayer = layer;
					
					while(successorLayer > 0) {
						successorLayer --;
						for(Extent suc: ordered) {
							if(suc.getLayer()==successorLayer) {
								if((e.isMissingPredecessor(suc) || successorLayer == layer -1) && suc.hasSubExtent(e)) {
									e.addPredecessor(suc);
									suc.addSuccessor(e);
									if(!e.hasLayer()) {
										e.setLayer(successorLayer +1);
										if(successorLayer +1 == layer) {
											layerAdded = true;
										}
									}
								}	
							}
						}
					}
					ordered.add(e);
				}
			}
			
			if(layerAdded) {
				layer ++;
			}
		}
		
		Extent bottom = getBottomElement(ordered);
		for(Extent e : ordered) {
			if(!e.equals(bottom) && !e.hasSuccessors()) {
				e.addSuccessor(bottom);
				bottom.addPredecessor(e);
			}
		}
		
		return ordered;
	}

	private Extent getTopElement(Set<Extent> ext) {
		for(Extent e: ext) {
			if(e.isTopExtent()) {
				return e;
			}
		}
		return null;
	}
	
	private Extent getBottomElement(Set<Extent> ext) {
		for(Extent e: ext) {
			if(e.isBottomExtent()) {
				return e;
			}
		}
		return null;
	}

	
	public int getNumberOfLayers() {
		return lattice.keySet().stream().reduce(Integer.MIN_VALUE, (a, b) -> Integer.max(a, b))+1;//Layers count start with 0, therefore add 1
	}
	
	public int getDrawHeight() {
		return heightMult * (getNumberOfLayers()+1);
	}
	
	/**
	 * Number of extents in the widest layer.(= highest number of extents per layer in this lattice)
	 */
	private int getWidth() {
		int width = 0;
		for(int i : lattice.keySet()) {
			if(lattice.get(i).size()>width)
				width = lattice.get(i).size();
		}
		return width;
	}
	
	public int getDrawWidth() {
		return getWidth() * widthMult;
	}
	
	/**
	 * Highest Layer starts with id 0;
	 */
	public Set<Extent> getLayer(int i){
		return lattice.get(i);
	}

	public void setBasicLattice(int drawWidth, int drawHeight) {
		for(int layerId = 0; layerId<getNumberOfLayers();layerId++) {
	    	int pos = 1;
	    	for(Extent e :getLayer(layerId)) {
	    		
	    		int x;
	    		if(e.getIrreduciblePredecessors2().size()==0) { //has no predecessors that are irreducible
	    			x = (drawWidth/(getLayer(layerId).size()+1))*pos;
	    		}else {
	    			x = getPositionBasedOnIrreduciblePredecessorsFor(e, DrawStyle.UNSCALED);
	    			
	    		}
	    		int height = Math.round(((drawHeight -100) * (layerId))/ (getNumberOfLayers()-1));
				int y = height + 40;
				
	    		e.setPos(new Position(x,y));
	    		
	    		pos ++;
	    	}
	    }
	}

	private int getPositionBasedOnIrreduciblePredecessorsFor(Extent e, DrawStyle style) {

		Set<Extent> irrPredecessors = e.getIrreduciblePredecessors2();
		int weight = 0;
		int x = 0;
		for(Extent irrPre: irrPredecessors) {
			weight = weight + irrPre.getIrreducibleAttributes().size();
			
			x = x + (irrPre.getIrreducibleAttributes().size() * irrPre.getPos(style).getX());
		    //x = x + ((irrPre.getPos(DrawStyle.unscaled).getX() - x)/2);
		}
		if(weight == 0)
			return -1;
		
		x = x / weight;
		return x;

	}

	/**
	 * Scales the y-Axis according to the weights of each Extent.
	 */
	public void scaleYAxis(int windowHeight) {
		
		double totalWeight = getTotalWeight(false);
		
		for(Extent extent: getAllExtents()) {
			Position pos = extent.getPos(DrawStyle.SCALED);
			int newY = getY(windowHeight, totalWeight, extent.getWeight());
			extent.setPos(new Position(pos.getX(), newY), DrawStyle.SCALED);
		}
	}
	
	/**
	 * Calculates the y-coordinate.
	 * @param windowHeight total height of the canvas
	 * @param totalWeight highest scalable value
	 * @param weight scalable value
	 */
	public static int getY(int windowHeight, double totalWeight, double weight) {
		int height = (int) Math.round(((windowHeight-150)*weight)/ (totalWeight));
		int newY = windowHeight - height - 100;
		return newY;
	}
	
	/**
	 * Scales the y-Axis logarithmic according to the weights of each Extent.
	 */
	public void scaleYAxisLogarithmic(int windowHeight) {
		
		double totalWeight = getTotalWeight(true);
		
		for(Extent extent: getAllExtents()) {
			Position pos = extent.getPos(DrawStyle.LOGARITHMIC);
		
			double logWeight = extent.getLogWeight()+totalWeight; 
			
			int newY = getY(windowHeight, totalWeight, logWeight);
			
			if(extent.getWeight()==0) {
				newY = -1;
			}
			extent.setPos(new Position(pos.getX()+50, newY), DrawStyle.LOGARITHMIC);
			extent.setPos(new Position(extent.getPos(DrawStyle.CASCADING).getX(), newY),DrawStyle.CASCADING);
		}
	}
	
	
	public static double getTotalWeight(boolean logarithmic) {
		double totalWeight = 0;
		double smallestWeight = Double.MAX_VALUE;
		for(Item i: Extent.getG()) {
			totalWeight += i.getWeight();
			if(smallestWeight > i.getWeight()) {
				smallestWeight = i.getWeight();
			}	
		}
		
		if(logarithmic) {
			totalWeight = Math.abs(Math.log(smallestWeight/totalWeight)/Math.log(2.0));
		}
		return totalWeight;
	}
	
	public Set<Extent> getAllExtents(){
		return lattice.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
	}


	private void scaleXAxisAdditiveLogarithmic(int windowWidth, int windowHeight) {
		//Layer 0 and 1 are no additive Extends -> start with layer 2
		for(int layer = 2; layer < this.getNumberOfLayers();layer++) {
			for(Extent e: lattice.get(layer)) {
				if(e.isIrreducible()) {
					//Do nothing -> irreducible are set when their parent is set -> move their x with the same x as their parent -> cascading
				}else {
					//search for highest ancestor that the predecessors share
					//Extent samePredecessor = findClosestSharedPredecessor(e);
					Extent samePredecessor = getTopElement();
					e.setAdditiveLogXBasedOnPredecessor(samePredecessor, DrawStyle.CASCADING);//also sets positions of irreducible successors 
				}
			}
		}
	}

	private void setAdditivePositions(int windowWidth, int windowHeight) {
		
		//0. set all positions to null
		for(Extent e: getAllExtents()) {
			e.setPos(null, DrawStyle.ADDITIVE);
			e.setPos(null, DrawStyle.CASCADING);
		}
		//Plan lattice from top to bottom layer with a raster and then stretch to given height/width
		//If Extent is irreducible it is 1 field below its predecessor (stretched if there are several) 
		//If extent is reducible -> sum of irreducible vectors
		//1. top element -> 0/0 (x/y) + set all children
		for(int layer = 0; layer < getNumberOfLayers();layer++) {
			for(Extent e: lattice.get(layer)) {
				if(e.getPos(DrawStyle.ADDITIVE)==null) { //no position set yet -> top extent or reducible
					Set<Extent> irrPres = e.getIrreduciblePredecessors();

					Position pos = new Position(0,0);
					for(Extent pre: irrPres) { //For every irreducible extent that defines e
						pos.add(pre.getVector(DrawStyle.ADDITIVE)); //add its vector
					}
					e.setPos(pos, DrawStyle.ADDITIVE);
					e.setPos(pos, DrawStyle.CASCADING);
					//find all irreducible children and set their positions
					findAndPositionAllIrreducibleChildrenBelow(e);
				}
			}
		}
		//2. stretch to fill windowWidth and WindowHeight
		int minX = 0;
		int maxX = 0;
		int maxY = 0;
		
		for(Extent e: getAllExtents()) {
			
			if(e.getPos(DrawStyle.ADDITIVE).getX()<minX) {
				minX = e.getPos(DrawStyle.ADDITIVE).getX();
			}
			
			if(e.getPos(DrawStyle.ADDITIVE).getX()>maxX) {
				maxX = e.getPos(DrawStyle.ADDITIVE).getX();
			}
			
			if(e.getPos(DrawStyle.ADDITIVE).getY()>maxY) {
				maxY = e.getPos(DrawStyle.ADDITIVE).getY();
			}
		}
		double scaleX = (windowWidth-100)/(maxX-minX);
		double scaleXLog = (windowWidth-200)/(maxX-minX);
		double scaleY = (windowHeight-100)/maxY;
		
		for(Extent e: getAllExtents()) {
			int gridX = e.getPos(DrawStyle.ADDITIVE).getX();
			int gridY = e.getPos(DrawStyle.ADDITIVE).getY();
			
			e.setPos(new Position((int)Math.round(50+ ((gridX + Math.abs(minX))*scaleX)), (int) Math.round((gridY*scaleY) + 40)), DrawStyle.ADDITIVE);
			e.setPos(new Position((int)Math.round(150+ ((gridX + Math.abs(minX))*scaleXLog)), (int) Math.round((gridY*scaleY) + 40)), DrawStyle.CASCADING);
		}
		
	}

	private void findAndPositionAllIrreducibleChildrenBelow(Extent e) {
		List<Extent> irrChildren = new ArrayList<>();
		
		for(Extent child: e.getSuccessors()) {
			if(child.isIrreducible()) {
				irrChildren.add(child);
			}
		}
		
		//set positions of irreducible children
		for(int i = 0; i< irrChildren.size();i++){
			//1 irreducible child -> directly below -> deltaX = 0
			//2 irr children -> deltaX = -1; 1
			//3 irr Children -> deltaX = -2;0;2 (first child, second child, third child)
			//4 irr children -> -3; -1; 1; 3
			int deltaX = (1 - irrChildren.size()) + (i * 2); 
			irrChildren.get(i).setPos(new Position(e.getPos(DrawStyle.ADDITIVE).getX()+deltaX, e.getPos(DrawStyle.ADDITIVE).getY()+1), DrawStyle.ADDITIVE);
		}
		
		for(Extent child: irrChildren) {
			findAndPositionAllIrreducibleChildrenBelow(child);
		}
		
	}

	public boolean hasPositions(DrawStyle style) {
		for(Extent e: getAllExtents()) {
			if(e.getPos(style)== null ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Called when dragging an irreducible node
	 */
	public void setAdditiveXForExt(Extent dragged, int xValue, int yValue, DrawStyle style) {
		if(dragged.isIrreducible() || dragged.isTopExtent()) { //Only irreducible Extents and the top element are draggable
			
			int xDiff = xValue-dragged.getPos(style).getX() ; //pixels that the dragged Extent is being dragged
			int yDiff = yValue-dragged.getPos(style).getY() ; //pixels that the dragged Extent is being dragged
			
			if(style == DrawStyle.ADDITIVE) { //additive mode allows to drag the y coordinate
				dragged.setPos(new Position(xValue, yValue), style);
			}else {
				dragged.setPos(new Position(xValue, dragged.getPos(style).getY()), style);
				yDiff = 0;
			}
			
			Set<Extent> successors = new HashSet<>(dragged.getSuccessors());
			for(int layer = dragged.getLayer()+1; layer < getNumberOfLayers(); layer ++) {
				Set<Extent> newSuccessors = new HashSet<>();
				for(Extent suc : successors) {
					if(suc.getLayer()==layer) {
						int x;
						if(dragged.isTopExtent()) {
							x = suc.getPos(style).getX()+xDiff;
							
							suc.setPos(new Position(x, suc.getPos(style).getY()+yDiff), style);
						}else {
							
							switch(style) {
							case ADDITIVE:
								Position init = new Position(getTopElement(getAllExtents()).getPos(style).getX(),getTopElement(getAllExtents()).getPos(style).getY());
								for(Extent irr: suc.getIrreduciblePredecessors3()) {
									init.add(irr.getVector(style));
								}
								if(suc.isIrreducible()) {
									init.setX(suc.getPos(DrawStyle.ADDITIVE).getX()+xDiff);
									init.setY(suc.getPos(DrawStyle.ADDITIVE).getY()+yDiff);
								}
								suc.setPos(init, style);
								break;
							case CASCADING:
								if(!suc.isIrreducible()) {
									//search for highest ancestor that the predecessors share
									//Extent samePredecessor = findClosestSharedPredecessor(suc);
									Extent samePredecessor = getTopElement();
									suc.setAdditiveLogXBasedOnPredecessor(samePredecessor, style);//also sets positions of irreducible successors 
								}
								break;
							default:
								x = getPositionBasedOnIrreduciblePredecessorsFor(suc, style);
								suc.setPos(new Position(x, suc.getPos(style).getY()), style);
								break;							
							}
						}								
						newSuccessors.addAll(suc.getSuccessors());						
					}
				}
				successors.addAll(newSuccessors);
			}
		}
	}
	
	public Extent getBottomElement() {
		Extent toReturn = null;
		for(Extent e: lattice.get(getNumberOfLayers()-1)) {
			if(toReturn == null || toReturn.getNumberOfObjects()>e.getNumberOfObjects()) {
				toReturn = e;
			}
		}
		return toReturn;
	}
	
	public Extent getTopElement() {
		Extent toReturn = null;
		for(Extent e: lattice.get(0)) {
			if(toReturn == null || toReturn.getNumberOfObjects()>e.getNumberOfObjects()) {
				toReturn = e;
			}
		}
		return toReturn;
	}
}
