package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.structure.*;
import gralog.rendering.*;
import gralog.rendering.shapes.*;
import java.util.Iterator;
import java.lang.reflect.Constructor;


public class SetVertexShapeCommand extends CommandForGralogToExecute {
    

    int vertexId;
    Vertex vertex;
    String shape;
    // String neighbourString;



    public SetVertexShapeCommand(String[] externalCommandSegments,Structure structure) {
        this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        try {    
            this.vertexId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e) {
            this.error = e;
            this.fail();
            return;
        }


        

        this.vertex = this.structure.getVertexById(this.vertexId);

        if (this.vertex == null) {
            this.fail();
            this.error = new NonExistantVertexException("vertex with id " + Integer.toString(this.vertexId) + " does not exist");
            return;
        }

        

        // this.generateLabel(externalCommandSegments);
        this.shape = externalCommandSegments[3].toLowerCase();

    }

    


    public void handle() {

        

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));


        
        Iterator classIterator = RenderingShape.renderingShapeClasses.iterator();

        Diamond diamond = new Diamond(new SizeBox(4.0,4.0));
        Class<? extends RenderingShape> curr = RenderingShape.renderingShapeClasses.get(0);
        boolean wasInstance = false;

        for (int i = 0; i < RenderingShape.renderingShapeClasses.size(); i ++) {
            if (this.shape.equals(RenderingShape.renderingShapeClasses.get(i).getSimpleName().toLowerCase())) {
                wasInstance = true;
                SizeBox currentSizebox = this.vertex.shape.sizeBox;
                try {
                    ///here!
                    Constructor cs = RenderingShape.renderingShapeClasses.get(i).getConstructors()[0];
                    this.vertex.shape = (RenderingShape)cs.newInstance(this.vertex.shape.sizeBox);
                }catch(Exception e) {
                    e.printStackTrace();
                }
                break;

            }
        }

        if (wasInstance) {
            this.setResponse(null);
            return;
        }

        this.fail();
        this.error = new Exception("specified shape \" " + this.shape + "\" is not known to gralog");

        

    }

}
