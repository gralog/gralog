package gralog.gralogfx;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;


public class GetIncomingEdgesCommand extends CommandForGralogToExecute {
	

	int sourceId;
	Vertex sourceVertex;
    // String neighbourString;



	public GetIncomingEdgesCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        System.out.println("init GetIncomingEdgesCommand");
        try{    
            this.sourceId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        this.sourceVertex = this.structure.getVertexById(this.sourceId);

        if (this.sourceVertex == null){
            this.fail();
            this.error = new Exception("error: source vertex does not exist");
            return;
        }
	}


	

	public void handle(){

        // int changeId;
       
        
        

        Set<Edge> conncetedEdges = this.sourceVertex.getIncomingEdges();

        System.out.println("here's what we got for incoming edges: ");
        for (Edge e : conncetedEdges){
            System.out.println(e.toString());
        }
        System.out.println("fin");

        String edgeString = "";
        for (Edge e : conncetedEdges){
            edgeString = edgeString + "("+Integer.toString(e.getSource().getId())+","+Integer.toString(e.getTarget().getId())+")"+ "#";
        }
        if (edgeString.length() > 0 && null != edgeString){
            edgeString = edgeString.substring(0,edgeString.length()-1);
        }

        System.out.println("here we our edge string |" + edgeString+ "|");
        this.setResponse(edgeString.trim());

        return;


        // return v;
	}

}