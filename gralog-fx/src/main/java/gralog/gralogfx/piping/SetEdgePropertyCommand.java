package gralog.gralogfx.piping;
import gralog.structure.*;
import gralog.rendering.*;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;


public class SetEdgePropertyCommand extends CommandForGralogToExecute {

    ////*** CHECK YOURSELF BEFORE YOU USE THIS FUNCTIONALITYYYYY***///
    

    int sourceId;
    int targetId;
    Vertex sourceVertex;
    Vertex targetVertex;
    Edge edge;
    String propertyString;
    String propertyStringValue;
    // String neighbourString;



    public SetEdgePropertyCommand(String[] externalCommandSegments,Structure structure){
        this.externalCommandSegments = externalCommandSegments;
        this.structure = structure;

        try{    
            this.sourceId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        this.externalCommandSegments = externalCommandSegments;

        try{    
            this.targetId = Integer.parseInt(externalCommandSegments[3]);
        }catch(NumberFormatException e){
            this.error = e;
            this.fail();
            return;
        }

        this.sourceVertex = this.structure.getVertexById(this.sourceId);

        if (this.sourceVertex == null){
            this.fail();
            this.error = new Exception("error: source vertex with id " + Integer.toString(this.sourceId) + " does not exist");
            return;
        }

        this.targetVertex = this.structure.getVertexById(this.targetId);

        if (this.targetVertex == null){
            this.fail();
            this.error = new Exception("error: target vertex with id " + Integer.toString(this.targetId) + " does not exist");
            return;
        }

        this.edge = this.structure.getEdgeByVertexIds(this.sourceId,this.targetId);
        if (this.edge == null){
            System.out.println("fail!!!! ahahaha i love failure");
            this.fail();
            this.error = new Exception("error: no edge with vertex coordinates " + Integer.toString(this.sourceId) + " " + Integer.toString(this.targetId));
            return;
        }

        // this.generateLabel(externalCommandSegments);
        try{

            this.propertyString = externalCommandSegments[4];
        }catch(Exception e){
            this.fail();
            this.error = e;
            return;
        }

        try{

            this.propertyStringValue = externalCommandSegments[5];
        }catch(Exception e){
            this.fail();
            this.error = e;
            return;
        }
    }




    public void handle(){

        

        // Edge e = structure.createEdge(this.sourceVertex,this.targetVertex);
            
        // e.isDirected = (externalCommandSegments[3].equals("true"));

        boolean found = false;
        Class<?> c = edge.getClass();
        for (Field f : c.getFields()){
            if (f.getName().equals(this.propertyString)){
                try{
                    System.out.println("looking perhasp for... " + f.getName() + "= " + f.get(this.edge)  + " or " + f.getType());
                    
                    if (f.getType().getName().equals("java.lang.Double")){
                        System.out.println("aha! a dubbleeee");
                        f.set(this.edge,Double.parseDouble(this.propertyStringValue));
                    }else if (f.getType().getName().equals("java.lang.Integer") || f.getType() == int.class){
                        f.set(this.edge,Integer.parseInt(this.propertyStringValue));
                    }else{ //string lol
                        // Constructor cs = f.get(this.edge).getClass().getConstructors()[0];
                        // System.out.println("constructor? : " + cs);
                        // cs.newInstance(this.propertyStringValue);
                        f.set(this.edge,this.propertyStringValue);
                        
                    }
                    this.setResponse(null);

                }catch(Exception e){
                    this.fail();
                    this.error = e;

                }
                return;
            }
        }
        this.fail();
        this.error = new Exception("class Edge does not have property : " + this.propertyString);
        return;


        // return v;
    }

}