package gralog.gralogfx.dialogfx;

import gralog.gralogfx.StructurePane;
import gralog.structure.Edge;
import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.dialog.*;
import gralog.structure.Vertex;

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

    public boolean findVertex(ArrayList<String> parameters, Structure structure, StructurePane structurePane){
        ArrayList<Vertex> allVertices = new ArrayList<Vertex>(structure.getVertices());
        for (Vertex v : allVertices){
            if (v.id == Integer.parseInt(parameters.get(0))){ // parser has already checked that parameter.get(0) is an int
                structurePane.select(v);
                return true;
            }
        }
        return false;
    }

    public boolean findEdge(ArrayList<String> parameters, Structure structure, StructurePane structurePane){
        ArrayList<Edge> allEdges= new ArrayList<Edge>(structure.getEdges());
        for (Edge edge : allEdges){
            if (edge.getId() == Integer.parseInt(parameters.get(0))){ // parser has already checked that parameter.get(0) is an int
                structurePane.select(edge);
                return true;
            }
        }
        return false;
    }
    public boolean findGraphElement(ArrayList<String> parameters, Structure structure, StructurePane structurePane){
        if (findVertex(parameters,structure,structurePane)){
            return true;
        }
        else{
            return findEdge(parameters,structure,structurePane);
        }
    }


}
