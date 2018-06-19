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
    int sourceId;
    int targetId;
    int edgeId = -1;



	public GetEdgePropertyCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        

		try{    
            this.sourceId = Integer.parseInt(externalCommandSegments[2]);
            this.targetId = Integer.parseInt(externalCommandSegments[3]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        this.sourceVertex = this.structure.getVertexById(this.sourceId);

        if (this.sourceVertex == null){
            this.fail();
            this.error = new Exception("error: source vertex with id: " + Integer.toString(this.sourceId) + " does not exist");
            return;
        }

        this.targetVertex = this.structure.getVertexById(this.targetId);

        if (this.targetVertex == null){
            this.fail();
            this.error = new Exception("error: target vertex with id: " + Integer.toString(this.targetId) + " does not exist");
            return;
        }

        try{    
            this.edgeId = Integer.parseInt(externalCommandSegments[5]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }catch(Exception e){
            System.out.println("no id given, who giveth a fuck");
        }

        if (this.edgeId == -1){
            this.edge = this.structure.getEdgeByVertexIds(this.sourceId,this.targetId);
        }else{
            this.edge = this.structure.getEdgeByVertexIdsAndId(this.sourceId,this.targetId,this.edgeId);
        }

        if (this.edge == null){
            if (this.edgeId != -1){
                this.error = new Exception("error: edge with id: " + this.edgeId + " between target vertex " + Integer.toString(this.targetId) + " and source vertex " + Integer.toString(this.sourceId) + " does not exist!");
            }else{
                this.error = new Exception("error: edge between target vertex " + Integer.toString(this.targetId) + " and source vertex " + Integer.toString(this.sourceId) + " does not exist!");
            }
            this.fail();
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