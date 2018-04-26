
/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
/**
 *
 * @author felix
 */
package gralog.gralogfx;
// import java.util.concurrent.ThreadLocalRandom;
// import gralog.events.*;
import gralog.rendering.*;
import java.util.concurrent.ThreadLocalRandom;

import java.util.Arrays;
import gralog.structure.*;
// import gralog.algorithm.*;
// import gralog.progresshandler.*;
// import gralog.gralogfx.*;

// import java.Arrays.*;



import java.util.Set;

// import java.io.*;
// import javafx.application.Platform;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Alert.AlertType;
// import javafx.scene.control.TextField;
// import javafx.scene.control.HBox;
// import javafx.scene.control.Label;
// import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;


public class PipingMessageHandler{

   
    private Structure structure;
    private StructurePane pane;

    
    public static String properGraphNames(String name){

        
        List<String> directed = Arrays.asList("d","directed","Directed");
        List<String> undirected = Arrays.asList("u","undirected","Undirected");
        List<String> buchi = Arrays.asList("b","buchi","buechi","b\u00fcchi");
        List<String> kripke = Arrays.asList("kripke","k");
        List<String> parity = Arrays.asList("parity","p","Game");
        List<String> automaton = Arrays.asList("a","automaton");
        for (String piece : name.split(" ")){


            piece = piece.toLowerCase();
            if (directed.contains(piece)){
                return "Directed Graph";
            }
            if (undirected.contains(piece)){
                return "Undirected Graph";
            }
                
            if (buchi.contains(piece)){
                return "Buechi Automaton";
            }
                
            if (kripke.contains(piece)){
                return "Kripke Structure";
            }
                
            if (parity.contains(piece)){
                return "Parity Game";
            }
                
            if (automaton.contains(piece)){
                return "Automaton";
            }
        }
        return (String)null;
    }


    public static Vertex handleAddVertex(String[] externalCommandSegments,Structure structure){
        Vertex v = structure.createVertex();
        v.coordinates = new Vector2D(
            ThreadLocalRandom.current().nextInt(0, 10+1),
            ThreadLocalRandom.current().nextInt(0, 10+1)
        );
        v.fillColor = new GralogColor(204, 136, 153);

        structure.addVertex(v);

        return v;
        
    }

    public static String handleDeleteVertex(String[] externalCommandSegments,Structure structure){
        int deleteId;
        try{    
            deleteId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }

        Vertex toDelete = structure.getVertexById(deleteId);
            
        if (toDelete == null){
            return "error: vertex does not exist"; //// error vertex does not exist
        }

        System.out.println("toDelete: " + toDelete);
        structure.removeVertex(toDelete);

        return "ack";

        
    }

    public static GralogColor colorConversion(String[] externalCommandSegments){
        GralogColor changeColor;
        if (externalCommandSegments.length == 4){
            //hex notation
            if (externalCommandSegments[3].length() == 7){
                externalCommandSegments[3] = externalCommandSegments[3].substring(1);
            }
            changeColor = new GralogColor(Integer.parseInt(externalCommandSegments[3],16));
            return changeColor;
        }else if (externalCommandSegments.length == 6){
            int r = Integer.parseInt(externalCommandSegments[3]);
            int g = Integer.parseInt(externalCommandSegments[4]);
            int b = Integer.parseInt(externalCommandSegments[5]);
            return new GralogColor(r,g,b);
        }
        return (GralogColor)null;
        
    }

    public static String handleSetVertexFillColor(String[] externalCommandSegments,Structure structure){
        int changeId;
        try{    
            changeId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }
        
        Vertex changeVertex = structure.getVertexById(changeId);

        if (changeVertex == null){
            return "error: vertex does not exist"; //// error vertex does not exist
        }
        GralogColor changeColor = colorConversion(externalCommandSegments);
        
        changeVertex.fillColor = changeColor;

        return "ack";
    }

    public static String handleSetVertexStrokeColor(String[] externalCommandSegments,Structure structure){
        int changeId;
        try{    
            changeId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }
        
        Vertex changeVertex = structure.getVertexById(changeId);

        if (changeVertex == null){
            return "error: vertex does not exist"; //// error vertex does not exist
        }
        GralogColor changeColor = colorConversion(externalCommandSegments);
        
        changeVertex.strokeColor = changeColor;

        return "ack";
    }

    public static String handleSetVertexRadius(String[] externalCommandSegments,Structure structure){
        int changeId;
        try{
            changeId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }

        Vertex changeVertex = structure.getVertexById(changeId);

        if (changeVertex == null){
            return "error: vertex does not exist"; //// error vertex does not exist
        }

        int newRadius = Integer.parseInt(externalCommandSegments[2]);

        changeVertex.radius = newRadius;

        return "ack";
    }

    public static String handleGetConnectedNeighbours(String[] externalCommandSegments,Structure structure){
        int sourceId;
        try{    
            sourceId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }

        Vertex sourceVertex = structure.getVertexById(sourceId);

        if (sourceVertex == null){
            return "error: vertex does not exist"; //// error vertex does not exist
        }

        Set<Vertex> connectedNeighbours = sourceVertex.getConnectedNeighbours();

        String neighbourString = "";
        for (Vertex v : connectedNeighbours){
            neighbourString = neighbourString + Integer.toString(v.getId()) + " ";
        }
        if (neighbourString.length() > 0 && null != neighbourString){
            neighbourString = neighbourString.substring(0,neighbourString.length()-1);
        }
        return neighbourString;
    }

    public static String handleAddEdge(String[] externalCommandSegments,Structure structure){
        System.out.println("in add edge");
        int sourceId;
        try{    
            sourceId = Integer.parseInt(externalCommandSegments[1]);
        }catch(NumberFormatException e){
            return e.toString();
        }

        Vertex sourceVertex = structure.getVertexById(sourceId);

        int targetId;
        try{    
            targetId = Integer.parseInt(externalCommandSegments[2]);
        }catch(NumberFormatException e){
            return e.toString();
        }

        Vertex targetVertex = structure.getVertexById(targetId);

        Edge e = structure.createEdge(sourceVertex,targetVertex);
        structure.addEdge(e);
        // e.setSource(sourceVertex);
        // e.setTarget(targetVertex);
        e.isDirected = (externalCommandSegments[3].equals("true"));

        return "ack";
    }

    public static String handleGetCurrentGraphId(){
        return "foo";
    
    }
}