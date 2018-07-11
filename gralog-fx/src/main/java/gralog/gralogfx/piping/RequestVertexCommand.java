package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.util.function.Supplier;
import java.util.concurrent.CountDownLatch;



public class RequestVertexCommand extends CommandForGralogToExecute {

    Vertex vertex;
    private CountDownLatch waitForSelection;
    private Supplier<Boolean> selectionFunction;
    private Piping piping;
  

	public RequestVertexCommand(String[] externalCommandSegments,Structure structure,Piping piping){
		
        this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        this.piping = piping;
        this.waitForSelection = piping.waitForSelection;
        this.selectionFunction = piping.selectionFunction;

	}

	public void handle(){

        this.selectionFunction.get();
        this.piping.state = Piping.State.WaitingForSelection;
        try{ 
            this.waitForSelection.await();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("all done!");

        
        
	}

}