package gralog.gralogfx.piping.commands;
import gralog.gralogfx.piping.NonExistantEdgeException;
import gralog.gralogfx.piping.PipingMessageHandler;
import gralog.structure.*;
import gralog.rendering.*;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;


public class SetEdgePropertyCommand extends CommandForGralogToExecute {

    ////*** CHECK YOURSELF BEFORE YOU USE THIS FUNCTIONALITYYYYY***///
    

    
    Edge edge;
    String propertyString;
    String propertyStringValue;
    // String neighbourString;



    public SetEdgePropertyCommand(String[] externalCommandSegments,Structure structure) {
        this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        try {
            this.edge = PipingMessageHandler.extractEdge(externalCommandSegments,structure);
        }catch(NonExistantEdgeException e) {
            this.setConsoleMessage("(non-fatal) " + e.toString());
            return;
        }catch(Exception e) {
            this.fail();
            this.setResponse(null);
            this.error = e;
            return;
        }
        try {

            this.propertyString = externalCommandSegments[3];
        }catch(Exception e) {
            this.fail();
            this.error = e;
            return;
        }

        try {

            this.propertyStringValue = externalCommandSegments[4];
        }catch(Exception e) {
            this.fail();
            this.error = e;
            return;
        }
    }




    public void handle() {
        if (this.edge != null) {

            boolean found = false;
            Class<?> c = edge.getClass();
            for (Field f : c.getFields()) {
                if (f.getName().equals(this.propertyString)) {
                    try {
                        if (f.getType().getName().equals("java.lang.Double")) {
                            f.set(this.edge,Double.parseDouble(this.propertyStringValue));
                        }else if (f.getType().getName().equals("java.lang.Integer") || f.getType() == int.class) {
                            f.set(this.edge,Integer.parseInt(this.propertyStringValue));
                        }else { //string lol
                            // Constructor cs = f.get(this.edge).getClass().getConstructors()[0];

                            // cs.newInstance(this.propertyStringValue);
                            f.set(this.edge,this.propertyStringValue);
                            
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
            // this.error = new Exception("class Edge does not have property : " + this.propertyString);
            // return;
        }
        this.setResponse(null);
        return;


        // return v;
    }

}
