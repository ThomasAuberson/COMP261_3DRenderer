public class EdgeList {
	
	/*
	 * 3D Renderer
	 * 
	 * Author: Thomas Auberson
	 * Version: 0.12
	 * 
	 * This class handles edge lists for polygons
	 */

	// FIELDS
	private double miny,maxy;
	private double[][] values; // Xl Zl Xr Zr

	public EdgeList(double miny, double maxy) {
		this.miny = miny;
		this.maxy = maxy;
		values = new double[(int)Math.ceil((maxy - miny + 1))][4];
	}

	public void addRow(double y, double xl, double zl, double xr, double zr) {
		values[(int)Math.ceil((y - miny))][0] = xl;
		values[(int)Math.ceil((y - miny))][1] = zl;
		values[(int)Math.ceil((y - miny))][2] = xr;
		values[(int)Math.ceil((y - miny))][3] = zr;
	}

	public double getXl(double y) {
		return values[(int)Math.ceil((y - miny))][0];
	}
	
	public double getXr(double y) {
		return values[(int)Math.ceil((y - miny))][2];
	}
	
	public double getZl(double y) {
		return values[(int)Math.ceil((y - miny))][1];
	}
	
	public double getZr(double y) {
		return values[(int)Math.ceil((y - miny))][3];
	}
	
//	public boolean contains(int x, int y){
//		return ((miny <= y)&&(maxy >= y));
//	}
	
	public String toString(){
		String sy = "[";
		String sxl = "[";
		String sxr = "[";
		String szl = "[";
		String szr = "[";
		for(int j = (int)Math.ceil(miny); j<=maxy; j++){
			sy = sy+j+",";
			sxl = sxl+getXl(j)+",";
			sxr = sxr+getXr(j)+",";
			szl = szl+getZl(j)+",";
			szr = szr+getZr(j)+",";
		}
		sy = miny+";"+maxy+" "+sy + "]\n"+sxl+ "]\n"+ sxr + "]\n" + szl + "]\n" +szr + "]\n";
		return sy;
	}
}
