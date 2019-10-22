package gralog.gralogfx.piping.commands;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Collection;




public class GetAllVerticesCommand extends CommandForGralogToExecute {
	
	public GetAllVerticesCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

	}


	

	public void handle() {

        // int changeId;
       
        
        

        Collection<Vertex> allNodes = this.structure.getVertices();

        String vertexString = "";
        for (Vertex v : allNodes) {
            vertexString = vertexString + Integer.toString(v.getId())+ "#";
        }
        if (vertexString.length() > 0 && null != vertexString) {
            vertexString = vertexString.substring(0,vertexString.length()-1);
        }


        this.setResponse(vertexString);

        return;

	}

}
