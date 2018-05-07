

package gralog.gralogfx;
import java.util.HashMap;

public class PipingPresets{
	


    // String neighbourString;

   	public static HashMap<String,String> colorPresets = new HashMap<String,String>();

   	static{
	   	colorPresets.put("red","#e6194b");
		colorPresets.put("green","#3cb44b");
		colorPresets.put("yellow","#ffe119");
		colorPresets.put("blue","#0082c8");
		colorPresets.put("orange","#f58231");
		colorPresets.put("purple","#911eb4");
		colorPresets.put("cyan","#46f0f0");
		colorPresets.put("magenta","#f032e6");
		colorPresets.put("lime","#d2f53c");
		colorPresets.put("pink","#fabebe");
		colorPresets.put("teal","#008080");
		colorPresets.put("lavender","#e6beff");
		colorPresets.put("brown","#aa6e28");
		colorPresets.put("beige","#fffac8");
		colorPresets.put("maroon","#800000");
		colorPresets.put("mint","#aaffc3");
		colorPresets.put("olive","#808000");
		colorPresets.put("coral","#ffd8b1");
		colorPresets.put("navy","#000080");
		colorPresets.put("grey","#808080");
		colorPresets.put("white","#FFFFFF");
		colorPresets.put("black","#000000");
		colorPresets.put("puce","#cc8899");
	}

    public static String getHexByColorName(String colorName){

	    




	    String hex;
	    if ((hex = colorPresets.get(colorName)) != null){
	        return hex;
	    }

    	
    	return null;
    }

    public static String properGraphNames(String name){

        return (String)null;
    }

}