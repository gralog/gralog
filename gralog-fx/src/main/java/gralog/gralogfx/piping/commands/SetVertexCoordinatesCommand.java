package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;


public class SetVertexCoordinatesCommand extends CommandForGralogToExecute {
    

    int vertexId;
    Vertex vertex;
    
    double x;
    double y;
    // String neighbourString;



    public SetVertexCoordinatesCommand(String[] externalCommandSegments,Structure structure) {
        this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        String stringX;
        String stringY;

        try {    
            this.vertexId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e) {
            this.error = e;
            this.fail();
            return;
        }

        this.vertex = this.structure.getVertexById(this.vertexId);

        if (this.vertex == null) {
            this.fail();
            this.error = new NonExistantVertexException("vertex with id " + Integer.toString(this.vertexId) + " does not exist");
            return;
        }

        

        // this.generateLabel(externalCommandSegments);
        try {    
            stringX = PipingMessageHandler.extractNthPositionString(externalCommandSegments,3);
            stringY = PipingMessageHandler.extractNthPositionString(externalCommandSegments,4);
            
        }catch(Exception e) {
            this.error = e;
            this.fail();
            return;
        }
        try {
            Double.parseDouble(stringX);
        }catch(Exception e) {
            stringX = null;
        }
        try {
            Double.parseDouble(stringY);
        }catch(Exception e) {
            stringY = null;
        }
        Vector2D coordinates = this.vertex.getCoordinates();
        this.x = (stringX == null) ? coordinates.getX() : Double.parseDouble(stringX);
        this.y = (stringY == null) ? coordinates.getY() : Double.parseDouble(stringY);
    }

    


    public void handle() {

        

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));

        
        Vector2D newCoordinates = new Vector2D(x,y);
        this.vertex.setCoordinates(newCoordinates);

        this.setResponse(null);

        return;


        // return v;
    }

}
