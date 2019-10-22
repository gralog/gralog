package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;


public class RequestRandomVertexCommand extends CommandForGralogToExecute {

    Vertex vertex;
  

	public RequestRandomVertexCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        

        


	}

	public void handle() {

        try {
            this.vertex = this.structure.getRandomVertex();
            if (this.vertex != null) {
                this.setResponse(Integer.toString(this.vertex.getId()));
                return;
            }
            this.fail();
            this.error = new NonExistantVertexException("Unable to pick a random vertex because there are no vertices in the graph@!");
            this.setResponse(null);
        }catch(Exception e) {
            this.fail();
            this.error = e;
            this.setResponse(null);
        }
        
	}

}
