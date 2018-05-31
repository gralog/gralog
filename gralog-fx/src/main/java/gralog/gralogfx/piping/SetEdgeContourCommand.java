package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;


public class SetEdgeContourCommand extends CommandForGralogToExecute {
    

    int sourceId;
    int targetId;
    Vertex sourceVertex;
    Vertex targetVertex;
    Edge edgeToChangeContourOn;
    String contour;
    // String neighbourString;



    public SetEdgeContourCommand(String[] externalCommandSegments,Structure structure){
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
        System.out.println("ok we're looking for edge with source : " + this.sourceId + " and target: " + this.targetId);
        this.edgeToChangeContourOn = this.structure.getEdgeByVertexIds(this.sourceId,this.targetId);
        System.out.println("bork bork bork ok we found the edge: " + this.edgeToChangeContourOn);
        if (this.edgeToChangeContourOn == null){
            System.out.println("fail!!!! ahahaha i love failure");
            this.fail();
            this.error = new Exception("error: no edge with vertex coordinates " + Integer.toString(this.sourceId) + " " + Integer.toString(this.targetId));
            return;
        }

        this.contour = externalCommandSegments[4];

        // this.generateLabel(externalCommandSegments);

    }

   


    public void handle(){

        

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));
        boolean wasType = false;
        for (GralogGraphicsContext.LineType lineType : GralogGraphicsContext.LineType.values()){
            String lineTypeString = lineType.toString().toLowerCase();
            if (contour.toLowerCase().equals(lineTypeString)){
                this.edgeToChangeContourOn.type = lineType;
                 wasType = true;
                 break;
            }
        }

        // if (contour.toLowerCase().equals("plain")){
        //     this.edgeToChangeContourOn.type = GralogGraphicsContext.LineType.PLAIN;
        // }else if(contour.toLowerCase().equals("dashed")){
        //     this.edgeToChangeContourOn.type = GralogGraphicsContext.LineType.DASHED;
        // }else if(contour.toLowerCase().equals("dotted")){
        //     this.edgeToChangeContourOn.type = GralogGraphicsContext.LineType.DOTTED;
        // }else{
        //     this.fail();
        //     this.error = new Exception("error: edge contour \"" + contour + "\" does not exist");
        //     return;
        // }


        if (wasType){
            this.setResponse(null);
            return;
        }


        this.fail();
        this.error = new Exception("error: edge contour \"" + contour + "\" does not exist");
        return;


        // return v;
    }

}