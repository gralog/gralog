package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantEdgeException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.gralogfx.piping.PropertyException;
import gralog.structure.*;
import gralog.rendering.*;



public class SetEdgeContourCommand extends CommandForGralogToExecute {
    

    
    Edge edge;
    int edgeId;
    String contour;
    // String neighbourString;



    public SetEdgeContourCommand(String[] externalCommandSegments,Structure structure) {
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

      

        this.contour = externalCommandSegments[3];

        // this.generateLabel(externalCommandSegments);

    }

   


    public void handle() {

        
        if (this.edge != null) {

            boolean wasType = false;
            for (GralogGraphicsContext.LineType lineType : GralogGraphicsContext.LineType.values()) {
                String lineTypeString = lineType.toString().toLowerCase();
                if (contour.toLowerCase().equals(lineTypeString)) {
                    this.edge.type = lineType;
                    wasType = true;
                    break;
                }
            }


            if (wasType) {
                this.setResponse(null);
                return;
            }


            this.fail();
            this.error = new PropertyException("error: edge contour \"" + contour + "\" does not exist");
            return;
        }
        this.setResponse(null);
        return;


        // return v;
    }

}
