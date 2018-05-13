
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
// import PipingPresets.*;
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

import java.util.HashMap;


public class PipingMessageHandler{

   
    private Structure structure;
    private StructurePane pane;

    public static GraphType properGraphFormats(String format){

        
        List<String> tikz = Arrays.asList("tikz");
        List<String> trivial = Arrays.asList("trivial","tgf");
        List<String> xml = Arrays.asList("xml");
     
        GraphType graphType = GraphType.Null;
        for (String piece : format.split(" ")){


            piece = piece.toLowerCase();
            if (tikz.contains(piece)){
                graphType = GraphType.Tikz;
            }
            
                
            if (trivial.contains(piece)){
                graphType = GraphType.Tgf;
            }
                
            if (xml.contains(piece)){
                graphType = GraphType.Xml;
            }
        }
        return graphType;
    }

    
    public static String properGraphNames(String name){

        
        List<String> directed = Arrays.asList("directed");
        List<String> undirected = Arrays.asList("undirected");
        List<String> buchi = Arrays.asList("buchi","buechi","b\u00fcchi");
        List<String> kripke = Arrays.asList("kripke");
        List<String> parity = Arrays.asList("parity","Game");
        List<String> automaton = Arrays.asList("automaton");
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

    public static GralogColor hexToGralogColor(String color){
        GralogColor changeColor;
        //hex notation
        if (color.length() == 7){
            color = color.substring(1);
        }
        changeColor = new GralogColor(Integer.parseInt(color,16));
        return changeColor;
    }

    public static GralogColor rgbToGralogColor(String[] rgb){

        int r = Integer.parseInt(rgb[0]);
        int g = Integer.parseInt(rgb[1]);
        int b = Integer.parseInt(rgb[2]);
        return new GralogColor(r,g,b);
    }




    public static GralogColor colorConversion(String[] colors){
        System.out.println("we're getting: ");
        
        String colorName;

        if ((colorName = PipingPresets.getHexByColorName(colors[0])) != null){
            return hexToGralogColor(colorName);
        }else{
            System.out.println(colorName + " not in database #rip");
        }

        GralogColor changeColor;
        if (colors.length == 1){
            
            return hexToGralogColor(colors[0]);
        }else if (colors.length == 3){
            return rgbToGralogColor(colors);
        }
        return (GralogColor)null;
        
    }

    public static CommandForGralogToExecute handleCommand(String[] externalCommandSegments,Structure structure){
        CommandForGralogToExecute currentCommand;
        if (externalCommandSegments[0].equals("addVertex")){ //user input simulation
            
            currentCommand = new AddVertexCommand(externalCommandSegments,structure);
            Structure currentStructure = structure;

            System.out.println("structure gotten with id: " + Integer.parseInt(externalCommandSegments[1]) + " is: " + currentStructure);


            // currentCommand.setStructure(currentStructure);

            System.out.println("and the command thinks its structure is: " + currentCommand.structure.toString());

            ///to generalize:::


            System.out.println("received message to add vertex to graph #" + externalCommandSegments[1]);
            // Vertex toAdd = PipingMessageHandler.handleAddVertex(externalCommandSegments,this.structure);
            // this.out.println(Integer.toString(toAdd.getId()));
            return currentCommand;
        }else if (externalCommandSegments[0].equals("getGraph")){ //user input simulation

            currentCommand = new GetGraphCommand(externalCommandSegments,structure);
            Structure currentStructure = structure;
            // currentCommand.setStructure(currentStructure);
            return currentCommand;


        }else if (externalCommandSegments[0].equals("sendGraph")){ //user input simulation

            currentCommand = new SendGraphCommand(externalCommandSegments,structure);
            Structure currentStructure = structure;
            // currentCommand.setStructure(currentStructure);
            return currentCommand;


        }else if (externalCommandSegments[0].equals("deleteVertex")){ //user input simulation
            System.out.println("received message to delete vertex " + externalCommandSegments[2]);

            currentCommand = new DeleteVertexCommand(externalCommandSegments,structure);
            Structure currentStructure = structure;
            // currentCommand.setStructure(currentStructure);
            return currentCommand;


        }else if (externalCommandSegments[0].equals("setVertexFillColor")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexFillColor(externalCommandSegments,this.structure);
            
            currentCommand = new SetVertexFillColorCommand(externalCommandSegments,structure);
            Structure currentStructure = structure;
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (externalCommandSegments[0].equals("setVertexStrokeColor")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexStrokeColor(externalCommandSegments,this.structure);
            
            currentCommand = new SetVertexStrokeColorCommand(externalCommandSegments,structure);
            Structure currentStructure = structure;
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (externalCommandSegments[0].equals("setEdgeColor")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexStrokeColor(externalCommandSegments,this.structure);
            
            currentCommand = new SetEdgeColorCommand(externalCommandSegments,structure);
            Structure currentStructure = structure;
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (externalCommandSegments[0].equals("setVertexRadius")){//format: setColor <vertexId> <newRadius>
            // PipingMessageHandler.handleSetVertexRadius(externalCommandSegments,this.structure);
            
            currentCommand = new SetVertexRadiusCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (externalCommandSegments[0].equals("getAllEdges")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba
            currentCommand = new GetAllEdgesCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (externalCommandSegments[0].equals("getAllVertices")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba
            currentCommand = new GetAllVerticesCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (externalCommandSegments[0].equals("getNeighbours")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba
            System.out.println("neibas reqd");
            currentCommand = new GetNeighboursCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (externalCommandSegments[0].equals("getOutgoingNeighbours")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba
            System.out.println("neibas reqd");
            currentCommand = new GetOutgoingNeighboursCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (externalCommandSegments[0].equals("getIncomingNeighbours")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba

            currentCommand = new GetIncomingNeighboursCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (externalCommandSegments[0].equals("getIncidentEdges")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba

            currentCommand = new GetIncidentEdgesCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (externalCommandSegments[0].equals("getAdjacentEdges")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba

            currentCommand = new GetAdjacentEdgesCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (externalCommandSegments[0].equals("getOutgoingEdges")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba

            currentCommand = new GetOutgoingEdgesCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (externalCommandSegments[0].equals("getIncomingEdges")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba

            currentCommand = new GetIncomingEdgesCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (externalCommandSegments[0].equals("addEdge")){//format: addEdge <sourceId> <targetId> <directed?>
            // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
            
            currentCommand = new AddEdgeCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;

            return currentCommand;
            // this.out.println(handleEdgeResponse);
        }else if (externalCommandSegments[0].equals("deleteEdge")){//format: addEdge <sourceId> <targetId> <directed?>
            // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
            // System.out.println("")
            currentCommand = new DeleteEdgeCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;

            return currentCommand;
            // this.out.println(handleEdgeResponse);
        }else if (externalCommandSegments[0].equals("setEdgeLabel")){//format: addEdge <sourceId> <targetId> <directed?>
            // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
            // System.out.println("")
            currentCommand = new SetEdgeLabelCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;

            return currentCommand;
            // this.out.println(handleEdgeResponse);
        }else if (externalCommandSegments[0].equals("setVertexLabel")){//format: addEdge <sourceId> <targetId> <directed?>
            // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
            // System.out.println("")
            currentCommand = new SetVertexLabelCommand(externalCommandSegments,structure);
            // Structure currentStructure = structure;
            return currentCommand;

            // this.out.println(handleEdgeResponse);
        }


        else{
            System.out.println("error: not a recognized command dumbfuck did you not read the documentation");
            // out.println(this.structure.xmlToString());
            
            currentCommand = new NotRecognizedCommand(externalCommandSegments,null);
            // Structure currentStructure = structure;
            return currentCommand;
            // this.spacePressed= false;
        }
    }

    
}