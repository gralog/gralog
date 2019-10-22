
/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
/**
 *
 * @author felix
 */
package gralog.gralogfx.piping;
// import java.util.concurrent.ThreadLocalRandom;
// import gralog.events.*;
import gralog.gralogfx.piping.commands.CommandForGralogToExecute;
import gralog.gralogfx.piping.commands.*;
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
import org.reflections.Reflections;

import java.util.HashMap;


public class PipingMessageHandler {

   
    private Structure structure;
    private StructurePane pane;
    private static HashMap<String,Class<? extends CommandForGralogToExecute>> commandNameToClassMap;
    private static Class[] args3;
    private static Class[] args2;
    static {
        args3 = new Class[3]; 
        args3[0] = String[].class; 
        args3[1] = Structure.class; 
        args3[2] = Piping.class; 
        args2 = new Class[2]; 
        args2[0] = String[].class; 
        args2[1] = Structure.class;
        commandNameToClassMap = new HashMap<String, Class<? extends CommandForGralogToExecute>>();
        Reflections reflections = new Reflections("gralog.gralogfx.piping");
        Set<Class<? extends CommandForGralogToExecute>> gralogCommands = reflections.getSubTypesOf(CommandForGralogToExecute.class);
        for (Class<? extends CommandForGralogToExecute> c : gralogCommands) {
            String name = c.getSimpleName().substring(0,c.getSimpleName().length()-7).toLowerCase();
            commandNameToClassMap.put(name,c);
        }
    }

    public static GraphType properGraphFormats(String format) {

        
        List<String> tikz = Arrays.asList("tikz");
        List<String> trivial = Arrays.asList("trivial","tgf");
        List<String> xml = Arrays.asList("xml");
        List<String> gtgf = Arrays.asList("gtgf");
        List<String> incm = Arrays.asList("incm");
     
        GraphType graphType = GraphType.Null;
        for (String piece : format.split(" ")) {


            piece = piece.toLowerCase();
            if (tikz.contains(piece)) {
                graphType = GraphType.Tikz;
            }
            
                
            if (trivial.contains(piece)) {
                graphType = GraphType.Tgf;
            }
                
            if (xml.contains(piece)) {
                graphType = GraphType.Xml;
            }

            if (gtgf.contains(piece)) {
                graphType = GraphType.GTgf;
            }
            
            if (incm.contains(piece)) {
            	graphType = GraphType.IncM;
            }
        }
        return graphType;
    }

    
    public static String properGraphNames(String name) {

        
        List<String> directed = Arrays.asList("directed");
        List<String> undirected = Arrays.asList("undirected");
        List<String> buchi = Arrays.asList("buchi","buechi","b\u00fcchi");
        List<String> kripke = Arrays.asList("kripke");
        List<String> parity = Arrays.asList("parity","game");
        List<String> automaton = Arrays.asList("automaton");
        for (String piece : name.split(" ")) {


            piece = piece.toLowerCase();
            if (directed.contains(piece)) {
                return "Directed Graph";
            }
            if (undirected.contains(piece)) {
                return "Undirected Graph";
            }
                
            if (buchi.contains(piece)) {
                return "Buechi Automaton";
            }
                
            if (kripke.contains(piece)) {
                return "Kripke Structure";
            }
                
            if (parity.contains(piece)) {
                return "Parity Game";
            }
                
            if (automaton.contains(piece)) {
                return "Automaton";
            }
        }
        return (String)null;
    }

    public static GralogColor hexToGralogColor(String color) throws ColorFormatException {
        GralogColor changeColor;
        //hex notation
        if (color.length() == 7) {
            color = color.substring(1);
        }
        try{
            changeColor = new GralogColor(Integer.parseInt(color));
            return changeColor;
        }catch(Exception e) {
            throw new ColorFormatException("the hex color: " + color + " is not defined!");
        }
        

    }

    public static GralogColor rgbToGralogColor(String rgb) throws ColorFormatException {
        String[] rgbSplit = rgb.split(",");
        try {
            int r = Integer.parseInt(rgbSplit[0]);
            int g = Integer.parseInt(rgbSplit[1]);
            int b = Integer.parseInt(rgbSplit[2]);
            return new GralogColor(r,g,b);
        }catch(Exception e) {
            throw new ColorFormatException("the rgb color: " + rgb + " is not defined!");
        }

    }

    public static String rejoinExternalCommandSegments(String[] externalCommandSegments) {
        return String.join(" ",externalCommandSegments);
    }

