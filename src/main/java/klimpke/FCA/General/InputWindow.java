package klimpke.FCA.General;

/**
 * 
 * @author Klimpke
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import klimpke.FCA.FormalContext.FormalContext;
import klimpke.FCA.Services.CSVManager;


public class InputWindow 
{

	JFrame frame;
	
	private JButton objectP;
	private JButton attributeP;
	private JButton btnAnalysis;
	
	private JTable crossTable;
	
	DefaultTableModel table;
	

	/**
	 * Create the application.
	 */
	public InputWindow() {
		initialize();
	}

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
				
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//BUTTONS ----------------------------------------------------------------------------
		objectP = new JButton("Add Object");
		objectP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addObject();
			}
		});
		objectP.setFont(new Font(objectP.getFont().getName(),objectP.getFont().getStyle(),14));
		objectP.setMargin(new Insets(1,1,1,1));
		
		attributeP = new JButton("Add Attribute");
		attributeP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addAttribute();
			}
		});
		
		attributeP.setFont(new Font(attributeP.getFont().getName(),attributeP.getFont().getStyle(),14));
		attributeP.setMargin(new Insets(1,1,1,1));
		
		
		btnAnalysis = new JButton("Create Lattice");
		btnAnalysis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FormalContext c = new FormalContext(table);
				try {
					ConceptLattice lattice = c.getConceptLattice();

					OutputWindow results = new OutputWindow(table, lattice);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		//TABLE -------------------------------------------------------------------------
		table = initializeTable(7);
		
        table.addRow(new Object[]{"Mult","", "a", "b", "c", "d", "e"});
        table.addRow(new Object[]{"1", "T1", "", "X", "", "X", ""});
        table.addRow(new Object[]{"1", "T2", "", "X", "", "", "X"});
        table.addRow(new Object[]{"1", "T3", "", "", "X", "", ""});
        table.addRow(new Object[]{"1", "T4", "X", "X", "X", "", ""});
        table.addRow(new Object[]{"1", "T5", "", "", "", "X", ""});
        table.addRow(new Object[]{"1", "T6", "", "X", "X", "", ""});
        table.addRow(new Object[]{"1", "T7", "", "", "", "", "X"});

		crossTable = new JTable(table);
        crossTable.setTableHeader(null);
       
        
        crossTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
        	
		    @Override
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		    {
		    	this.setHorizontalAlignment(JLabel.CENTER);
		        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		        c.setBackground(row == 0 || column == 1? Color.LIGHT_GRAY : Color.WHITE);
		        c.setBackground(row != 0 && row % 2 == 0 && column != 1? new Color(224,224,224): c.getBackground());
		        c.setBackground(column == 1 && row != 0 && row % 2 != 0 ? new Color(224,224,224): c.getBackground());
		        
		        c.setFont(row == 0 || column == 1 ? new Font("Serif",Font.BOLD,12): c.getFont());
		        
		        c.setForeground(row == 0 && column >1? Color.RED : Color.BLACK);
		        c.setForeground(column == 1? Color.BLUE: c.getForeground());
		        
		      //Ensure that the multiplication value is a positive integer
		        if(row > 0 && column == 0) {
		        	String cellValue = (String) value;
		        	try {
		        		int cellValueInt = Integer.valueOf(cellValue);
		        		if(cellValueInt < 0)
		        			setValue(1);
		        	}catch(Exception e) {
		        		setValue(1);
		        	}
		        }
		        
		        if(row!=0 && column >1) {
		        	if(value.toString().contains("X")) { //bright                   //dark
		        		c.setBackground(row%2 == 0? new Color(184,204,183): new Color(198,219,197));
		        		
		        		crossTable.setValueAt("X", row, column);
		        	}else {
		        		c.setBackground(row%2 == 0? new Color(204,183,186): new Color(219,197,200));
		        		crossTable.setValueAt("", row, column);
		        	}
		        }
		        return c;
		    }		    
		});
        
        crossTable.addMouseListener(new java.awt.event.MouseAdapter(){
        	public void mouseClicked(java.awt.event.MouseEvent e){

        		int row = crossTable.rowAtPoint(e.getPoint());

        		int col = crossTable.columnAtPoint(e.getPoint());
        		if(row > 0 && col > 1) {
        			if(table.getValueAt(row, col)=="X") {
        				table.setValueAt("", row, col);
        				
        			}else {
        				table.setValueAt("X", row, col);
        			}	
        		}	
        	}    	
        });

        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteRow = new JMenuItem("Delete last object");
        deleteRow.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	if(table.getRowCount()>2)
            		table.setRowCount(table.getRowCount()-1);
            }
        });
        
        popupMenu.add(deleteRow);
        JMenuItem deleteColumn = new JMenuItem("Delete last attribute");
        deleteColumn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	if(table.getColumnCount()>3)
            		table.setColumnCount(table.getColumnCount()-1);
            }
        });
        popupMenu.add(deleteColumn);
        crossTable.setComponentPopupMenu(popupMenu);

		final JScrollPane scrollPane = new JScrollPane(crossTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		//------------------------MENU BAR----------------------------------------------------------
		
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("Content");
		
		JMenuItem menuItem = new JMenuItem("New",KeyEvent.VK_N);
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                table.setColumnCount(4);
                table.setRowCount(0);
                table.addRow(new Object[]{"Mult","", "m", "w"});
                table.addRow(new Object[]{"1", "Adam", "X", ""});
                table.addRow(new Object[]{"1", "Eve", "", "X"});
                
            }
        });
        menu.add(menuItem);
		
        JMenuItem menuItem2 = new JMenuItem("Load CSV file",KeyEvent.VK_N);
        menuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                	JFileChooser fileChooser = new JFileChooser();
					fileChooser.setDialogTitle("");   
					 
					int userSelection = fileChooser.showOpenDialog(frame);
					 
					if (userSelection == JFileChooser.APPROVE_OPTION) {
					    File fileToLoad = fileChooser.getSelectedFile();
					    System.out.println("Load file: " + fileToLoad.getAbsolutePath());
					    DefaultTableModel newTable = CSVManager.loadTableFromCSV(fileToLoad);
						table.setRowCount(newTable.getRowCount());
						table.setColumnCount(newTable.getColumnCount());
						for(int row = 0; row< newTable.getRowCount(); row++) {
							for(int column = 0; column< newTable.getColumnCount(); column++) {
								table.setValueAt(newTable.getValueAt(row, column), row, column);
							}
						}
					}
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
            }
        });
        menu.add(menuItem2);
        
		JMenuItem menuItem3 = new JMenuItem("Save CSV",KeyEvent.VK_N);
        menuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Specify a file to save");   
				 
				int userSelection = fileChooser.showSaveDialog(frame);
				 
				if (userSelection == JFileChooser.APPROVE_OPTION) {
				    File file = fileChooser.getSelectedFile();
				    System.out.println("Save as file: " + file.getAbsolutePath());
				    
				    CSVManager.saveTableToCSV(file,table);
				}
            }
        });
        menu.add(menuItem3);
        
        //example based on https://de.statista.com/statistik/daten/studie/1825/umfrage/koerpergroesse-nach-geschlecht/
        JMenuItem menuItem4 = new JMenuItem("Load gender-size-distribution example",KeyEvent.VK_N);
        menuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	table.setColumnCount(6);
                table.setRowCount(0);
                table.addRow(new Object[]{"Mult","", "m", "f","<175",">=175"});
                table.addRow(new Object[]{"31","M1", "X", "","X",""});
                table.addRow(new Object[]{"69","M2", "X", "","","X"});
                table.addRow(new Object[]{"91","F1", "", "X","X",""});
                table.addRow(new Object[]{"9","F2", "", "X","","X"});
            }
        });
        menu.add(menuItem4);
        
        //example from:
        //Bernhard Ganter, Rudolf Wille: Formal Concept Analysis - Mathematical Foundations. 
        //Springer 1999, page 76
        JMenuItem menuItem5 = new JMenuItem("Stream example",KeyEvent.VK_N);
        menuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                table.setColumnCount(8);
                table.setRowCount(0);
                table.addRow(new Object[]{"Mult","", "large", "small", "flowing", "stagnant", "artificial", "natural"});
                table.addRow(new Object[]{"1","river", "X", "","X", "","" , "X"});
                table.addRow(new Object[]{"1","brooks", "", "X", "X", "","" , "X"});
                table.addRow(new Object[]{"1","canal", "X", "", "X", "", "X",""});
                table.addRow(new Object[]{"1","ditch", "", "X", "X", "", "X",""});
                table.addRow(new Object[]{"1","lake", "X", "", "", "X", "", "X"});
                table.addRow(new Object[]{"1","slough", "", "X", "", "X", "", "X"});
                table.addRow(new Object[]{"1","pond", "X", "", "", "X", "X",""}); 
                table.addRow(new Object[]{"1","basin", "","X", "", "X", "X", ""});
            }
        });
        menu.add(menuItem5);
        
        
        menubar.add(menu);

        JMenu menu2 = new JMenu("Help");
        
        JMenuItem menu2Item1 = new JMenuItem("Help",KeyEvent.VK_N);
        menu2Item1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	String content = "Table View: \n Deleting rows/columns: right klick on table and choose desired option\n\n";
            	content += "Lattice View:\n View node informations: right klick on node"
            			+ "\n Change canvas size: pull the edges of the frame"
            			+ "\n Reinitialize lattice to fill the canvas: double click on canvas and choose 'YES'"
            			+ "\n additive dragging mode: directly manipulate x-position of the top node and its direct descendants - additive rule applies"
            			+ "\n free dragging mode: directly manipulate x-position of every node without further changes (not available in cascading view)";

                JOptionPane pane = new JOptionPane(content,JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);


                JDialog dialog = pane.createDialog((JFrame)null,"Help");

                dialog.setLocation(200,200);
                dialog.setVisible(true);
            }
        });
        menu2.add(menu2Item1);
        
        JMenuItem menu2Item2 = new JMenuItem("About",KeyEvent.VK_N);
        menu2Item2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	String content = "Created by J. Klimpke\n 2020 \n TU Dresden";

                JOptionPane pane = new JOptionPane(content,JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
                JDialog dialog = pane.createDialog((JFrame)null,"About");

                dialog.setLocation(200,200);
                dialog.setVisible(true);
            }
        });
        menu2.add(menu2Item2);
        menubar.add(menu2);
        
        //Layout
        
        JPanel leftButtonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftButtonRow.add(btnAnalysis);
        
        JPanel rightButtonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtonRow.add(objectP);
        rightButtonRow.add(attributeP);
        
        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.add(leftButtonRow);
        buttonRow.add(rightButtonRow);
        
        JPanel scrollPanePanel = new JPanel();
        
        crossTable.setBorder(new LineBorder(Color.BLACK));
        scrollPane.setBorder(new EmptyBorder(0,10,5,10));
        
        scrollPanePanel.setLayout(new BorderLayout());
        scrollPanePanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		frame.setJMenuBar(menubar);
		
		contentPane.add(Box.createVerticalStrut(5));
		contentPane.add(buttonRow);
		contentPane.add(Box.createVerticalStrut(5));
		contentPane.add(scrollPanePanel);
		
		frame.setContentPane(contentPane);
		frame.setMinimumSize(new Dimension(450,150));
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}



	private DefaultTableModel initializeTable(int columnCount) {
		DefaultTableModel toReturn = new DefaultTableModel() {
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return (row == 0 && column > 1)|| (row != 0 && column <= 1);
		    }
			
		};
		
		for(int i = 0; i<columnCount; i++) {
			toReturn.addColumn("");	
		}
		return toReturn;
	}

	private void addAttribute() {
		Vector data = new Vector();
		data.add(0, "Attribut");
		for(int i = 1; i< table.getRowCount();i++) {
			data.add(i,"");
		}
		table.addColumn("Attribut",data);
		
		table.fireTableRowsInserted(0, table.getRowCount()-1);
	}

	private void addObject() {
		Vector data = new Vector();
		data.add(0, "1");
		data.add(1,"T"+table.getRowCount());
		for(int i = 2; i< table.getColumnCount();i++) {
			data.add(i,"");
		}
		table.addRow(data);
	}
}
