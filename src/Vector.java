public class Vector {
	
	public final double x;
	public final double y;
	public final double z;
	public final double mag;
	
	public Vector(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
		mag = Math.sqrt(x*x+y*y+z*z);
	}
	
	public String toString(){
		String s = "["+x+","+y+","+z+"]";
		return s;
	}	
}
