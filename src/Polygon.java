import java.awt.Color;
import java.util.HashSet;

/*
 * 3D Renderer
 * 
 * Author: Thomas Auberson
 * Version: 0.12
 * 
 * This class stores and renders poygons in the model
 */

public class Polygon {

	private Coord[] coords = new Coord[3];
	private final int red;
	private final int green;
	private final int blue;

	private Vector normal;
	private Model model;
	// private HashSet<LightSource> lightSources;

	private double minx = Integer.MAX_VALUE, maxx = Integer.MIN_VALUE, miny = Integer.MAX_VALUE, maxy = Integer.MIN_VALUE;
	private EdgeList edgeList;
	private Color color;

	public Polygon(Coord c1, Coord c2, Coord c3, int r, int g, int b, Model m) {
		model = m;
		coords[0] = c1;
		coords[1] = c2;
		coords[2] = c3;
		red = r;
		green = g;
		blue = b;

		totalRefresh();
	}

	// TRANSLATION
	public void translate(int x, int y) {
		coords[0] = new Coord(coords[0].x + x, coords[0].y + y, coords[0].z);
		coords[1] = new Coord(coords[1].x + x, coords[1].y + y, coords[1].z);
		coords[2] = new Coord(coords[2].x + x, coords[2].y + y, coords[2].z);
		translationRefresh();
	}

	public void zoomIn(double rate) {
		coords[0] = new Coord(coords[0].x * rate, coords[0].y * rate, coords[0].z * rate);
		coords[1] = new Coord(coords[1].x * rate, coords[1].y * rate, coords[1].z * rate);
		coords[2] = new Coord(coords[2].x * rate, coords[2].y * rate, coords[2].z * rate);
		translationRefresh();
	}

	public void zoomOut(double rate) {
		coords[0] = new Coord(coords[0].x * rate, coords[0].y * rate, coords[0].z * rate);
		coords[1] = new Coord(coords[1].x * rate, coords[1].y * rate, coords[1].z * rate);
		coords[2] = new Coord(coords[2].x * rate, coords[2].y * rate, coords[2].z * rate);
		translationRefresh();
	}

	public void rotateXaxis(double cosTheta, double sinTheta) {
		coords[0] = new Coord(coords[0].x, (coords[0].y * cosTheta - coords[0].z * sinTheta), (coords[0].y * sinTheta + coords[0].z * cosTheta));
		coords[1] = new Coord(coords[1].x, (coords[1].y * cosTheta - coords[1].z * sinTheta), (coords[1].y * sinTheta + coords[1].z * cosTheta));
		coords[2] = new Coord(coords[2].x, (coords[2].y * cosTheta - coords[2].z * sinTheta), (coords[2].y * sinTheta + coords[2].z * cosTheta));
		totalRefresh();
	}

	public void rotateYaxis(double cosTheta, double sinTheta) {
		coords[0] = new Coord((coords[0].x * cosTheta - coords[0].z * sinTheta), (coords[0].y), (coords[0].x * sinTheta + coords[0].z * cosTheta));
		coords[1] = new Coord((coords[1].x * cosTheta - coords[1].z * sinTheta), (coords[1].y), (coords[1].x * sinTheta + coords[1].z * cosTheta));
		coords[2] = new Coord((coords[2].x * cosTheta - coords[2].z * sinTheta), (coords[2].y), (coords[2].x * sinTheta + coords[2].z * cosTheta));
		totalRefresh();
	}

	// REFRESHING
	private void totalRefresh() {
		// System.out.println("New Light Source: "+lightSource.toString());
		setBounds();
		edgeList = getEdgeList();
		updateNormal();
		updateColor();
		// Debugging
		// System.out.println(toString());
		// System.out.println(red + " " + green + " " + blue);
		// System.out.print(edgeList.toString());
	}

	private void translationRefresh() {
		setBounds();
		edgeList = getEdgeList();
	}

	private void updateNormal() {
		Coord c1 = coords[0];
		Coord c2 = coords[1];
		Coord c3 = coords[2];
		double xnorm = c1.y * (c2.z - c3.z) + c2.y * (c3.z - c1.z) + c3.y * (c1.z - c2.z);
		double ynorm = c1.z * (c2.x - c3.x) + c2.z * (c3.x - c1.x) + c3.z * (c1.x - c2.x);
		double znorm = c1.x * (c2.y - c3.y) + c2.x * (c3.y - c1.y) + c3.x * (c1.y - c2.y);
		normal = new Vector(xnorm, ynorm, znorm);
	}

