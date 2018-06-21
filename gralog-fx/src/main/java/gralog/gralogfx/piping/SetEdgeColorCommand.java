package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;


public class SetEdgeColorCommand extends CommandForGralogToExecute {
	

	Vertex sourceVertex;
    Vertex targetVertex;
    int sourceId;
    int targetId;
    Edge edge;
    int edgeId;
	GralogColor changeColor;



	public SetEdgeColorCommand(String[] externalCommandSegments,Structure structure){
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
        String color = "";
        try{
            color = this.externalCommandSegments[4];
            if (color.substring(0,3).equals("hex")){
                this.changeColor = PipingMessageHandler.colorConversionHex(color.substring(4,color.length()-1));
            }else if(color.substring(0,3).equals("rgb")){
                this.changeColor = PipingMessageHandler.colorConversionRGB(color.substring(4,color.length()-1));
            }else{
                System.out.println("fuckadoodledoo");
            }
        }catch(ArrayIndexOutOfBoundsException e){
            this.error = e;
            this.fail();
            return;
        }catch(StringIndexOutOfBoundsException e){
            this.error = e;
            this.fail();
            return;
        }
        

        if (this.changeColor == null){
            System.out.println("fuck it's null!");
            String colorString = "";
            
            this.fail();
            this.error = new Exception("The color: \"" + color + "\" is not known to gralog.");
            return;
        }else{
            System.out.println("it's not null jk");
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

	}

	public void handle(){

        // int changeId;
       
        
        this.edge.color = changeColor;


        this.setResponse(null);
        return;


        // return v;
	}

}