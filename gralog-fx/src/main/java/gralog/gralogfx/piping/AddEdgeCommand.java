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
            e = structure.createEdge(this.sourceVertex,this.targetVertex);
            System.out.println("and directly afterward we have id = " + e.getId());
        }else{
            e = structure.createEdge(this.sourceVertex,this.targetVertex,this.id);
        }

        if (e == null){
            this.fail();
            this.error = new Exception("error: an edge between vertices " + this.sourceId + " and " + this.targetId + " with id: " + this.id + " already exists");
            this.setResponse("fail");
            return;
        }else{
            System.out.println("they say e is not null. ok then it is: " +e);
        }
    
        System.out.println("i have creaeted e: " + e);
            
        e.isDirected = this.isDirected;

        this.structure.addEdge(e);

        this.setResponse(Integer.toString(e.getId()));

        return;


        // return v;
	}

}