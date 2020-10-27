package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;


public class GPrintCommand extends gralog.gralogfx.piping.commands.CommandForGralogToExecute {
	


    // String neighbourString;

	String[] externalCommandSegments;
	Piping piping;
	String message;
	Piping.MessageToConsoleFlag flag;

	public GPrintCommand(String[] externalCommandSegments,Structure structure,Piping piping) {
		this.externalCommandSegments = externalCommandSegments;
		this.piping=piping;
		this.flag = Piping.MessageToConsoleFlag.GPrint;
		try {
			this.message = PipingMessageHandler.extractNthPositionString(externalCommandSegments,2);
		}catch(Exception e) {
			this.error = e;
			this.fail();
		}


	}


	public void handle() {
		this.piping.sendMessageToConsole.accept(this.message,this.flag);
        this.setResponse(null);
        return;
	}

}
