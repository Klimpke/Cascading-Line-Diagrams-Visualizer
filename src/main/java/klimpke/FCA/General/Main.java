package klimpke.FCA.General;

import java.awt.EventQueue;

/**
 * 
 * @author Klimpke
 *
 */
public class Main {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InputWindow window = new InputWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
