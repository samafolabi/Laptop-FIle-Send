import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class GUIMain extends JFrame {
	
	final JFileChooser fc = new JFileChooser();	
	JButton openBut;
	static JButton saveBut;
	JTextField addrField;
	static JLabel jLabel;
	
	static String[] files;
	
	static Notification notification = new Notification() {
		
		@Override
		public void sent(String res) {
			// TODO Auto-generated method stub
			jLabel.setText(res);
		}
		
		@Override
		public void received(String res, String[] fileNames) {
			// TODO Auto-generated method stub
			jLabel.setText(res);
			files = fileNames;
			saveBut.doClick();
		}
	};
	
	public GUIMain() {
		createUserInterface();
	}

	private void createUserInterface() {
		// TODO Auto-generated method stub
		Container contentPane = getContentPane();
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(null);
		
		openBut = new JButton();
		openBut.setBounds(40, 150, 90, 20);
		openBut.setFont(new Font("Default", Font.PLAIN, 12));
		openBut.setText("Open File");
		openBut.setForeground(Color.BLACK);
		openBut.setBackground(Color.WHITE);
		openBut.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(openBut);
		openBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				openActionPerformed(event);				
			}
		});
		
		saveBut = new JButton();
		saveBut.setBounds(140, 150, 90, 20);
		saveBut.setFont(new Font("Default", Font.PLAIN, 12));
		saveBut.setText("Save File");
		saveBut.setForeground(Color.BLACK);
		saveBut.setBackground(Color.WHITE);
		saveBut.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(saveBut);
		saveBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				saveActionPerformed(event);				
			}
		});
		
		addrField = new JTextField();
		addrField.setBounds(180, 47, 100, 20);
		addrField.setFont(new Font("Default", Font.PLAIN, 12));
		addrField.setHorizontalAlignment(JTextField.CENTER);
		addrField.setForeground(Color.BLACK);
		addrField.setBackground(Color.WHITE);
		contentPane.add(addrField);
		
		jLabel = new JLabel();
		jLabel.setBounds(30, 300, 100, 20);
		jLabel.setFont(new Font("Default", Font.PLAIN, 12));
		jLabel.setText("");
		jLabel.setForeground(Color.WHITE);
		contentPane.add(jLabel);
		
		setSize(400, 400);
		setTitle("Laptop File Send");
		setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GUIMain app = new GUIMain();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ReceiveServer rServer = new ReceiveServer(notification);
	}
	
	private void openActionPerformed (ActionEvent e) {
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(GUIMain.this);
		try {
			InetAddress addr = InetAddress.getByName(addrField.getText());//InetAddress.getLocalHost().getHostName();
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File[] files = fc.getSelectedFiles();
	            SendClient client = new SendClient(files, 
	            addr, notification);
	        }
		} catch (Exception e1) {
			// TODO: handle exception
			jLabel.setText("Error: " + e1.toString());
		}
	}
	
	private void saveActionPerformed (ActionEvent e) {
		for (int i = 0; i < files.length; i++) {
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showSaveDialog(GUIMain.this);
			try {
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            Files.move(Paths.get(System.getProperty("user.dir")+"/"+files[i]),
		            		Paths.get(fc.getSelectedFile()+"/"+files[i]));
		            //Diff between files, large files, list ip addresses, show which
		            //ip address sent, show name of file to save on file chooser,
		            //remove save file, show error problems, clean up code, save to 
		            //previous directory, save a directory, daemon
		        }
			} catch (Exception e1) {
				// TODO: handle exception
				jLabel.setText("Error: " + e1.toString());
			}
		}
	}

}
