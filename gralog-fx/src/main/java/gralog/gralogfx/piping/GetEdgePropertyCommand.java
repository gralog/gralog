package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;
import java.lang.reflect.Field;

public class GetEdgePropertyCommand extends CommandForGralogToExecute {
	

	Vertex sourceVertex;
    Vertex targetVertex;
    Edge edge;
	String propertyString;



	public GetEdgePropertyCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        int sourceVertexId;
        int targetVertexId;
		try{    
            sourceVertexId = Integer.parseInt(externalCommandSegments[2]);
            targetVertexId = Integer.parseInt(externalCommandSegments[3]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        this.sourceVertex = this.structure.getVertexById(sourceVertexId);

        if (this.sourceVertex == null){
            this.fail();
            this.error = new Exception("error: source vertex with id: " + Integer.toString(sourceVertexId) + " does not exist");
            return;
        }

        this.targetVertex = this.structure.getVertexById(targetVertexId);

        if (this.targetVertex == null){
            this.fail();
            this.error = new Exception("error: target vertex with id: " + Integer.toString(targetVertexId) + " does not exist");
            return;
        }

        this.edge = this.structure.getEdgeByVertexIds(sourceVertexId,targetVertexId);
        if (this.edge == null){
            this.fail();
            this.error = new Exception("error: no edge with vertex coordinates " + Integer.toString(sourceVertexId) + " " + Integer.toString(targetVertexId));
            return;
        }
        try{

            this.propertyString = externalCommandSegments[4];
        }catch(Exception e){
            this.fail();
            this.error = e;
            return;
        }

	}

	public void handle(){

        // int changeId;
        boolean found = false;
        Class<?> c = edge.getClass();
        for (Field f : c.getFields()){
            if (f.getName().equals(this.propertyString)){
                try{
                    System.out.println("looking perhasp for... " + f.getName() + "= " + f.get(this.edge));
                    this.setResponse(f.get(this.edge).toString());

                }catch(Exception e){
                    this.fail();
                    this.error = e;

                }
                return;
            }
        }
        this.fail();
        this.error = new Exception("class Edge does not have property : " + this.propertyString);
        return;



        // return v;
	}

}