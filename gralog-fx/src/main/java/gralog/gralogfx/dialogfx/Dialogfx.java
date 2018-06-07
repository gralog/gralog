package gralog.gralogfx.dialogfx;

import gralog.gralogfx.StructurePane;
import gralog.structure.Structure;

import static gralog.gralogfx.panels.Console.ANSI_RED;
import static gralog.gralogfx.panels.Console.ANSI_RESET;


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
