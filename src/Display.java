import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;

/*
 * 3D Renderer
 * 
 * Author: Thomas Auberson
 * Version: 0.18
 * 
 * This class handles the GUI
 * It controls a JFrame window with a single JPanel canvas display.  
 * Extracted from Template Library v0.12
 */

public class Display extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	// FIELDS
	private String TITLE = "3D Renderer";
	private String VERSION = "0.18"; // *RREQUIRES DEFINITION
	private String AUTHOR = "Thomas Auberson";
	private String DESCRIPTION = "<br><br>COMP261 Assignment #3<br>A 3D renderer that uses a Z-buffer to render 3D models stored<br> as triangular polygons."; // *RREQUIRES
																																								// DEFINITION
	private int SIZE_X = 706, SIZE_Y = 751;
	private boolean RESIZABLE = false;

	private Thread thread = new Thread(this);
	private JFrame frame;
	private MenuBar menu;

	private Model model;
	private int numLights = 0;
	private final String[] NEW_LIGHT_DEFAULTS = {"255","255","255","1.0","1.0","1.0","0.5"};

	public static double ambientIntensity = 0.3;
	public static boolean lightRotation = false; // Light sources rotate with
													// image

	private Point lastMousePoint;
	private boolean ctrl = false;

	// NOTE: Size of Frame:
	// Edges (Width) = 6; Edges (Height) = 28; Menubar (Height) = 23
	// ==> Frame (x,y) = JPanel(x)+6, JPanel(y)+51

	// CONSTRUCTOR
	public Display() {
		// Initialise the JFrame
		frame = createFrame();
		frame.add(this);

		// Initialise Menu Bar
		menu = new MenuBar(this, numLights);
		frame.setJMenuBar(menu);

		// Initialise Mouse Listeners
		MouseHandler mouse = new MouseHandler(this);
		this.addMouseListener(mouse);
		this.addMouseWheelListener(mouse);
		this.addMouseMotionListener(mouse);

		// Initialise Key Listener
		KeyHandler key = new KeyHandler(this);
		this.addKeyListener(key);
		requestFocusInWindow();

		// Start Display
		frame.setVisible(true);
		thread.start();
	}

	private JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(SIZE_X, SIZE_Y);
		frame.setTitle(TITLE);
		frame.setResizable(RESIZABLE);
		frame.setLocationRelativeTo(null); // Sets window in centre
		frame.setLayout(new GridLayout(1, 1, 0, 0));
		return frame;
	}

	// KEY LISTENER
	public void keyPressed(int code) {
		if (code == 17) { // CTRL
			// System.out.println("CTRL - ON");
			ctrl = true;
		}
	}

	public void keyReleased(int code) {
		if (code == 17) { // CTRL
			// System.out.println("CTRL - OFF");
			ctrl = false;
		}
	}

	// MOUSE ACTION LISTENR
	public void mousePressed(Point p, int button) {
		SwingUtilities.convertPointFromScreen(p, this);
		requestFocusInWindow();
		lastMousePoint = p;
		// System.out.println("Mouse Pressed: "+p.x+", "+p.y);
	}

	public void mouseReleased(Point p, int button) {
		SwingUtilities.convertPointFromScreen(p, this);
	}

	public void mouseClicked(Point p, int button) {
		SwingUtilities.convertPointFromScreen(p, this);
	}

	public void mouseWheelMoved(int distance) {
		zoom(distance);
	}

	public void mouseDragged(Point p) {
		// SwingUtilities.convertPointFromScreen(p, this);
		// System.out.println("Mouse Dragged: "+p.x+", "+p.y);
		if (ctrl) {
			rotate(lastMousePoint, p);
		} else {
			pan(lastMousePoint, p);
		}
		lastMousePoint = p;
	}

	// MENU ACTION LISTENER
	public void menuButtonClicked(String button) {
		switch (button) {
		case "About":
			JOptionPane.showMessageDialog(this, "<html>" + TITLE + "<br>Version: " + VERSION + "<br>Author: " + AUTHOR + DESCRIPTION + "</html>", "About", JOptionPane.PLAIN_MESSAGE);
			break;
		case "Load File":
			loadFile();
			break;
		// case "Light Source Intensity":
		// String s = JOptionPane.showInputDialog(this,
		// "Type in an intensity value (between 0 and 1):", "" +
		// lightSourceIntensity);
		// if (s != null) {
		// double d = Double.parseDouble(s);
		// if ((d >= 0) && (d <= 1))
		// lightSourceIntensity = d;
		// }
		// model.refreshImage();
		// break;
		case "Ambient Light Intensity":
			String s = JOptionPane.showInputDialog(this, "Type in an intensity value (between 0 and 1):", "" + ambientIntensity);
			if (s != null) {
				double d = Double.parseDouble(s);
				if ((d >= 0) && (d <= 1))
					ambientIntensity = d;
			}
			model.refreshColors();
			model.refreshImage();
			repaint();
			break;
		// case "Light Source Direction":
		// s = JOptionPane.showInputDialog(this,
		// "Type in a light source direction vector (e.g [0.3,0.5,0.7]):",
		// model.getLightSource().toString());
		// // System.out.println("Stage 0");
		// if (s != null) {
		// String vals[] = s.split(",|\\[|\\]");
		// // System.out.println("Stage 1: "+vals.length);
		// if (vals.length >= 3) {
		// // System.out.println("Stage 2");
		// double x = Double.parseDouble(vals[1]);
		// double y = Double.parseDouble(vals[2]);
		// double z = Double.parseDouble(vals[3]);
		// model.setLightSource(new Vector(x, y, z));
		// model.refreshImage();
		// }
		// }
		// break;
		case "Add Light Source":
			new LightSourceDialog(this,0,NEW_LIGHT_DEFAULTS);
			break;
		case "Enable Light Rotation":
			lightRotation = !lightRotation;
			System.out.println("Light Rotation: " + lightRotation);
			break;
		}
		String[] bVals = button.split("#");
		if(bVals.length == 2){
			int n = Integer.parseInt(bVals[1]);
			new LightSourceDialog(this, n, model.lightSources.get(n-1).getVals());
		}
	}

	private void loadFile() { // Use a JFileLoader to choose a directory
		JFileChooser fileLoader = new JFileChooser();
		fileLoader.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int n = fileLoader.showOpenDialog(this);

		if (n == JFileChooser.APPROVE_OPTION) {
			File file = fileLoader.getSelectedFile();
			try {
				BufferedReader scan = new BufferedReader(new FileReader(file));
				String s;
				// Read in Light Source
				s = scan.readLine();
				String[] vals = s.split(" ");
				Vector light = new Vector(Double.parseDouble(vals[0]), Double.parseDouble(vals[1]), Double.parseDouble(vals[2]));
				model = new Model(light);

				// Read in Polygons
				while ((s = scan.readLine()) != null) {
					vals = s.split(" ");

					Coord c1 = new Coord(Double.parseDouble(vals[0]), Double.parseDouble(vals[1]), Double.parseDouble(vals[2]));
					Coord c2 = new Coord(Double.parseDouble(vals[3]), Double.parseDouble(vals[4]), Double.parseDouble(vals[5]));
					Coord c3 = new Coord(Double.parseDouble(vals[6]), Double.parseDouble(vals[7]), Double.parseDouble(vals[8]));
					int r = Integer.parseInt(vals[9]);
					int g = Integer.parseInt(vals[10]);
					int b = Integer.parseInt(vals[11]);
					Polygon p = new Polygon(c1, c2, c3, r, g, b, model);
					model.addPolygon(p);
				}

				scan.close();
				numLights = 1;
				refreshMenuBar();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error reading file!", "WARNING", JOptionPane.WARNING_MESSAGE);
			}
		}
		if (model != null)
			model.refreshImage();
		repaint();
	}

	public void newLightSource(String[] vals) {
		if (model == null)
			return;
	//	try {
			int r, g, b;
			r = Integer.parseInt(vals[0]);
			g = Integer.parseInt(vals[1]);
			b = Integer.parseInt(vals[2]);
			double x, y, z, i;
			x = Double.parseDouble(vals[3]);
			y = Double.parseDouble(vals[4]);
			z = Double.parseDouble(vals[5]);
			i = Double.parseDouble(vals[6]);
			model.addLightSource(r, g, b, new Vector(x, y, z), i);

			numLights++;
			refreshMenuBar();

			repaint();
//		} catch (Exception e) {
//			JOptionPane.showMessageDialog(this, "Invalid Input", "WARNING", JOptionPane.WARNING_MESSAGE);
//		}
	}
	
	public void changeLightSource(String[] vals, int n) {
		if (model == null)
			return;
		try {
			int r, g, b;
			r = Integer.parseInt(vals[0]);
			g = Integer.parseInt(vals[1]);
			b = Integer.parseInt(vals[2]);
			double x, y, z, i;
			x = Double.parseDouble(vals[3]);
			y = Double.parseDouble(vals[4]);
			z = Double.parseDouble(vals[5]);
			i = Double.parseDouble(vals[6]);
			model.changeLightSource(n,r, g, b, new Vector(x, y, z), i);
			repaint();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Invalid Input", "WARNING", JOptionPane.WARNING_MESSAGE);
		}
	}

	public void refreshMenuBar() {
		menu = new MenuBar(this, numLights);
		frame.setJMenuBar(menu);
		frame.setVisible(true);
	}

	// TRANSLATION
	public void pan(Point p1, Point p2) {
		if (model != null)
			model.translate(p2.x - p1.x, p2.y - p1.y);
		repaint();
	}

	public void zoom(int distance) {
		if (model == null)
			return;
		if (distance > 0) {
			model.zoomOut();
		}
		if (distance < 0) {
			model.zoomIn();
		}
		repaint();
	}

	public void rotate(Point p1, Point p2) {
		if (model != null) {
			model.rotateXaxis((p2.y - p1.y) * 0.03);
			model.rotateYaxis((p2.x - p1.x) * 0.03);
		}
		repaint();
	}

	// RENDERING
	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (model != null)
			model.paint(g);
	}

	// THREAD
	public void run() {
		while (true) {

			// repaint();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void main(String[] args) {
		new Display();
	}
}