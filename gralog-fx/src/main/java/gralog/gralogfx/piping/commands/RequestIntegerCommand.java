package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.util.function.Supplier;
import java.util.concurrent.CountDownLatch;



public class RequestIntegerCommand extends CommandForGralogToExecute {

    Vertex vertex;
    private CountDownLatch waitForSelection;
    private Supplier<Boolean> selectionFunction;
    private Piping piping;
  

	public RequestIntegerCommand(String[] externalCommandSegments,Structure structure,Piping piping){
		
        this.externalCommandSegments = externalCommandSegments;
		this.structure = structure;
        this.piping = piping;
        this.waitForSelection = piping.waitForSelection;


	}

	public void handle(){

        // this.selectionFunction.get();
        this.piping.state = Piping.State.WaitingForConsoleInput;
        this.piping.sendMessageToConsole.accept("Integer requested!",Piping.MessageToConsoleFlag.Request);
        
        try{
            this.piping.redrawMyStructurePanes();
            this.piping.setClassSelectionIsWaitingFor(Integer.class);
            this.waitForSelection.await();
        }catch(Exception e){
            e.printStackTrace();
        }
        this.piping.state = Piping.State.InProgress;
        String consoleInputString= this.piping.getConsoleInput();
        this.setResponse(consoleInputString);


        
        
	}

}