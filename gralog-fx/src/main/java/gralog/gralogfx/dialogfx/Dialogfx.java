package gralog.gralogfx.dialogfx;

import gralog.gralogfx.StructurePane;
import gralog.structure.Structure;
import gralog.dialog.*;

import java.util.ArrayList;


public class Dialogfx {

    public void selectAll(StructurePane currentPane){
        Structure structure = currentPane.getStructure();
        currentPane.selectAll(structure.getVertices());
        currentPane.selectAll(structure.getEdges());
        currentPane.requestRedraw();
    }

    public void selectAllVertices(StructurePane currentPane){
        Structure structure = currentPane.getStructure();
        currentPane.selectAll(structure.getVertices());
        currentPane.requestRedraw();
    }

    public void selectAllEdges(StructurePane currentPane){
        Structure structure = currentPane.getStructure();
        currentPane.selectAll(structure.getEdges());
        currentPane.requestRedraw();
    }

    public void selectList(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog){
        Structure structure = currentPane.getStructure();
        if (dialog.getVertexListS().containsKey(parameters.get(0))) {
            currentPane.selectAll(dialog.getVertexListS().get(parameters.get(0)));
            currentPane.requestRedraw();
            return;
        }
        if (dialog.getEdgeListS().containsKey(parameters.get(0))){
            currentPane.selectAll(dialog.getEdgeListS().get(parameters.get(0)));
            currentPane.requestRedraw();
            return;
        }
        dialog.setErrorMsg("No such list: " + parameters.get(0) + ".\n");
    }

    public void deselectAll(StructurePane currentPane){
        currentPane.clearSelection();
        currentPane.requestRedraw();
    }

    public void deselectAllVertices(StructurePane currentPane){
        Structure structure = currentPane.getStructure();
        currentPane.deselectAll(structure.getVertices());
        currentPane.requestRedraw();
    }

    public void deselectAllEdges(StructurePane currentPane){
        Structure structure = currentPane.getStructure();
        currentPane.deselectAll(structure.getEdges());
        currentPane.requestRedraw();
    }

}