	private double lightAngle(Vector lightSource) {
		// Dot product of lightSource & Normal
		double dotProduct = normal.x * lightSource.x + normal.y * lightSource.y + normal.z * lightSource.z;
		// Cosine (Theta == angle) =
		// dotProduct/(normal_magnitude*lightsource_magnitude)
		double cosTheta = dotProduct / (normal.mag * lightSource.mag);
		// Return a relative intensity between 0 - 1
		// NOTE: At Angle = 0 == cosTheta = Maximum & relIntensity = Maximum ==>
		// cosTheta is proportional to relIntensity
		double relIntensity = cosTheta / 2 + 0.5;
		if (relIntensity == Double.NaN)
			relIntensity = 0;
		return relIntensity;
	}

	public void updateColor() {
		double intensityR = Display.ambientIntensity;
		double intensityG = Display.ambientIntensity;
		double intensityB = Display.ambientIntensity;
		// System.out.println("Stage 1: "+intensityR+","+intensityG+","+intensityB);
		for (LightSource l : model.lightSources) {
			intensityR += (l.intensityR() * lightAngle(l.direction));
			intensityG += (l.intensityG() * lightAngle(l.direction));
			intensityB += (l.intensityB() * lightAngle(l.direction));
			// System.out.println("Stage 2.x: "+intensityR+","+intensityG+","+intensityB);
		}
		if (intensityR > 1)
			intensityR = 1.0;
		if (intensityG > 1)
			intensityG = 1.0;
		if (intensityB > 1)
			intensityB = 1.0;
		// System.out.println("Stage 3: "+intensityR+","+intensityG+","+intensityB);
		int r = (int) (red * intensityR);
		int g = (int) (green * intensityG);
		int b = (int) (blue * intensityB);
		color = new Color(r, g, b);
	}

	private void setBounds() {
		minx = Integer.MAX_VALUE;
		maxx = Integer.MIN_VALUE;
		miny = Integer.MAX_VALUE;
		maxy = Integer.MIN_VALUE;
		for (int i = 0; i < 3; i++) {
			Coord c = coords[i];
			if (c.x < minx)
				minx = c.x;
			if (c.x > maxx)
				maxx = c.x;
			if (c.y < miny)
				miny = c.y;
			if (c.y > maxy)
				maxy = c.y;
		}
	}

	private EdgeList getEdgeList() {
		EdgeList edgeList = new EdgeList(miny, maxy);
		Edge[] edges = new Edge[3];
		edges[0] = new Edge(coords[0], coords[1]);
		edges[1] = new Edge(coords[1], coords[2]);
		edges[2] = new Edge(coords[2], coords[0]);

		for (int y = (int)Math.ceil(miny); y <= maxy; y++) {
			double xl = Integer.MAX_VALUE, xr = Integer.MIN_VALUE, zl = Integer.MAX_VALUE, zr = Integer.MIN_VALUE;
			if (edges[0].onEdge(y)) {
				xr = xl = edges[0].getX(y);
				zr = zl = edges[0].getZ(y);
			}
			if (edges[1].onEdge(y)) {
				if (xl > edges[1].getX(y))
					xl = edges[1].getX(y);
				if (xr <= edges[1].getX(y))
					xr = edges[1].getX(y);
				if (zl > edges[1].getZ(y))
					zl = edges[1].getZ(y);
				if (zr <= edges[1].getZ(y))
					zr = edges[1].getZ(y);
			}
			if (edges[2].onEdge(y)) {
				if (xl > edges[2].getX(y))
					xl = edges[2].getX(y);
				if (xr <= edges[2].getX(y))
					xr = edges[2].getX(y);
				if (zl > edges[2].getZ(y))
					zl = edges[2].getZ(y);
				if (zr <= edges[2].getZ(y))
					zr = edges[2].getZ(y);
			}
			edgeList.addRow(y, xl, zl, xr, zr);
		}
		return edgeList;
	}

	public boolean contains(int x, int y) {
		if (((miny <= y) && (maxy >= y)) && ((minx <= x) && (maxx >= x))) {
			if ((edgeList.getXl(y) <= x) && (edgeList.getXr(y) >= x))
				return true;
		}
		return false;
	}

	public double getDepth(int x, int y) {
		double z1 = edgeList.getZl(y);
		double z2 = edgeList.getZr(y);
		double x1 = edgeList.getXl(y);
		double x2 = edgeList.getXr(y);
		if (x2 == x1) {
			return (z1 + z2) / 2;
		}
		return (((z2 - z1) / (x2 - x1)) * (x - x1) + z1);
	}

	public Color getColor() {
		return color;
	}

	public boolean isVisible() {
		if (normal.z > 0)
			return false;
		return true;
	}

	public String toString() {
		String s = coords[0].toString() + coords[1].toString() + coords[2].toString();
		return s;
	}
}
