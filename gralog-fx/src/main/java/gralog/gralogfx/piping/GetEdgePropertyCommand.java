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
        

        //get source, target vertex id's, terminate program if the number is not a number
		try{    
            this.sourceId = Integer.parseInt(externalCommandSegments[2]);
            this.targetId = Integer.parseInt(externalCommandSegments[3]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        //find source vertex, terminate program if the vertex doesn't exist
        this.sourceVertex = this.structure.getVertexById(this.sourceId);
        if (this.sourceVertex == null){
            this.fail();
            this.error = new Exception("error: source vertex with id: " + Integer.toString(this.sourceId) + " does not exist");
            return;
        }

        //find target vertex, terminate program if the vertex doesn't exist
        this.targetVertex = this.structure.getVertexById(this.targetId);
        if (this.targetVertex == null){
            this.fail();
            this.error = new Exception("error: target vertex with id: " + Integer.toString(this.targetId) + " does not exist");
            return;
        }

        //get edge id, terminate program if not a number. if no edge id given, on is gone
        try{    
            this.edgeId = Integer.parseInt(externalCommandSegments[5]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }catch(Exception e){
            System.out.println("no id given, who giveth a fuck");
        }


        //if no id given, get an edge between the two vertices
        if (this.edgeId == -1){
            this.edge = this.structure.getEdgeByVertexIds(this.sourceId,this.targetId);
        }else{//if id given, get THE edge between the two vertices
            this.edge = this.structure.getEdgeByVertexIdsAndId(this.sourceId,this.targetId,this.edgeId);
        }

        //if this edge doesn't exist, crash the program
        if (this.edge == null){
            if (this.edgeId != -1){
                this.error = new Exception("error: edge with id: " + this.edgeId + " between target vertex " + Integer.toString(this.targetId) + " and source vertex " + Integer.toString(this.sourceId) + " does not exist!");
            }else{
                this.error = new Exception("error: edge between target vertex " + Integer.toString(this.targetId) + " and source vertex " + Integer.toString(this.sourceId) + " does not exist!");
            }
            this.fail();
            return;
        }

        //extract the property to be searched for, if it doesn't exist, terminate program
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