/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.algorithm;

import gralog.firstorderlogic.logic.firstorder.formula.FirstOrderFormula;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;

import gralog.algorithm.*;
import gralog.firstorderlogic.prover.TreeDecomposition.*;
import gralog.structure.*;
import gralog.progresshandler.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;


import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import javafx.scene.control.TextArea;


/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="First Order Logic Prover",
  text="",
  url="https://en.wikipedia.org/wiki/First-order_logic"
)

public class FirstOrderProver extends Algorithm {
    
    @Override
    public AlgorithmParameters GetParameters(Structure s) {
        return new FirstOrderProverParameters();
    }
  
    public Object Run(Structure s, AlgorithmParameters p,Set<Object> selection, ProgressHandler onprogress) throws Exception {
        
       FirstOrderProverParameters sp = (FirstOrderProverParameters)(p);
        Set<Vertex> V=s.getVertices();
        int i=0;
        for(Vertex v : V ){
            v.Label=String.valueOf(i);
            i++;
        }
        if(V.isEmpty()){
            return "Please input a graph";
        }
         PrintWriter out = new PrintWriter(new BufferedWriter(
                                                 new FileWriter("PreviousSearch.txt", false)));    
            out.println(sp.formulae);  
            out.close();
            FirstOrderParser parser = new FirstOrderParser();
            FirstOrderFormula phi;
        try {
            phi = parser.parseString(sp.formulae);
        } catch (Exception ex) {
           return "Incorrect Syntax or Formula";
        }
        PrintWriter o1 = new PrintWriter(new BufferedWriter(
                                                 new FileWriter("CorrectSearches.txt", true)));
            o1.println(sp.formulae);           
            o1.close();
      
        HashMap<String, Vertex> varassign = new HashMap<String, Vertex>();
        
        FOQueryResult result=new FOQueryResult();
        result.rootBag=phi.EvaluateProver(s, varassign,onprogress);
        
        result.rootBag.caption=phi.toString();
        
        return result;
    }
    
}
