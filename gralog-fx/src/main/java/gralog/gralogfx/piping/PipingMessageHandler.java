
/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
/**
 *
 * @author felix
 */
package gralog.gralogfx.piping;
// import java.util.concurrent.ThreadLocalRandom;
// import gralog.events.*;
import gralog.rendering.*;
import java.util.concurrent.ThreadLocalRandom;

import java.util.Arrays;
import gralog.structure.*;
import gralog.gralogfx.StructurePane;
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

    public static GralogColor hexToGralogColor(String color) throws ColorFormatException{
        GralogColor changeColor;
        //hex notation
        if (color.length() == 7){
            color = color.substring(1);
        }
        try{

            changeColor = new GralogColor(Integer.parseInt(color,16));
            return changeColor;
        }catch(Exception e){
            throw new ColorFormatException("the hex color: " + color + " is not defined!");
        }
        

    }

    public static GralogColor rgbToGralogColor(String rgb) throws ColorFormatException{
        String[] rgbSplit = rgb.split(",");
        try{
            int r = Integer.parseInt(rgbSplit[0]);
            int g = Integer.parseInt(rgbSplit[1]);
            int b = Integer.parseInt(rgbSplit[2]);
            return new GralogColor(r,g,b);
        }catch(Exception e){
            throw new ColorFormatException("the rgb color: " + rgb + " is not defined!");
        }

    }

    public static String rejoinExternalCommandSegments(String[] externalCommandSegments){
        return String.join(" ",externalCommandSegments);
    }

    public static Edge extractEdge(String[] externalCommandSegments, Structure structure) throws Exception{
        String edge;
        try{
            edge = externalCommandSegments[2];
        }catch(Exception e){
            throw new MessageFormatException("the command " + rejoinExternalCommandSegments(externalCommandSegments) + " did not have an edge as the 3rd paramter!");
        }
        String[] sourceTargetOrEdgeId = edge.split(",");
        if (sourceTargetOrEdgeId.length == 1){
            //the edge id was passed
            int id;
            try{
                id = Integer.parseInt(edge);
            }catch(NumberFormatException e){
                throw new MessageFormatException("the id: " + edge + " you have passed was not an integer!");
            }

            Edge e = structure.getEdgeById(id);
            if (e == null){
                throw new NonExistantEdgeException("the id: " + edge + " is not assigned to an edge!");
            }
            return e;
        }else{
            int sourceId;
            int targetId;
            String sourceString;
            String targetString;
            Vertex source;
            Vertex target;
            try{
                sourceString = sourceTargetOrEdgeId[0];
            }catch(Exception e){
                throw new MessageFormatException("the source of the source,target tuple: " + edge + " you have passed was not valid!");
            }
            try{
                targetString = sourceTargetOrEdgeId[1];
            }catch(Exception e){
                throw new MessageFormatException("the target of the source,target tuple: " + edge + " you have passed was not valid!");
            }
            try{
                sourceId = Integer.parseInt(sourceString);
            }catch(Exception e){
                throw new MessageFormatException("source id: " + sourceString + " you have passed was not an integer!");
            }
            try{
                targetId = Integer.parseInt(targetString);
            }catch(Exception e){
                throw new MessageFormatException("target id: " + targetString + " you have passed was not an integer!");
            }
            try{
                source = structure.getVertexById(sourceId);
            }catch(Exception e){
                throw new NonExistantVertexException("the source vertex of id " + sourceString + " you have passed does not exist!");
            }
            try{
                target = structure.getVertexById(targetId);
            }catch(Exception e){
                throw new NonExistantVertexException("the target vertex of id " + targetString + " you have passed does not exist!");
            }
            Edge e = structure.getEdgeByEndVertices(source,target);
            if (e == null){
                throw new NonExistantEdgeException("there is no edge with endpoints " + edge + " which you have passed!");
            }
            return e;

        }
    }

    public static Vertex extractVertex(String[] externalCommandSegments, Structure structure) throws Exception{
        String vertex;
        try{
            vertex = externalCommandSegments[2];
        }catch(Exception e){
            throw new MessageFormatException("the command " + rejoinExternalCommandSegments(externalCommandSegments) + " did not have a vertex as the 3rd paramter!");
        }
        
 
        int id;
        try{
            id = Integer.parseInt(vertex);
        }catch(NumberFormatException e){
            throw new MessageFormatException("the id: " + vertex + " you have passed was not an integer!");
        }
        Vertex v = structure.getVertexById(id);
        if (v == null){
            throw new NonExistantEdgeException("the id: " + vertex + " is not assigned to a vertex!");
        }
        return v;
    
    }

    public static String extractNthPositionString(String[] externalCommandSegments, Structure structure,int n) throws Exception{
        String string;
        try{
            string = externalCommandSegments[n];
        }catch(Exception e){
            throw new MessageFormatException("the command " + rejoinExternalCommandSegments(externalCommandSegments) + " did not have a paramater at index: " + n + "! Did you remember that Informatiker count from 0?");
        }
        return string;
    
    }

    public static Vertex extractSourceFromEdge(String[] externalCommandSegments, Structure structure) throws Exception{
        Edge e  = extractEdge(externalCommandSegments,structure);
        return e.getSource();
    }

    public static Vertex extractTargetFromEdge(String[] externalCommandSegments, Structure structure) throws Exception{
        Edge e  = extractEdge(externalCommandSegments,structure);
        return e.getTarget();
    }

   

    public static String universalEdgeToTuple(Edge e){
        return "("+Integer.toString(e.getId())+","+Integer.toString(e.getSource().getId())+","+Integer.toString(e.getTarget().getId())+")";
    }

    public static String universalEdgeToGralogTuple(Edge e){
        return "("+e.gralogPipify()+","+e.getSource().gralogPipify()+","+e.getTarget().gralogPipify()+")";
    }


    public static List<String[]> parsePauseVars(String[] vars, boolean rankGiven){
        List<String[]> tuples = new ArrayList<String[]>();
        
        int rankAddition = 0;
        if (rankGiven){
            rankAddition = 1;
        }
        for (int i = 1 + rankAddition; i < vars.length; i ++){
            String[] terms = vars[i].split("=");
            for (String x : terms){
                System.out.println("iter: " + x);
            }
            System.out.println("ok done with iterating");
            System.out.println("ok we have : " + terms[0] + "," + terms[1] + " as terms");
            String varName = terms[0].substring(1,terms[0].length());
            String varValue = terms[1].substring(0,terms[1].length()-1);
            String[] vals = {varName,varValue};
            tuples.add(vals);
        }  
        return tuples;

    }

    public static GralogColor colorConversionHex(String color) throws ColorFormatException{
        
        String colorName;

        if ((colorName = PipingPresets.getHexByColorName(color)) != null){
            return hexToGralogColor(colorName);
        }else{
            System.out.println(colorName + " not in database #rip");
        }
        return hexToGralogColor(color);
        
    }

    public static GralogColor colorConversionRGB(String color) throws ColorFormatException{
        
        return rgbToGralogColor(color);
    }

    public static CommandForGralogToExecute handleCommand(String[] externalCommandSegments,Structure structure){
        System.out.println("handlincom");
        CommandForGralogToExecute currentCommand;
        Structure currentStructure = structure;
        String commandKeyword = externalCommandSegments[0];
        System.out.println("command keyword: " + commandKeyword);

        if (commandKeyword.equals("addVertex")){ //user input simulation
            
            currentCommand = new AddVertexCommand(externalCommandSegments,currentStructure);
            

            System.out.println("structure gotten with id: " + Integer.parseInt(externalCommandSegments[1]) + " is: " + currentStructure);


            // currentCommand.setStructure(currentStructure);

            System.out.println("and the command thinks its structure is: " + currentCommand.structure.toString());

            ///to generalize:::


            System.out.println("received message to add vertex to graph #" + externalCommandSegments[1]);
            // Vertex toAdd = PipingMessageHandler.handleAddVertex(externalCommandSegments,this.structure);
            // this.out.println(Integer.toString(toAdd.getId()));
            return currentCommand;
        }else if (commandKeyword.equals("getGraph")){ //user input simulation

            currentCommand = new GetGraphCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;


        }else if (commandKeyword.equals("setGraph")){ //user input simulation

            currentCommand = new SetGraphCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;


        }else if (commandKeyword.equals("deleteVertex")){ //user input simulation
            System.out.println("received message to delete vertex " + externalCommandSegments[2]);

            currentCommand = new DeleteVertexCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;


        }else if (commandKeyword.equals("setVertexShape")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexFillColor(externalCommandSegments,this.structure);
            
            currentCommand = new SetVertexShapeCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (commandKeyword.equals("setVertexFillColor")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexFillColor(externalCommandSegments,this.structure);
            
            currentCommand = new SetVertexFillColorCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (commandKeyword.equals("setVertexStrokeColor")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexStrokeColor(externalCommandSegments,this.structure);
            
            currentCommand = new SetVertexStrokeColorCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (commandKeyword.equals("setEdgeContour")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexStrokeColor(externalCommandSegments,this.structure);
            
            currentCommand = new SetEdgeContourCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (commandKeyword.equals("setEdgeColor")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexStrokeColor(externalCommandSegments,this.structure);
            
            currentCommand = new SetEdgeColorCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
        }else if (commandKeyword.equals("getVertex")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexStrokeColor(externalCommandSegments,this.structure);
            
            currentCommand = new GetVertexCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (commandKeyword.equals("getVertexProperty")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexStrokeColor(externalCommandSegments,this.structure);
            
            currentCommand = new GetVertexPropertyCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (commandKeyword.equals("getEdge")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexStrokeColor(externalCommandSegments,this.structure);
            
            currentCommand = new GetEdgeCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (commandKeyword.equals("getEdgeProperty")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexStrokeColor(externalCommandSegments,this.structure);
            
            currentCommand = new GetEdgePropertyCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (commandKeyword.equals("setEdgeProperty")){//format: setColor <vertexId> (case1: <hex> case2: <r> <g> <b>)
            // PipingMessageHandler.handleSetVertexStrokeColor(externalCommandSegments,this.structure);
            
            currentCommand = new SetEdgePropertyCommand(externalCommandSegments,currentStructure);
            
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (commandKeyword.equals("setVertexRadius")){//format: setColor <vertexId> <newRadius>
            // PipingMessageHandler.handleSetVertexRadius(externalCommandSegments,this.structure);
            
            currentCommand = new SetVertexRadiusCommand(externalCommandSegments,currentStructure);
            // 
            // currentCommand.setStructure(currentStructure);
            return currentCommand;
            // this.out.println("ack");
        }else if (commandKeyword.equals("getAllEdges")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba
            currentCommand = new GetAllEdgesCommand(externalCommandSegments,currentStructure);
            // 
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (commandKeyword.equals("getAllVertices")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba
            currentCommand = new GetAllVerticesCommand(externalCommandSegments,currentStructure);
            // 
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (commandKeyword.equals("getNeighbours")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba
            System.out.println("neibas reqd");
            currentCommand = new GetNeighboursCommand(externalCommandSegments,currentStructure);
            // 
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (commandKeyword.equals("getOutgoingNeighbours")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba
            System.out.println("neibas reqd");
            currentCommand = new GetOutgoingNeighboursCommand(externalCommandSegments,currentStructure);
            // 
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (commandKeyword.equals("getIncomingNeighbours")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba

            currentCommand = new GetIncomingNeighboursCommand(externalCommandSegments,currentStructure);
            // 
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (commandKeyword.equals("getIncidentEdges")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba

            currentCommand = new GetIncidentEdgesCommand(externalCommandSegments,currentStructure);
            // 
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (commandKeyword.equals("getAdjacentEdges")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba

            currentCommand = new GetAdjacentEdgesCommand(externalCommandSegments,currentStructure);
            // 
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (commandKeyword.equals("getOutgoingEdges")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba

            currentCommand = new GetOutgoingEdgesCommand(externalCommandSegments,currentStructure);
            // 
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (commandKeyword.equals("getIncomingEdges")){//format: setColor <vertexId>
            // String neighbourString = PipingMessageHandler.handleGetNeighbours(externalCommandSegments,this.structure);///get to know yo neighba

            currentCommand = new GetIncomingEdgesCommand(externalCommandSegments,currentStructure);
            // 
            return currentCommand;
            // this.out.println(neighbourString);
        }else if (commandKeyword.equals("addEdge")){//format: addEdge <sourceId> <targetId> <directed?>
            // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
            
            currentCommand = new AddEdgeCommand(externalCommandSegments,currentStructure);
            // 

            return currentCommand;
            // this.out.println(handleEdgeResponse);
        }else if (commandKeyword.equals("deleteEdge")){//format: addEdge <sourceId> <targetId> <directed?>
            // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
            // System.out.println("")
            currentCommand = new DeleteEdgeCommand(externalCommandSegments,currentStructure);
            // 

            return currentCommand;
            // this.out.println(handleEdgeResponse);
        }else if (commandKeyword.equals("deleteAllEdges")){//format: addEdge <sourceId> <targetId> <directed?>
            // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
            // System.out.println("")
            currentCommand = new DeleteAllEdgesCommand(externalCommandSegments,currentStructure);
            // 

            return currentCommand;
            // this.out.println(handleEdgeResponse);
        }else if (commandKeyword.equals("setEdgeLabel")){//format: addEdge <sourceId> <targetId> <directed?>
            // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
            // System.out.println("")
            currentCommand = new SetEdgeLabelCommand(externalCommandSegments,currentStructure);
            // 

            return currentCommand;
            // this.out.println(handleEdgeResponse);
        }else if (commandKeyword.equals("setVertexLabel")){//format: addEdge <sourceId> <targetId> <directed?>
            // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
            // System.out.println("")
            currentCommand = new SetVertexLabelCommand(externalCommandSegments,currentStructure);
            // 
            return currentCommand;

            // this.out.println(handleEdgeResponse);
        }else if (commandKeyword.equals("error")){//format: addEdge <sourceId> <targetId> <directed?>
            // String handleEdgeResponse = PipingMessageHandler.handleAddEdge(externalCommandSegments,this.structure);///get to know yo neighba
            System.out.println("error schmerror");
            currentCommand = new PreGralogErrorCommand(externalCommandSegments,currentStructure);
            // 
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