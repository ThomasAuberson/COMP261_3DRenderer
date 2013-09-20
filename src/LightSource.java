public class LightSource {

	public Vector direction;
	private double intensity;
	private int red;
	private int green;
	private int blue;

	public LightSource(int r, int g, int b, Vector d, double i) {
		red = r;
		green = g;
		blue = b;
		intensity = i;
		direction = d;
	}
	
	public void changeLightSource(int r, int g, int b, Vector d, double i){
		red = r;
		green = g;
		blue = b;
		intensity = i;
		direction = d;
	}

	public void rotateXaxis(double cosTheta, double sinTheta) {
		direction = new Vector(direction.x, (direction.y * cosTheta - direction.z * sinTheta), (direction.y * sinTheta + direction.z * cosTheta));
		//System.out.println("Lightsource Rotating");
	}

	public void rotateYaxis(double cosTheta, double sinTheta) {
		direction = new Vector((direction.x * cosTheta - direction.z * sinTheta), (direction.y), (direction.x * sinTheta + direction.z * cosTheta));
		//System.out.println("Lightsource Rotating");
	}

	public double intensityR() {
		return (intensity*red)/255;
	}

	public double intensityG() {
		return (intensity*green)/255;
	}

	public double intensityB() {
		return (intensity*blue)/255;
	}

	public void setIntensity(double i){
		intensity = i;
	}
	
	public String toString(){
		String s = "Color: "+red+","+green+","+blue+" Direction: "+direction.x+","+direction.y+","+direction.z+" Intensity: "+intensity;
		return s;
	}
	
	public String[] getVals(){
		String[] vals = new String[7];
		vals[0] = ""+red;
		vals[1] = ""+green;
		vals[2] = ""+blue;
		vals[3] = ""+direction.x;
		vals[4] = ""+direction.y;
		vals[5] = ""+direction.z;
		vals[6] = ""+intensity;
		return vals;
	}
}
