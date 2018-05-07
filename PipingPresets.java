

package gralog.gralogfx;
import java.util.HashMap;

public class PipingPresets{
	


    // String neighbourString;
	public HashMap<String,String> colorPresets;
   

    public static String getHexByColorName(String colorName){
    	String hex;
    	
	    colorPresets = new HashMap<String,String>();

	    colorPresets.put("red","#ff0000");

	    public static String getHexByColorName(String colorName){
	        String hex;
	        if ((hex = colorPresets.get(colorName)) != null){
	            return hex;
	        }
	        return null;
	    }
    	if ((hex = colorPresets.get(colorName)) != null){
    		return hex;
    	}
    	return null;
    }

}