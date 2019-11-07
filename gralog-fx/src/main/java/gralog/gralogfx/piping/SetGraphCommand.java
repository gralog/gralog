package gralog.gralogfx.piping;
import gralog.gralogfx.piping.commands.CommandForGralogToExecute;
import gralog.structure.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import gralog.gralogfx.StructurePane;
import java.util.Arrays;
import gralog.importfilter.TrivialGraphFormatImport;
import gralog.importfilter.GralogTrivialGraphFormatImport;


public class SetGraphCommand extends CommandForGralogToExecute {
	

	GraphType format;
    String graphString;
    StructurePane currentStructurePane;
    int localStructureId;
    Piping piping;


	public SetGraphCommand(String[] externalCommandSegments,Structure structure,Piping piping) {
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        this.piping= piping;
        

        try {
            this.format = PipingMessageHandler.properGraphFormats(externalCommandSegments[2]);
            this.graphString = String.join("#",Arrays.copyOfRange(externalCommandSegments, 3, externalCommandSegments.length));
        }catch(Exception e) {
            this.error = e;
            this.fail();
            return;
        }


        if (this.format == GraphType.Null) {
            this.fail();
            this.error = new Exception(this.format.toString() + " ain't no proper graph format");
            return;
        }
        this.localStructureId = this.piping.getIdWithStructure(this.structure);
        this.currentStructurePane = this.piping.getStructurePaneWithId(this.localStructureId);
	}




	

	public void handle() {

        /*Gralog messages are in the format:

        $$
        Bla 
        Bla
        bla
        #
        bla
        $


        In other words, if a message is going to have multiple lines, there is 
        a $$ in front of the first line, then a $ at the end before the last line
        */

        if (this.format == GraphType.Xml) {

            try {
                InputStream is = new ByteArrayInputStream(this.graphString.getBytes());
                Structure structureFromXml = Structure.loadFromStream(is);
                this.piping.pairLocalIdAndStructure(localStructureId,structureFromXml);
                this.currentStructurePane.setStructure(structureFromXml);

            }catch(Exception e) {
                this.error =e;
                this.fail();
                return;
            }
            //parse the xml, possibly in a multi-line manner

        }else if (this.format == GraphType.Tgf) {
            String totalGraph = "";
            String firstLine;
            try {
                firstLine = PipingMessageHandler.extractNthPositionString(externalCommandSegments,3);
            }catch(Exception e) {
                doFail(e);
                return;
            }
            if (!firstLine.equals("$$")) {
                doFail(new MessageFormatException("no multiline syntax!"));
            }
            String line = "";
            
            try {
                line = this.piping.getNextLine();
                while (!line.equals("$")) {

                    totalGraph += line + "\n";
                    line = this.piping.getNextLine();
                }
            }catch(Exception e) {
                this.doFail(e);
                return;
            }
            InputStream is = new ByteArrayInputStream(totalGraph.getBytes());
            TrivialGraphFormatImport importer = new TrivialGraphFormatImport();
            Structure structureFromTGF;
            try {
                structureFromTGF = importer.importGraph(is,null);
            }catch(Exception e) {
                this.doFail(e);
                return;
            }
            this.piping.pairLocalIdAndStructure(localStructureId,structureFromTGF);
        }else if (this.format == GraphType.Tikz) {
            //parse the tikz, possibly in a multi-line manner
        }else if (this.format == GraphType.GTgf) {
            String totalGraph = "";
            String firstLine;
            try {
                firstLine = PipingMessageHandler.extractNthPositionString(externalCommandSegments,3);
            }catch(Exception e) {
                doFail(e);
                return;
            }
            if (!firstLine.equals("$$")) {
                doFail(new MessageFormatException("no multiline syntax!"));
            }
            String line = "";
            
            try {
                line = this.piping.getNextLine();
                while (!line.equals("$")) {

                    totalGraph += line + "\n";
                    line = this.piping.getNextLine();
                }
            }catch(Exception e) {
                this.doFail(e);
                return;
            }
            InputStream is = new ByteArrayInputStream(totalGraph.getBytes());
            GralogTrivialGraphFormatImport importer = new GralogTrivialGraphFormatImport();
            Structure structureFromGTGF;
            try {
                structureFromGTGF = importer.importGraph(is,null);
            }catch(Exception e) {
                this.doFail(e);
                return;
            }
            this.currentStructurePane.setStructure(structureFromGTGF);
            this.piping.pairLocalIdAndStructure(localStructureId,structureFromGTGF);

        }else {
            this.error = new MessageFormatException("The format " + this.format.toString() + " ain't no proper graph format!");
        }

        // int changeId;

        this.setResponse(null);

        return ;
	}

    public void doFail(Exception e) {
        this.error = e;
        this.fail();
        return;
    }

}
