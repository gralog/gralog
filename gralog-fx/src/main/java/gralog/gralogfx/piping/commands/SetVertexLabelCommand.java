package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;


public class SetVertexLabelCommand extends CommandForGralogToExecute {
    

    int vertexId;
    Vertex vertex;
    String label;
    // String neighbourString;



    public SetVertexLabelCommand(String[] externalCommandSegments,Structure structure) {
        this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

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
            this.label = PipingMessageHandler.extractNthPositionString(externalCommandSegments,3);
        }catch(Exception e) {
            this.error = e;
            this.fail();
            return;
        }

    }

    public void generateLabel(String[] externalCommandSegments) {
        String label = "";
        String[] labelPieces = externalCommandSegments[3].split(" ");
        for (int i = 0; i < labelPieces.length; i += 1) {
            label = label + labelPieces[i]+ " ";
        }
        this.label = label;
    }


    public void handle() {

        

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));

        this.vertex.setLabel(this.label);

        this.setResponse(null);

        return;


        // return v;
    }

}
