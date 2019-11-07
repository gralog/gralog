package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantVertexException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;


public class SetVertexPropertyCommand extends CommandForGralogToExecute {

    ////*** CHECK YOURSELF BEFORE YOU USE THIS FUNCTIONALITYYYYY***///
    

    
    Vertex vertex;
    String propertyString;
    String propertyStringValue;
    // String neighbourString;



    public SetVertexPropertyCommand(String[] externalCommandSegments,Structure structure) {
        this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        try {
            this.vertex = PipingMessageHandler.extractVertex(externalCommandSegments,structure);
        }catch(NonExistantVertexException e) {
            this.setConsoleMessage("(non-fatal) " + e.toString());
            return;
        }catch(Exception e) {
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }
        try {
            this.propertyString = PipingMessageHandler.extractNthPositionString(externalCommandSegments,3);
        }catch(Exception e) {
            this.fail();
            this.error = e;
            return;
        }

        try {

            this.propertyStringValue = PipingMessageHandler.extractNthPositionString(externalCommandSegments,4);
        }catch(Exception e) {
            this.fail();
            this.error = e;
            return;
        }
    }




    public void handle() {
        if (this.vertex != null) {

            boolean found = false;
            Class<?> c = vertex.getClass();
            for (Field f : c.getFields()) {
                if (f.getName().equals(this.propertyString)) {
                    try {
                        if (f.getType().getName().equals("java.lang.Double")) {
                            f.set(this.vertex,Double.parseDouble(this.propertyStringValue));
                        }else if (f.getType().getName().equals("java.lang.Integer") || f.getType() == int.class) {
                            f.set(this.vertex,Integer.parseInt(this.propertyStringValue));
                        }else { //string lol
                            // Constructor cs = f.get(this.vertex).getClass().getConstructors()[0];

                            // cs.newInstance(this.propertyStringValue);
                            f.set(this.vertex,this.propertyStringValue);
                            
                        }
                        this.setResponse(null);

                    }catch(Exception e) {
                        this.fail();
                        this.error = e;

                    }
                    return;
                }
            }
            // this.fail();
            // this.error = new Exception("class Vertex does not have property : " + this.propertyString);
            // return;
        }
        this.setResponse(null);
        return;


        // return v;
    }

}
