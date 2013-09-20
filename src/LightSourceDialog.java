import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LightSourceDialog {

	private int WIDTH = 350, HEIGHT = 250;
	JTextField[] fields = new JTextField[7];
	private String[] vals = new String[7];
	JFrame frame = new JFrame();
	Display display;
	int index;

	public LightSourceDialog(Display dis, int n, String[] defaults) {
		index = n;
		display = dis;
		JPanel panel = new JPanel();
		JPanel row = new JPanel();

		frame.add(panel);
		frame.setResizable(false);
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.setLocationRelativeTo(dis);
		frame.setTitle("Add Light Source");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		panel.setLayout(new GridLayout(5, 1));
		panel.add(new JLabel("Chooese a color for the light source (R,G,B):"));
		row.setLayout(new GridLayout(1, 3));
		panel.add(row);
		for (int i = 0; i < 3; i++) {
			row.add(fields[i] = new JTextField(defaults[i]));
		}
		panel.add(new JLabel("Chooese a direction vector for the light source (x,y,z):"));
		row = new JPanel();
		row.setLayout(new GridLayout(1, 3));
		panel.add(row);
		for (int i = 3; i < 6; i++) {
			row.add(fields[i] = new JTextField(defaults[i]));
		}
		JButton button = new JButton("Done");
		
		row = new JPanel();
		panel.add(row);
		row.setLayout(new GridLayout(1, 3));
		row.add(new JLabel("Intensity:"));
		row.add(fields[6] = new JTextField(defaults[6]));
		row.add(button);
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				lightSource();
				frame.dispose();
			}
		});

		frame.pack();
		frame.setVisible(true);
	}

	public void lightSource() {
		for (int i = 0; i < 7; i++) {
			vals[i] = fields[i].getText();
		}
		if(index<= 0)
		display.newLightSource(vals);
		else
			display.changeLightSource(vals, index-1);
	}	
}
