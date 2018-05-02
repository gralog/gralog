package gralog.gralogfx;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;


public class GetNeighbouringEdgesCommand extends CommandForGralogToExecute {
	

	int sourceId;
	Vertex sourceVertex;
    int targetId;
    Vertex targetVertex;
    // String neighbourString;



	public GetNeighbouringEdgesCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        try{    
            this.sourceId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

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

       
	}


	

	public void handle(){

        // int changeId;
       
        
        

        Set<Edge> neighbouringEdges = this.sourceVertex.getConnectedEdges();
        neighbouringEdges.addAll(this.targetVertex.getConnectedEdges());


        String edgeString = "";
        for (Edge e : neighbouringEdges){
            edgeString = edgeString + "("+Integer.toString(e.getSource().getId())+","+Integer.toString(e.getTarget().getId())+")"+ " ";
        }
        if (edgeString.length() > 0 && null != edgeString){
            edgeString = edgeString.substring(0,edgeString.length()-1);
        }


        this.setResponse(edgeString);

        return;


        // return v;
	}

}