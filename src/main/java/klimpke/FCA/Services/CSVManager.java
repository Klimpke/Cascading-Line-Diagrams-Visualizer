package klimpke.FCA.Services;

/**
 * 
 * @author Klimpke
 *
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

import javax.swing.table.DefaultTableModel;

public class CSVManager {

	public static void saveTableToCSV(File file, DefaultTableModel table) {
		try {
			FileWriter writer = new FileWriter(file);
			for(int row = 0; row < table.getRowCount(); row++) {
				String line = "";
				for(int column = 0; column < table.getColumnCount(); column ++) {
					line += line == ""? String.valueOf(table.getValueAt(row, column)): ", "+String.valueOf(table.getValueAt(row, column)).trim();
				}
				line += row == table.getRowCount()-1? "": "\n";
				writer.append(line);
			}
			writer.flush();
		}catch(Exception e) {
		}		
	}

	public static DefaultTableModel loadTableFromCSV(File file) throws FileNotFoundException {
		DefaultTableModel table = new DefaultTableModel();
		Scanner scanner = new Scanner(file);
		while(scanner.hasNext()) {
			String line = scanner.nextLine();
			String[] split = line.split(",");
			
			if(table.getRowCount()==0) {
				for(int i = 0; i< split.length;i++)
				table.addColumn("");
			}
			table.addRow(split);
		}		
		return table;
	}

}
