package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;
import java.lang.reflect.Field;


public class GetVertexCommand extends CommandForGralogToExecute {

    Vertex vertex;
  

	public GetVertexCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        

        try{
            this.vertex = PipingMessageHandler.extractVertex(externalCommandSegments,structure);
        }catch(Exception e){
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }


	}

	public void handle(){

        // int changeId;

        
        try{
            this.setResponse(this.vertex.gralogPipify());
        }catch(Exception e){
            this.fail();
            this.error = e;

        }

        
	}

}