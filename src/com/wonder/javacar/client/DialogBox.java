package com.wonder.javacar.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DialogBox extends JFrame implements ActionListener {

	private JPanel panel;
	private JFileChooser fileChooser;
	private File capFile;

	public DialogBox() {
		super();
		setSize(680, 680);
		this.setLocationRelativeTo(null);
		this.setTitle("BioCard Edition");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(getPanel());
	}

	private JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel();
			this.panel.add(getFileChooser());
		}
		return this.panel;
	}

	private JFileChooser getFileChooser() {
		if (this.fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("CAP Files", "cap");
			fileChooser.setFileFilter(filter);
		}

		return this.fileChooser;
	}

	public File getCapFile() {
		return capFile;
	}

	public void setCapFile(File capFile) {
		this.capFile = capFile;
	}

	/**
	 * @param fileChooser the fileChooser to set
	 */
	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// CAP File
				FileOutputStream cap = null;
				DialogBox dialogBox = new DialogBox();
				//dialogBox.setVisible(true);
				
				int choiceReturn = dialogBox.getFileChooser().showOpenDialog(dialogBox);
				if (choiceReturn == JFileChooser.APPROVE_OPTION) {
					dialogBox.setCapFile(dialogBox.getFileChooser().getSelectedFile());
					try {
						cap = new FileOutputStream(dialogBox.getCapFile());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				if(cap != null)
					System.out.println(cap.toString());
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Clik on");
	}
}