    public static Edge extractEdge(String[] externalCommandSegments, Structure structure) throws Exception {
        String edge;
        try {
            edge = externalCommandSegments[2];
        }catch(Exception e) {
            throw new MessageFormatException("the command " + rejoinExternalCommandSegments(externalCommandSegments) + " did not have an edge as the 3rd paramter!");
        }
        String[] sourceTargetOrEdgeId = edge.split(",");
        if (sourceTargetOrEdgeId.length == 1) {
            //the edge id was passed
            int id;
            try {
                id = Integer.parseInt(edge);
            }catch(NumberFormatException e) {
                throw new MessageFormatException("the id: " + edge + " you have passed was not an integer!");
            }

            Edge e = structure.getEdgeById(id);
            if (e == null) {
                throw new NonExistantEdgeException("the id: " + edge + " is not assigned to an edge!");
            }
            return e;
        }else {
            int sourceId;
            int targetId;
            String sourceString;
            String targetString;
            Vertex source;
            Vertex target;
            try {
                sourceString = sourceTargetOrEdgeId[0];
            }catch(Exception e) {
                throw new MessageFormatException("the source of the source,target tuple: " + edge + " you have passed was not valid!");
            }
            try {
                targetString = sourceTargetOrEdgeId[1];
            }catch(Exception e) {
                throw new MessageFormatException("the target of the source,target tuple: " + edge + " you have passed was not valid!");
            }
            try {
                sourceId = Integer.parseInt(sourceString);
            }catch(Exception e) {
                throw new MessageFormatException("source id: " + sourceString + " you have passed was not an integer!");
            }
            try {
                targetId = Integer.parseInt(targetString);
            }catch(Exception e) {
                throw new MessageFormatException("target id: " + targetString + " you have passed was not an integer!");
            }
            try {
                source = structure.getVertexById(sourceId);
            }catch(Exception e) {
                throw new NonExistantVertexException("the source vertex of id " + sourceString + " you have passed does not exist!");
            }
            try {
                target = structure.getVertexById(targetId);
            }catch(Exception e) {
                throw new NonExistantVertexException("the target vertex of id " + targetString + " you have passed does not exist!");
            }
            Edge e = structure.getEdgeByEndVertices(source,target);
            if (e == null) {
                throw new NonExistantEdgeException("there is no edge with endpoints " + edge + " which you have passed!");
            }
            return e;

        }
    }

    

    public static Vertex extractVertex(String[] externalCommandSegments, Structure structure) throws Exception {
        String vertex;
        try {
            vertex = externalCommandSegments[2];
        }catch(Exception e) {
            throw new MessageFormatException("the command " + rejoinExternalCommandSegments(externalCommandSegments) + " did not have a vertex as the 3rd paramter!");
        }
        
 
        int id;
        try {
            id = Integer.parseInt(vertex);
        }catch(NumberFormatException e) {
            throw new MessageFormatException("the id: " + vertex + " you have passed was not an integer!");
        }
        Vertex v = structure.getVertexById(id);
        if (v == null) {
            throw new NonExistantEdgeException("the id: " + vertex + " is not assigned to a vertex!");
        }
        return v;
    
    }

    public static String extractNthPositionString(String[] externalCommandSegments, int n) throws Exception {
        String string;
        try {
            string = externalCommandSegments[n];
        }catch(Exception e){
            throw new MessageFormatException("the command " +
                    rejoinExternalCommandSegments(externalCommandSegments) +
                    " did not have a paramater at index: " +
                    n);
        }
        return string;
    
    }

    public static Vertex extractSourceFromEdge(String[] externalCommandSegments, Structure structure) throws Exception {
        Edge e  = extractEdge(externalCommandSegments,structure);
        return e.getSource();
    }

    public static Vertex extractTargetFromEdge(String[] externalCommandSegments, Structure structure) throws Exception {
        Edge e  = extractEdge(externalCommandSegments,structure);
        return e.getTarget();
    }

   

    public static String universalEdgeToTuple(Edge e) {
        return "("+Integer.toString(e.getId())+","+Integer.toString(e.getSource().getId())+","+Integer.toString(e.getTarget().getId())+")";
    }

    public static String universalEdgeToGralogTuple(Edge e) {
        return e.gralogPipify()+"#source="+Integer.toString(e.getSource().getId())+"|vertex"+"#target="+Integer.toString(e.getTarget().getId())+"|vertex";
    }


    public static List<String[]> parsePauseVars(String[] vars, boolean rankGiven) {
        List<String[]> tuples = new ArrayList<String[]>();
        
        int rankAddition = 0;
        if (rankGiven) {
            rankAddition = 1;
        }
        for (int i = 1 + rankAddition; i < vars.length; i ++) {
            String[] terms = vars[i].split("=");
            String varName = terms[0].substring(1,terms[0].length());
            String varValue = terms[1].substring(0,terms[1].length()-1);
            String[] vals = {varName,varValue};
            tuples.add(vals);
        }  
        return tuples;

    }

    public static GralogColor colorConversionHex(String color) throws ColorFormatException {
        
        String colorName;


        if ((colorName = PipingPresets.getHexByColorName(color)) != null) {
            return hexToGralogColor(colorName);
        }
        return hexToGralogColor(color);
        
    }

    public static GralogColor colorConversionRGB(String color) throws ColorFormatException {
        
        return rgbToGralogColor(color);
    }

    public static CommandForGralogToExecute handleCommand(String[] externalCommandSegments,Structure structure,Piping piping) {
        CommandForGralogToExecute currentCommand;
        Structure currentStructure = structure;
        String commandKeyword = externalCommandSegments[0];




        Class<? extends CommandForGralogToExecute> myClass = commandNameToClassMap.get(commandKeyword.toLowerCase());
        try {
            currentCommand = myClass.getDeclaredConstructor(args2).newInstance(externalCommandSegments,currentStructure);
        }catch(Exception e) {
            try {
                currentCommand = myClass.getDeclaredConstructor(args3).newInstance(externalCommandSegments,currentStructure,piping);
            }catch(Exception ex) {
                currentCommand = new NotRecognizedCommand(externalCommandSegments,null);
            }

        }
        return currentCommand;

    }

    
}
