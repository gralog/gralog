package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;

public class DeleteEdgeCommand extends CommandForGralogToExecute {
	

	int sourceId;
    int targetId;
	Vertex sourceVertex;
    Vertex targetVertex;
    Edge edgeToDelete;
    int edgeId = -1;

	public DeleteEdgeCommand(String[] externalCommandSegments,Structure structure){
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

        this.sourceVertex = this.structure.getVertexById(this.sourceId);

        try{    
            this.edgeId = Integer.parseInt(externalCommandSegments[4]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }catch(Exception e){
            System.out.println("no id given, who giveth a fuck");
        }

        if (this.edgeId == -1){
            this.edgeToDelete = this.structure.getEdgeByVertexIds(this.sourceId,this.targetId);
        }else{
            this.edgeToDelete = this.structure.getEdgeByVertexIdsAndId(this.sourceId,this.targetId,this.edgeId);
        }

        if (this.edgeToDelete == null){
            if (this.edgeId != -1){
                this.error = new Exception("error: edge with id: " + this.edgeId + " betwee target vertex " + Integer.toString(this.targetId) + " and source vertex " + Integer.toString(this.sourceId) + " does not exist!");
                this.fail();
                return;
            }
            System.out.println("The edge doesn't exist but that's okee!");
        }
        

	}


	public void handle(){

        

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));
        if (this.edgeToDelete != null){
            this.structure.removeEdge(this.edgeToDelete);
        }

        this.setResponse(null);

        return;


        // return v;
	}

}