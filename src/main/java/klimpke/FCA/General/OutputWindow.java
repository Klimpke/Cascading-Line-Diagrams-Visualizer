package klimpke.FCA.General;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import klimpke.FCA.FormalContext.Extent;

/**
 * 
 * @author Klimpke
 *
 */
public class OutputWindow extends JTabbedPane{

	private int drawWidth;
	private int drawHeight;
	private int circleWidth = 10;

	private Extent dragged = null;
	private ConceptLattice lattice;
	private int freeHeight = 30;
	boolean freeDraggingMode = false;
	
	public OutputWindow(DefaultTableModel table, ConceptLattice l) {

		this.lattice = l;
		
		drawWidth = lattice.getDrawWidth() >500? lattice.getDrawWidth(): 500; //width should not be too small
		drawHeight = lattice.getDrawHeight();
		
		
		JTabbedPane tp = new JTabbedPane();
		tp.setBounds(0, freeHeight, drawWidth, drawHeight);

		JFrame f = new JFrame();
		f.setMinimumSize(new Dimension(300,300));
		tp.getToolkit().setDynamicLayout(false);
		
		JLabel label = new JLabel("Dragging Mode:");
		label.setBounds(10, 0, 150, 30);
		f.add(label);
		
		//Radio Buttons
		
		JRadioButton freeRB = new JRadioButton("free");
		JRadioButton addRB = new JRadioButton("additive");
		addRB.setSelected(true);
		ButtonGroup radioB = new ButtonGroup();
		radioB.add(freeRB);
		radioB.add(addRB);
		freeRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				freeDraggingMode = true;
			}
			
		});
		addRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				freeDraggingMode = false;
			}
			
		});
		freeRB.setBounds(130, 0, 60, 30);
		addRB.setBounds(190,0,90,30);
		f.add(freeRB);
		f.add(addRB);
		
		f.addComponentListener(new ComponentAdapter() {

		    @Override
		    public void componentResized(ComponentEvent e) {
		    	drawWidth = f.getWidth();		        
		        drawHeight = f.getHeight()-freeHeight;
		        tp.setBounds(0, freeHeight, f.getWidth(), drawHeight);
		    }

		});
		
		JPanel p1 = new JPanel() {
			@Override
	        public void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            paintLattice(g,lattice,DrawStyle.ADDITIVE);
	        }
		};
		p1.addMouseListener( getMouseAdapter(DrawStyle.ADDITIVE, p1.getGraphics(), f, p1) );
        p1.addMouseMotionListener(getMouseMotionAdapter(DrawStyle.ADDITIVE, p1));
        
		tp.add("Additive LD", p1);
		
		JPanel p2 = new JPanel() {
			@Override
	        public void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            paintLattice(g,lattice,DrawStyle.CASCADING);
	            if(dragged != null && !dragged.isIrreducible() && !dragged.isTopExtent()) {

					//Draw red parallelogram 
	            	if(dragged.getPredecessors().size()==2) {
		            	drawRedParallelogram(dragged, g);	
	            	}	            
	            }
			}

			private void drawRedParallelogram(Extent dragged, Graphics g) {

				Extent samePredecessor = lattice.getTopElement();
            	//Extent samePredecessor = lattice.findClosestSharedPredecessor(dragged);
				Position predictedPos = new Position(samePredecessor.getPos(DrawStyle.CASCADING).getX(),samePredecessor.getPos(DrawStyle.CASCADING).getY());
				for(Extent pre: dragged.getPredecessors()) {
					predictedPos.add(pre.getPos(DrawStyle.CASCADING).minus(samePredecessor.getPos(DrawStyle.CASCADING)));
				}
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(Color.RED);
		        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0);
		        g2d.setStroke(dashed);
		        g2d.drawLine(samePredecessor.getPos(DrawStyle.CASCADING).getX(), samePredecessor.getPos(DrawStyle.CASCADING).getY(),
						predictedPos.getX(), predictedPos.getY());
				for(Extent pre: dragged.getPredecessors()) {
					g2d.drawLine(pre.getPos(DrawStyle.CASCADING).getX(), pre.getPos(DrawStyle.CASCADING).getY(), 
							predictedPos.getX(), predictedPos.getY());

					g2d.drawLine(pre.getPos(DrawStyle.CASCADING).getX(), pre.getPos(DrawStyle.CASCADING).getY(), 
							samePredecessor.getPos(DrawStyle.CASCADING).getX(), samePredecessor.getPos(DrawStyle.CASCADING).getY());
				}
				g2d.dispose();
				

			}
		};
		p2.addMouseListener( getMouseAdapter(DrawStyle.CASCADING, p2.getGraphics(), f, p2) );
        p2.addMouseMotionListener(getMouseMotionAdapter(DrawStyle.CASCADING, p2));
        
		tp.add("Cascading LD", p2);
		
		
		f.add(tp);
		f.setSize(drawWidth, drawHeight+freeHeight);
		f.setLayout(null);
		f.setVisible(true);
	}

	
	protected void askReinitializeQuestion(JFrame f, JPanel p) {
		drawWidth = f.getWidth();
		drawHeight = f.getHeight()-freeHeight;
		Object[] options = {"Yes","No"};
		
		if(JOptionPane.showOptionDialog(f, "Do you want to reinitialize the lattice?", "Resizing", 
									JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
									null, options, options[0])== 0) {
	    
			lattice.initializePositions(drawHeight, drawWidth);
			p.setBounds(0, freeHeight, drawWidth, drawHeight);
		}

		
	}


	private MouseMotionListener getMouseMotionAdapter(DrawStyle style, JPanel p) {
		return new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
            	if(dragged == null) {
					dragged = findClosestExtent(e.getComponent().getMousePosition(), style);
					dragged.setDragging(true);
				}
            	
            	Point mousePos = e.getComponent().getMousePosition();

        		if(mousePos!=null) {
            		if(freeDraggingMode && style == DrawStyle.ADDITIVE) {
						//dragged.setPos(new Position((int)mousePos.getX(), dragged.getPos(style).getY()), style);
						dragged.setPos(new Position((int)mousePos.getX(), (int)mousePos.getY()), style);
	            	}else {
						lattice.setAdditiveXForExt(dragged, (int)mousePos.getX(), (int)mousePos.getY(),style);
					}
            	}

        		p.repaint();
				
            }
        };
	}


	private MouseListener getMouseAdapter(DrawStyle style, Graphics g, JFrame f, JPanel p) {
		return new MouseAdapter(){
			 @Override
			 public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) {
					askReinitializeQuestion(f, p);
					p.repaint();
				}
				if(SwingUtilities.isRightMouseButton(e)) {
					Extent selected = findClosestExtent(e.getPoint(), style);
					String content = selected.toExtentString()+"\n"+
									 selected.toIntentString()+"\n"+
									 "Weight: "+selected.getWeight()+"\n"+
									 "Support: "+String.format("%.2f",selected.getWeight()/ConceptLattice.getTotalWeight(false)).replace(',', '.')+"\n"+
									 "Log2(Support): "+String.format("%.2f",selected.getLogWeight()).replace(',', '.');
					
					JOptionPane pane = new JOptionPane(content,JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
					
					
					JDialog dialog = pane.createDialog((JFrame)null,"Concept Information");
					
					dialog.setLocation(e.getXOnScreen(),e.getYOnScreen());
					dialog.setVisible(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent evt) {
				Extent dragged2 = findClosestExtent(evt.getComponent().getMousePosition(), style);

				if((style == DrawStyle.CASCADING) && dragged != null && 
						!dragged.isIrreducible() && !dragged2.isIrreducible() 
						&& !dragged.isTopExtent() && !dragged2.isTopExtent() 
						&& dragged.equals(dragged2)) {
						
					dragged.setDragging(false);
						dragged = null;
				}else {
					if(dragged!=null)
						dragged.setDragging(false);
					
					dragged = dragged2;
					dragged.setDragging(true);
					
				}
				p.repaint();
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		};
	}

	/**
	 * Returns the extent that is closest to the given Position.
	 */
	private Extent findClosestExtent(Point mousePosition, DrawStyle style) {
		Extent closestExtent = null;
		double bestDist = Double.MAX_VALUE;
		for(Extent e: lattice.getAllExtents()) {
			double dist = Math.sqrt(Math.pow(mousePosition.getX()-e.getPos(style).getX(), 2)+
									Math.pow(mousePosition.getY()-e.getPos(style).getY(), 2));
			if(dist< bestDist) {
				bestDist = dist;
				closestExtent = e;
			}
		}		
		return closestExtent;
	}

	/**
	 * Basic draw method for different draw styles
	 */
	protected void paintLattice(Graphics g, ConceptLattice lattice, DrawStyle style) {
		paintWhiteBackground(g);
		
		if(!lattice.hasPositions(style)) {
			lattice.initializePositions(drawHeight, drawWidth);
		}
		
		//Draw Lines to predecessors
	    for(Extent e :lattice.getAllExtents()) {
	    	
	    	Position pos = e.getPos(style);
	    	for(Extent predecessor: e.getPredecessors()) {
	    		Position pre = predecessor.getPos(style);
	    		if(pos.getY() != -1 && pre.getY() != -1) {
	    			g.drawLine(pos.getX(), pos.getY(), pre.getX(), pre.getY());
	    		}
	       	}
	    }
	    

	    //Draw fading line to bottom element that has weight of 0 in logarithmic scaling (-> y = - infinite)
	    if(style == DrawStyle.CASCADING) {
	    	Extent bottom = lattice.getBottomElement();
	    	if(bottom.getWeight()==0) { //y = - inf -> bottom not displayed
	    		for(Extent e: bottom.getPredecessors()) {
	    			Position startPos = e.getPos(style);
	    			
	    			for(int c = 0; c < 25; c++) {
	    				g.setColor(new Color(c*10,c*10,c*10));
	    				g.drawLine(startPos.getX(), startPos.getY()+c, startPos.getX(), startPos.getY()+c+1);
	    			}
	    			g.setColor(Color.BLACK);
	    		}
	    	}
	    }
	    
	    //Draw Circles and Text
	    g.setFont( new Font("Serif",Font.BOLD,12));
	    
	    for(Extent e: lattice.getAllExtents()) {
	    	Position pos = e.getPos(style);

	    	if(pos.getY()!= -1) {
	    		if(e.isIrreducible()) {
	    			g.setColor(Color.GREEN);
	    			g.fillRect(pos.getX()-circleWidth/2, pos.getY()-circleWidth/2, circleWidth, circleWidth);
	    			g.setColor(Color.BLACK);
	    		}else {
	    			g.fillOval(pos.getX()-circleWidth/2, pos.getY()-circleWidth/2, circleWidth, circleWidth);
		    	    g.setColor(Color.BLACK);
	    		}
	    		
	    	    List<String> attributeLabel = e.getAttributeLabel();
	    	    List<String> objectLabel = e.getObjectLabel();
	    	    
	    	    if(attributeLabel.size()!=0) {
	    	    	g.setColor(Color.RED);
		    		drawLabel(g, attributeLabel, pos.getX()+10, pos.getY()-5, -20);
		    		g.setColor(Color.BLACK);
	    	    }
	    	    if(objectLabel.size()!=0) {
	    	    	g.setColor(Color.BLUE);
	    			drawLabel(g, e.objectLabel(), pos.getX()+10, pos.getY()+15, 20);
	    			g.setColor(Color.BLACK);
	    	    }
	    	}	    	
	    }
	    
	    
	    
	    
	    //Draw lines that explain the y-position of each point
	    if(style == DrawStyle.LOGARITHMIC || style == DrawStyle.CASCADING) {

	    	List<Integer> drawnPositions = new ArrayList<>(); //remembers at which y positions scales have been drawn
	    	
	    	Extent biggest = null;
		    for(Extent e: lattice.getAllExtents()) {
		    	biggest = biggest == null || biggest.getWeight(style) < e.getWeight(style)? e : biggest;
		    }

	    	int highestWeight = biggest.getWeight();
		    drawScaling(g, biggest.getPos(style).getY(),highestWeight,highestWeight,style); //Scale of highest value
		    
		    drawnPositions.add(biggest.getPos(style).getY()); //highest scale value
		    

		    Extent bottom = lattice.getBottomElement();
		    drawnPositions.add(bottom.getPos(style).getY()); //lowest scale value
		    
		    g.drawString("supp(X) | log2(supp(X)) | wgt(X)", 0, 15);
		    
		    //scale for lowest value
		    if(bottom.getWeight()!=0) {
		    	drawScaling(g, bottom.getPos(style).getY(), bottom.getWeight(),highestWeight, style);
		    }
		    
		    for(Extent e: lattice.getAllExtents()) {
		    	boolean toClose = false;
		    	int drawY = e.getPos(style).getY();
		    	if(drawY == -1) {
		    		continue;
		    	}
		    	
		    	for(Integer alreadyDraw: drawnPositions) {
		    		if(Math.abs(alreadyDraw-drawY) < 50) {
		    			toClose = true;
		    		}
		    	}
		    	if(!toClose) {
		    		drawScaling(g, drawY,e.getWeight(),highestWeight, style);
		    		drawnPositions.add(drawY);
		    	}
		    }
	    }
	    
	}


	/**
	 * For drawing the labels beside the circle of each extent.
	 * @param startX x value where the first String shall be printed 
	 * @param startY y value where the first String shall be printed
	 * @param diffY Changes the y coordinate for the second and every following string
	 */
	private void drawLabel(Graphics g, List<String> labels, int startX, int startY, int diffY) {
		int i = 0;
		for(String label: labels) {
			g.drawString(label, startX, startY + i*diffY);
			i++;
		}
	}

	/**
	 * Draw a line at height y and write the value of weight as explanation above the line.
	 * @param highestWeight 
	 * @param style 
	 */
	private void drawScaling(Graphics g, int y, double weight, int highestWeight, DrawStyle style) {
		Graphics2D g2d = (Graphics2D) g.create();

        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
        g2d.setStroke(dashed);
        g2d.drawLine(0, y, 100, y);
        

        String toDraw = String.format("%.0f",weight);
        
        
        if(style == DrawStyle.CASCADING) {
			toDraw = String.format("%.2f", Math.round(weight*100/highestWeight)/100.0) + " | "+
					String.format("%.2f", Math.log(weight/highestWeight)/Math.log(2.0))+ " | "+
					String.format("%.0f", weight);
			toDraw = toDraw.replace(',', '.');
        	
        }
        
        g.drawString(toDraw, 0, y-10);

        g2d.dispose();
	}
	
	
	public void paintWhiteBackground(Graphics g) {
		g.setColor(Color.WHITE);
        g.fillRect(0, 0, drawWidth, drawHeight);
        g.setColor(Color.BLACK);
	}
}