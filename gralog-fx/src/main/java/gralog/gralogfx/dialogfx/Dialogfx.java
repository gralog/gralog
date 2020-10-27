package gralog.gralogfx.dialogfx;

import gralog.gralogfx.StructurePane;
import gralog.gralogfx.piping.commands.SetVertexShapeCommand;
import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.shapes.*;
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
        if (dialog.existsVertexList(parameters.get(0))) {
            currentPane.selectAll(dialog.findVertexList(parameters.get(0)));
            currentPane.requestRedraw();
            return;
        }
        if (dialog.existsEdgeList(parameters.get(0))){
            currentPane.selectAll(dialog.findEdgeList(parameters.get(0)));
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
    public boolean findGraphElement(ArrayList<String> parameters, StructurePane structurePane){
        Structure structure = structurePane.getStructure();
        if (findVertex(parameters,structure,structurePane)){
            return true;
        }
        else{
            return findEdge(parameters,structure,structurePane);
        }
    }
    public boolean chooseLayout(ArrayList<String> parameters, StructurePane structurePane){
        System.out.println("CHOOSING A LAYOUT NOT IMPLEMENTED YET!\n");
        // TODO: Implement this
        return false;
    }


    public void setDirected(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        Structure structure = currentPane.getStructure();
        if (dialog.existsVertexList(parameters.get(0))) {
            dialog.setErrorMsg("Choose a list of edges, not of vertices.");
            return;
        }
        if (dialog.existsEdgeList(parameters.get(0))) {
            for (Object e : dialog.findEdgeList(parameters.get(0))) {
                Edge ee = (Edge) e;
                ee.setDirectedness(true);
            }
            currentPane.requestRedraw();
        }
        else{
            dialog.setErrorMsg("No such list of edges found.");
            return;
        }
    }

    public void setUndirected(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        Structure structure = currentPane.getStructure();
        if (dialog.existsVertexList(parameters.get(0))) {
            dialog.setErrorMsg("Choose a list of edges, not of vertices.");
            return;
        }
        if (dialog.existsEdgeList(parameters.get(0))) {
            for (Object e : dialog.findEdgeList(parameters.get(0))) {
                Edge ee = (Edge) e;
                ee.setDirectedness(false);
            }
            currentPane.requestRedraw();
        }
        else{
            dialog.setErrorMsg("No such list of edges found.");
            return;
        }
    }


    public void setColor(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {

        if (dialog.existsVertexList(parameters.get(0))) {
            dialog.setErrorMsg("\'Set color\' is only defined for edges. For vertices, say \'set fill [color]\' or \'set stroke [color]\'.");
            return;
        }

        if (dialog.existsEdgeList(parameters.get(0))) {
            for (Object e : dialog.findEdgeList((parameters.get(0)))) {
                Edge ee = (Edge) e;
                ee.color = new GralogColor (GralogColor.stringToColor(parameters.get(1)));
            }
            currentPane.requestRedraw();
        }
        else{
            dialog.setErrorMsg("No such list of edges found.");
            return;
        }
    }


    public void setLabel(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        if (dialog.existsVertexList(parameters.get(0))) {
            for (Object v : dialog.findVertexList(parameters.get(0))){
                Vertex vv = (Vertex) v;
                vv.setLabel(parameters.get(1));
            }
            currentPane.requestRedraw();
            return;
        }

        if (dialog.existsEdgeList(parameters.get(0))) {
            for (Object e : dialog.findEdgeList(parameters.get(0))){
                Edge ee = (Edge) e;
                ee.setLabel(parameters.get(1));
            }
            currentPane.requestRedraw();
            return;
        }

        dialog.setErrorMsg("No such list found.");
        return;
    }

    public void setType(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        if (dialog.existsVertexList(parameters.get(0))) {
            dialog.setErrorMsg("Choose a list of edges, not of vertices.");
            return;
        }

        if (dialog.existsEdgeList(parameters.get(0))) {
            GralogGraphicsContext.LineType type = null;
            switch (parameters.get(1)) {
                case "PLAIN":   type = GralogGraphicsContext.LineType.PLAIN;
                                break;
                case "DOTTED":  type = GralogGraphicsContext.LineType.DOTTED;
                                break;
                case "DASHED":  type = GralogGraphicsContext.LineType.DASHED;
                                break;
            }
            for (Object e : dialog.findEdgeList(parameters.get(0))){
                Edge ee = (Edge) e;
                ee.type = type;
            }
            currentPane.requestRedraw();
            return;
        }

        dialog.setErrorMsg("No such list of edges found.");
        return;
    }

    public void setEdgeType(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        if (dialog.existsVertexList(parameters.get(0))) {
            dialog.setErrorMsg("Choose a list of edges, not of vertices.");
            return;
        }

        if (dialog.existsEdgeList(parameters.get(0))) {
            Edge.EdgeType type = null;
            switch (parameters.get(1)) {
                case "SHARP":   type = Edge.EdgeType.SHARP;
                    break;
                case "BEZIER":  type = Edge.EdgeType.BEZIER;
                    break;
            }
            for (Object e : dialog.findEdgeList(parameters.get(0))){
                Edge ee = (Edge) e;
                ee.setEdgeType(type);
            }
            currentPane.requestRedraw();
            return;
        }

        dialog.setErrorMsg("No such list of edges found.");
        return;
    }

    public void setThickness(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        if (dialog.existsVertexList(parameters.get(0))) {
            dialog.setErrorMsg("Choose a list of edges, not of vertices.");
            return;
        }

        if (dialog.existsEdgeList(parameters.get(0))) {
            for (Object e : dialog.findEdgeList(parameters.get(0))){
                Edge ee = (Edge) e;
                ee.thickness = Double.parseDouble(parameters.get(1));
            }
            currentPane.requestRedraw();
            return;
        }
        dialog.setErrorMsg("No such list of edges found.");
        return;
    }

    public void setWeight(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        if (dialog.existsVertexList(parameters.get(0))) {
            dialog.setErrorMsg("Choose a list of edges, not of vertices.");
            return;
        }

        if (dialog.existsEdgeList(parameters.get(0))) {
            for (Object e : dialog.findEdgeList(parameters.get(0))){
                Edge ee = (Edge) e;
                ee.weight = Double.parseDouble(parameters.get(1));
            }
            currentPane.requestRedraw();
            return;
        }
        dialog.setErrorMsg("No such list of edges found.");
        return;
    }


    public void setFillColor(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        if (dialog.existsEdgeList(parameters.get(0))) {
            dialog.setErrorMsg("\'Set fill [color]\' is only defined for vertices. For edges, say \'set <list> color\'.");
            return;
        }

        if (dialog.existsVertexList(parameters.get(0))) {
            for (Object v : dialog.findVertexList(parameters.get(0))) {
                Vertex vv = (Vertex) v;
                vv.fillColor = new GralogColor (GralogColor.stringToColor(parameters.get(1)));
            }
            currentPane.requestRedraw();
        }
        else{
            dialog.setErrorMsg("No such list of vertices found.");
            return;
        }
    }

    public void setStrokeColor(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        if (dialog.existsEdgeList(parameters.get(0))) {
            dialog.setErrorMsg("\'Set stroke [color]\' is only defined for vertices. For edges, say \'set <list> color\'.");
            return;
        }

        if (dialog.existsVertexList(parameters.get(0))) {
            for (Object v : dialog.findVertexList(parameters.get(0))) {
                Vertex vv = (Vertex) v;
                vv.strokeColor = new GralogColor (GralogColor.stringToColor(parameters.get(1)));
            }
            currentPane.requestRedraw();
        }
        else{
            dialog.setErrorMsg("No such list of vertices found.");
            return;
        }
    }

    public void setShape(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        if (dialog.existsEdgeList(parameters.get(0))) {
            dialog.setErrorMsg("Choose a list of vertices, not of edegs.");
            return;
        }

        if (dialog.existsVertexList(parameters.get(0))) {
            GralogGraphicsContext.LineType type = null;
            for (Object v : dialog.findVertexList(parameters.get(0))){
                Vertex vv = (Vertex) v;
                switch (parameters.get(1)) {
                    case "ELLIPSE":   vv.shape = new Ellipse (vv.shape.sizeBox);
                        break;
                    case "CYCLE":  vv.shape = new Cycle(vv.shape.sizeBox);
                        break;
                    case "DIAMOND":  vv.shape = new Diamond(vv.shape.sizeBox);
                        break;
                    case "RECTANGLE": vv.shape = new Rectangle(vv.shape.sizeBox);
                        break;
                }
            }
            currentPane.requestRedraw();
            return;
        }
        dialog.setErrorMsg("No such list of edges found.");
        return;
    }

    public void setWidth(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        if (dialog.existsEdgeList(parameters.get(0))) {
            dialog.setErrorMsg("Choose a list of vertices, not of edges.");
            return;
        }

        if (dialog.existsVertexList(parameters.get(0))) {
            for (Object v : dialog.findVertexList(parameters.get(0))){
                Vertex vv = (Vertex) v;
                vv.shape.setWidth(Double.parseDouble(parameters.get(1)));
            }
            currentPane.requestRedraw();
            return;
        }
        dialog.setErrorMsg("No such list of vertices found.");
        return;
    }

    public void setHeight(ArrayList<String> parameters, StructurePane currentPane, Dialog dialog) {
        if (dialog.existsEdgeList(parameters.get(0))) {
            dialog.setErrorMsg("Choose a list of vertices, not of edges.");
            return;
        }

        if (dialog.existsVertexList(parameters.get(0))) {
            for (Object v : dialog.findVertexList(parameters.get(0))){
                Vertex vv = (Vertex) v;
                vv.shape.setHeight(Double.parseDouble(parameters.get(1)));
            }
            currentPane.requestRedraw();
            return;
        }
        dialog.setErrorMsg("No such list of vertices found.");
        return;
    }
}
