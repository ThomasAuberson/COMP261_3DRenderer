
/*
 * 3D Renderer
 * 
 * Author: Thomas Auberson
 * Version: 0.12
 * 
 * This class stores information relating to edges of polygons
 */

public class Edge {
	
	private double x1, z1, y1, y2;
	private double gradientX, gradientZ;
	
	public Edge(Coord c1, Coord c2){
		if(c1.y > c2.y){
			Coord c = c1;
			c1 = c2;
			c2 = c;
		}			
		x1 = c1.x;
		z1 = c1.z;
		y1 = c1.y;
		y2 = c2.y;
		gradientX = (c2.x-c1.x)/(c2.y-c1.y);
		gradientZ = (c2.z-c1.z)/(c2.y-c1.y);
	}
	
	public double getX(double y){
		return (gradientX*(y-y1)+x1);
	}
	
	public double getZ(double y){
		return (gradientZ*(y-y1)+z1);
	}
	
	public boolean onEdge(double y){
		return (((y2>=y)&&(y1<=y))||((y2<=y)&&(y1>=y)));
	}
	

}
