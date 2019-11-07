package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.Piping;
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
  

	public RequestVertexCommand(String[] externalCommandSegments,Structure structure,Piping piping) {
		
        this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        this.piping = piping;
        this.waitForSelection = piping.waitForSelection;
        this.selectionFunction = piping.graphObjectSelectionFunction;

	}

	public void handle() {

        this.selectionFunction.get();
        this.piping.state = Piping.State.WaitingForSelection;
        this.piping.sendMessageToConsole.accept("Vertex requested!",Piping.MessageToConsoleFlag.Request);
        
        try {
            this.piping.redrawMyStructurePanes();
            this.piping.setClassSelectionIsWaitingFor(Vertex.class);
            this.waitForSelection.await();
        }catch(Exception e) {
            e.printStackTrace();
        }
        this.piping.state = Piping.State.InProgress;
        Vertex v = (Vertex)this.piping.getSelectedObject();
        this.setResponse(Integer.toString(v.getId()));


        
        
	}

}
