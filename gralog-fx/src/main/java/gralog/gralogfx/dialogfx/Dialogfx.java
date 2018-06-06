package gralog.gralogfx.dialogfx;

import gralog.gralogfx.StructurePane;
import gralog.structure.Structure;


public class Dialogfx {

    public void selectAll(StructurePane currentPane){
        Structure structure = currentPane.getStructure();
        currentPane.selectAll(structure.getVertices());
        currentPane.selectAll(structure.getEdges());
        currentPane.requestRedraw();

    }
}
