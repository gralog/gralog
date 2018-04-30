package gralog.gralogfx;
import gralog.structure.*;
import gralog.rendering.*;

public class AddEdgeLabelCommand extends CommandForGralogToExecute {
    

    int sourceId;
    int targetId;
    Vertex sourceVertex;
    Vertex targetVertex;
    Edge edgeToAddLabelTo;
    String label;
    // String neighbourString;



    public AddEdgeLabelCommand(String[] externalCommandSegments,Structure structure){
        this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        try{    
            this.sourceId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        this.externalCommandSegments = externalCommandSegments;

        try{    
            this.targetId = Integer.parseInt(externalCommandSegments[3]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        this.sourceVertex = this.structure.getVertexById(this.sourceId);

        if (this.sourceVertex == null){
            this.fail();
            this.error = new Exception("error: source vertex with id " + Integer.toString(this.sourceId) + " does not exist");
            return;
        }

        this.targetVertex = this.structure.getVertexById(this.targetId);

        if (this.targetVertex == null){
            this.fail();
            this.error = new Exception("error: target vertex with id " + Integer.toString(this.targetId) + " does not exist");
            return;
        }

        this.edgeToAddLabelTo = this.structure.getEdgeByVertexIds(this.sourceId,this.targetId);
        if (this.edgeToAddLabelTo == null){
            System.out.println("fail!!!! ahahaha i love failure");
            this.fail();
            this.error = new Exception("error: no edge with vertex coordinates " + Integer.toString(this.sourceId) + " " + Integer.toString(this.targetId));
            return;
        }

        this.generateLabel(externalCommandSegments);

    }

    public void generateLabel(String[] externalCommandSegments){
        String label = "";
        for (int i = 4; i < externalCommandSegments.length; i += 1){
            label = label + externalCommandSegments[i]+ " ";
        }
        this.label = label;
    }


    public void handle(){

        

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));

        this.edgeToAddLabelTo.setLabel(this.label);

        this.setResponse("ack");

        return;


        // return v;
    }

}