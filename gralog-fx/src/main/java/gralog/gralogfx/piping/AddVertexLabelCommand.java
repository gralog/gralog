package gralog.gralogfx;
import gralog.structure.*;
import gralog.rendering.*;

public class AddVertexLabelCommand extends CommandForGralogToExecute {
    

    int vertexId;
    Vertex vertex;
    String label;
    // String neighbourString;



    public AddVertexLabelCommand(String[] externalCommandSegments,Structure structure){
        this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        try{    
            this.vertexId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }


        

        this.vertex = this.structure.getVertexById(this.vertexId);

        if (this.vertex == null){
            this.fail();
            this.error = new Exception("error: vertex with id " + Integer.toString(this.vertexId) + " does not exist");
            return;
        }

        

        this.generateLabel(externalCommandSegments);

    }

    public void generateLabel(String[] externalCommandSegments){
        String label = "";
        for (int i = 3; i < externalCommandSegments.length; i += 1){
            label = label + externalCommandSegments[i]+ " " ;
        }
        this.label = label;
    }


    public void handle(){

        

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));

        this.vertex.setLabel(this.label);

        this.setResponse(null);

        return;


        // return v;
    }

}