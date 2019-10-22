package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantEdgeException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;


public class SetEdgeLabelCommand extends CommandForGralogToExecute {
    

   
    Edge edge;
    String label;
    // String neighbourString;



    public SetEdgeLabelCommand(String[] externalCommandSegments,Structure structure) {
        this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        try {
            this.edge = PipingMessageHandler.extractEdge(externalCommandSegments,structure);
        }catch(NonExistantEdgeException e) {
            this.setConsoleMessage("(non-fatal) " + e.toString());
            return;
        }catch(Exception e) {
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }

        // this.generateLabel(externalCommandSegments);
        this.label = externalCommandSegments[3];
    }

    public void generateLabel(String[] externalCommandSegments) {
        String label = "";
        for (int i = 4; i < externalCommandSegments.length; i += 1) {
            label = label + externalCommandSegments[i]+ " ";
        }
        this.label = label;
    }


    public void handle() {

        

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));
        if (this.edge != null) {
            this.edge.setLabel(this.label);
        }

        this.setResponse(null);

        return;


        // return v;
    }

}
