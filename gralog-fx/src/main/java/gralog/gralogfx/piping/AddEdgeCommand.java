package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;


public class AddEdgeCommand extends CommandForGralogToExecute {
	

	int sourceId;
    int targetId;
	Vertex sourceVertex;
    Vertex targetVertex;
    int id= -1;
    boolean isDirected;
    // String neighbourString;



	public AddEdgeCommand(String[] externalCommandSegments,Structure structure){
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

        System.out.println("ok found my source, it's got outgoint eges???");

        for (Edge e : this.sourceVertex.getOutgoingEdges()){
            System.out.println("bla: "+ e);
        }

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

        try{
            this.isDirected = externalCommandSegments[4].equals("true");
        }catch(ArrayIndexOutOfBoundsException e){
            this.error = e;
            this.fail();
        }

        try{
            this.id = Integer.parseInt(externalCommandSegments[5]);
        }catch(Exception e){
            System.out.println("no id given, who gives a fuck");
        }
	}


	public void handle(){

        Edge e;
        if (this.id == -1){
            e = structure.addEdge(this.sourceVertex,this.targetVertex,null);
            if (e == null){
                this.fail();
                this.error = new Exception("error: too many edges between vertices " + this.sourceId + " and " + this.targetId + "; only 4 are allowed!");
                this.setResponse("fail");
                return;
            }
        }else{
            e = structure.addEdge(this.sourceVertex,this.targetVertex,this.id,null);
            if (e == null){
                //either the id exists or there are too many edges.
                if (this.structure.getEdgeById(this.id) == null){
                    this.error = new Exception("error: too many edges between vertices " + this.sourceId + " and " + this.targetId + "; only 4 are allowed!");
                }else{
                    this.error = new Exception("error: an edge between vertices " + this.sourceId + " and " + this.targetId + " with id: " + this.id + " already exists");
                }
                this.fail();
                this.setResponse("fail");
                return;
            }
        }

        e.isDirected = this.isDirected;

        this.setResponse(PipingMessageHandler.universalEdgeToGralogTuple(e));

        return;


        // return v;
	}

}