package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;


public class SetEdgeColorCommand extends CommandForGralogToExecute {
	

	Vertex sourceVertex;
    Vertex targetVertex;
    Edge edge;
	GralogColor changeColor;



	public SetEdgeColorCommand(String[] externalCommandSegments,Structure structure){
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

	}

	public void handle(){

        // int changeId;
       
        String[] color;
        if (this.externalCommandSegments.length == 5){
            color = Arrays.copyOfRange(externalCommandSegments, 4, 5);
            this.changeColor = PipingMessageHandler.colorConversion(Arrays.copyOfRange(externalCommandSegments, 4, 5));
        }else{
            color = Arrays.copyOfRange(externalCommandSegments, 4, 7);
            this.changeColor = PipingMessageHandler.colorConversion(Arrays.copyOfRange(externalCommandSegments, 4, 7));
        }



        if (this.changeColor == null){
            System.out.println("fuck it's null!");
            String colorString = "";
            for (String x : color){
                colorString += x;
            }
            this.fail();
            this.error = new Exception("The color: \"" + colorString + "\" is not known to gralog.");
            return;
        }else{
            System.out.println("it's not null jk");
        }
        
        
        
        this.edge.color = changeColor;


        this.setResponse(null);
        return;


        // return v;
	}

}