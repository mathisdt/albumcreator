package org.zephyrsoft.albumcreator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.UIManager;

/**
 * start-up class for this application
 * 
 * @author Mathis Dirksen-Thedens
 */
public class Start {
	
	public static void main(String[] args) {
		
		Service service = new Service();
		
		// set look-and-feel (temporarily redirect error stream so the console is not polluted)
		PrintStream originalErrorStream = System.err;
		System.setErr(new PrintStream(new ByteArrayOutputStream()));
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (Exception e) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ee) {
				// set no specific look-and-feel as it keeps on making problems
			}
		}
		System.setErr(originalErrorStream);
		
		GUI gui = new GUI(service);
		gui.setVisible(true);
		
	}
	
}
