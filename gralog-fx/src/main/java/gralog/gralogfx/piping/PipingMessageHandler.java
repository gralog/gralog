
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




    public static GralogColor colorConversion(String[] colors){
        System.out.println("we're getting: ");
        for (int i = 0; i < colors.length; i ++){
            System.out.println("color: " + colors[i]);
        }
        GralogColor changeColor;
        if (colors.length == 1){
            String color = colors[0];
            //hex notation
            if (color.length() == 7){
                color = color.substring(1);
            }
            changeColor = new GralogColor(Integer.parseInt(color,16));
            return changeColor;
        }else if (colors.length == 3){
            int r = Integer.parseInt(colors[0]);
            int g = Integer.parseInt(colors[1]);
            int b = Integer.parseInt(colors[2]);
            return new GralogColor(r,g,b);
        }
        return (GralogColor)null;
        
    }

    
}