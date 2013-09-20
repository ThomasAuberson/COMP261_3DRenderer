import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JOptionPane;

/*
 * 3D Renderer
 * 
 * Author: Thomas Auberson
 * Version: 0.12
 * 
 * This class holds the model and methods that handle rendering and translating the model
 */

public class Model {

	public static final double ZOOM_RATE = 1.5;

	private HashSet<Polygon> polygons = new HashSet<Polygon>();
	private HashSet<Polygon> visiblePolygons = new HashSet<Polygon>();

	private BufferedImage img;
	private final int width = 700, height = 700;

	public final double DEFAULT_INTENSITY = 0.5;

	public ArrayList<LightSource> lightSources = new ArrayList<LightSource>();

	// CONSTRUCTOR
	public Model(Vector lightSource) {
		lightSources.add(new LightSource(255, 255, 255, lightSource, DEFAULT_INTENSITY));
		refreshImage();
	}

	public void addPolygon(Polygon p) {
		polygons.add(p);
	}

	public void addLightSource(int r, int g, int b, Vector d, double i) {
		lightSources.add(new LightSource(r, g, b, d, i));
		refreshColors();
		refreshImage();
	}

	public void changeLightSource(int n, int r, int g, int b, Vector d, double i) {
		lightSources.get(n).changeLightSource(r, g, b, d, i);
		refreshColors();
		refreshImage();
	}

	public void refreshColors() {
		for (Polygon p : polygons) {
			p.updateColor();
		}
	}

	// TRANSLATION
	public void translate(int x, int y) {
		// System.out.println("Model Translate: " + x + ", " + y);
		for (Polygon p : polygons) {
			p.translate(x, y);
		}
		refreshImage();
	}

	public void zoomIn() {
		for (Polygon p : polygons) {
			p.zoomIn(ZOOM_RATE);
		}
		refreshImage();
	}

	public void zoomOut() {
		double rate = 1 / ZOOM_RATE;
		for (Polygon p : polygons) {
			p.zoomOut(rate);
		}
		refreshImage();
	}

	public void rotateXaxis(double angle) {
		double cosTheta = Math.cos(angle);
		double sinTheta = Math.sin(angle);
		if (Display.lightRotation) {
			for (LightSource l : lightSources) {
				l.rotateXaxis(cosTheta, sinTheta);
			}
		}
		for (Polygon p : polygons) {
			p.rotateXaxis(cosTheta, sinTheta);
		}
		refreshImage();
	}

	public void rotateYaxis(double angle) {
		double cosTheta = Math.cos(angle);
		double sinTheta = Math.sin(angle);
		if (Display.lightRotation) {
			for (LightSource l : lightSources) {
				l.rotateYaxis(cosTheta, sinTheta);
			}
		}
		for (Polygon p : polygons) {
			p.rotateYaxis(cosTheta, sinTheta);
		}
		refreshImage();
	}

	// IMAGE PROCESSING
	private BufferedImage convertBitmapToImage(Color[][] bitmap, int w, int h) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	private void computeVisiblePolygons() {
		visiblePolygons.clear();
		for (Polygon p : polygons) {
			if (p.isVisible()) {
				visiblePolygons.add(p);
			}
		}
	}

	// REFRESH IMAGE
	public void refreshImage() {
		computeVisiblePolygons();

		// Initialize Z Buffer
		Color[][] colorBuffer = new Color[width][height];
		double[][] depthBuffer = new double[width][height];

		for (int i = 0; i < colorBuffer.length; i++) {
			for (int j = 0; j < colorBuffer[0].length; j++) {
				colorBuffer[i][j] = Color.gray; // Initialize colors to grey
			}
		}
		for (int i = 0; i < depthBuffer.length; i++) {
			for (int j = 0; j < depthBuffer[0].length; j++) {
				depthBuffer[i][j] = Integer.MAX_VALUE; // Initialize depths to
														// very far away
			}
		}

		for (Polygon p : visiblePolygons) {
			// p.refreshVisiblePolygon(lightSource);
			for (int i = 0; i < depthBuffer.length; i++) {
				for (int j = 0; j < depthBuffer[0].length; j++) {
					if (p.contains(i - 350, j - 350)) {
						double depth = p.getDepth(i - 350, j - 350);
						if (depth <= depthBuffer[i][j]) {
							depthBuffer[i][j] = depth;
							colorBuffer[i][j] = p.getColor();
						}
					}
				}
			}
		}

		// Set colorBuffer to image
		img = convertBitmapToImage(colorBuffer, width, height);
	}

	// LIGHT SOURCE
	// public Vector getLightSource() {
	// return lightSource;
	// }
	//
	// public void setLightSource(Vector v) {
	// lightSource = v;
	// }

	// RENDERING
	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);

	}
}
