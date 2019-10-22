package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.MessageFormatException;
import gralog.structure.*;
import gralog.rendering.*;


public class NotRecognizedCommand extends CommandForGralogToExecute {
	


    // String neighbourString;



	public NotRecognizedCommand(String[] externalCommandSegments,Structure structure) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        String externalCommand = "";
        for (String s : externalCommandSegments) {
            externalCommand += s + " ";
        }
        if (externalCommand.length() > 0) {
            externalCommand = externalCommand.substring(0,externalCommand.length()-1);
        }
        this.fail();
        this.error = new MessageFormatException("<" +externalCommand + "> is not a recognized command");

	}


	public void handle() {
        return;
	}

}
