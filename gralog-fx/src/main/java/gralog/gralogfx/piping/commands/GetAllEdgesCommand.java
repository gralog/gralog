package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;




public class GetAllEdgesCommand extends CommandForGralogToExecute {
	


	public GetAllEdgesCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

	}


	

	public void handle() {

       

        Set<Edge> allEdges = this.structure.getEdges();

        String edgeString = "";
        for (Edge e : allEdges){
            edgeString = edgeString + PipingMessageHandler.universalEdgeToTuple(e)+ "#";
        }
        if (edgeString.length() > 0 && null != edgeString) {
            edgeString = edgeString.substring(0,edgeString.length()-1);
        }


        this.setResponse(edgeString);

        return;

	}

}
