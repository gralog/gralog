package gralog.gralogfx;
import gralog.structure.*;
import gralog.rendering.*;
import java.util.Set;

import gralog.exportfilter.TrivialGraphFormatExport;


public class GetGraphCommand extends CommandForGralogToExecute {
	

	GraphType format;
    // String neighbourString;



	public GetGraphCommand(String[] externalCommandSegments,Structure structure){
		this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;
        System.out.println("init GetGraphCommand");
        
        this.format = PipingMessageHandler.properGraphFormats(externalCommandSegments[2]);
        System.out.println("this.format: " + this.format.toString());

        if (this.format == GraphType.Null){
            this.fail();
            this.error = new Exception("error: " + this.format.toString() + " ain't no proper format");
            
        }
	}




	

	public void handle(){

        if (this.format == GraphType.Xml){
            try{
                String xml = this.structure.xmlToString();
                this.setResponse(xml);
            }catch(Exception e){
                this.setResponse(e.toString());
            }
            
            return;
        }

        if (this.format == GraphType.Tgf){
            try{
                String tgf = TrivialGraphFormatExport.exportToString(this.structure);
                this.setResponse(tgf);
            }catch(Exception e){
                this.setResponse(e.toString());
            }
            
            return;
        }

        // int changeId;
        this.setResponse("User wanted it in format : " + this.format.toString());
    


        return ;



        // return v;
	}

}