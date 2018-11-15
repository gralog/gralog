

package gralog.gralogfx.piping;
import java.util.HashMap;
import gralog.rendering.GralogColor;

public class PipingPresets {
	



    // String neighbourString;

   	

    public static String getHexByColorName(String colorName) {

	    
	    String hex;
	    for (GralogColor.Color c : GralogColor.Color.values()) {
	    	if (c.name().equalsIgnoreCase(colorName)) {
	    		return Integer.toString(c.getValue());
	    	}
	    }
	    

    	return null;
    }

    public static String multiLineIfyGraphString(String graph) {
    	graph = "$$\n" + graph;
    	graph = graph.substring(0,graph.length()-1) + "\n$";
    	return graph;
    }


    public static String properGraphNames(String name) {

        return (String)null;
    }

}
