
// Examples of toString methods to help debug code -
// Such methods make it easy to print out your data structures when debugging

    /** toString for my EdgeList class */

    public String toString(){
	return String.format("x: %8.3f to %8.3f  z: %8.3f to %8.3f",
			     leftX, rightX, leftZ, rightZ);
    }


    /** toString for my Polygon class */
    public String toString(){
        StringBuilder ans = new StringBuilder("Poly:");
	Formatter f = new Formatter(ans);
	ans.append(hidden?'h':' ');
        for (int i=0; i<3; i++){
	    f.format("(%8.3f,%8.3f,%8.3f)",
		     vertices[i].x, vertices[i].y, vertices[i].z);
	}
	f.format("n:(%6.3f,%6.3f,%6.3f)",
		 normal.x, normal.y, normal.z);
	f.format("c:(%3d-%3d-%3d)",
		 reflectivity.getRed(), reflectivity.getGreen(),
		 reflectivity.getBlue());
	bounds();
	f.format("b:(%3d %3d %3d %3d)",
		 bounds.x, bounds.y, bounds.width, bounds.height);
	if (shade!=null) {
	    f.format("s:(%3d-%3d-%3d)",
		     shade.getRed(), shade.getGreen(), shade.getBlue());}
        return ans.toString();
    }
