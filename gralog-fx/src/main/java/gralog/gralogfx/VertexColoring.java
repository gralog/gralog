package gralog.gralogfx;


import gralog.rendering.GralogColor;
import gralog.structure.Structure;
import gralog.structure.Vertex;

import java.util.HashMap;
import java.util.Map;

public class VertexColoring {
    public HashMap<Integer, GralogColor.Color> coloringFX;
    public HashMap<Vertex, Integer> coloring;

    public VertexColoring(HashMap<Vertex, Integer> coloring, HashMap<Integer, GralogColor.Color> coloringFX){
        if (! (coloring == null))
            this.coloring = coloring;
        else
            this.coloring = new HashMap<Vertex, Integer>();
        if (!(coloringFX == null))
            this.coloringFX = coloringFX;
        else
            this.coloringFX = GralogColor.IntegerToColor();
    }

    public void setColors() {
        int number = 0;
        for (Vertex v : coloring.keySet()) {
            number = coloring.get(v);
            if (number == GralogColor.numberNamedColors())
                number = 0;
            v.fillColor = new GralogColor(coloringFX.get(number).getValue());
        }
    }
}
