/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.algorithm;

import gralog.firstorderlogic.logic.firstorder.formula.FirstOrderFormula;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;

import gralog.algorithm.*;
import gralog.firstorderlogic.structure.*;
import gralog.structure.*;
import gralog.progresshandler.*;
import gralog.rendering.VectorND;
import gralog.structure.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
/**
 *
 * @author Hv
 */
@AlgorithmDescription(
  name="Model Checking Game for first Order Logic",
  text="",
  url="https://en.wikipedia.org/wiki/First-order_logic"
)
public class ModelCheckingGameFOLogic extends Algorithm{
    @Override
    public AlgorithmParameters GetParameters(Structure s) {
         File file=new File("PreviousSearch.txt");
            String str="";
           
            if (file.exists()){
            
            try {
                
                BufferedReader input = new BufferedReader(new FileReader(file));
                str=input.readLine();
            } catch (Exception ex) {
                    str= "ERROR" + ex.toString();
                }
            
            }
            else str="";
           
        return new StringAlgorithmParameter(str);
    }
    /*void DFS(Vertex root,Double x,Double y)
    {
        root.Coordinates.add(x);
        root.Coordinates.add(y);
        
        System.out.println(root.Label + "  x = " +x + "y= " + y ); 
        Set<Edge> edges=root.getConnectedEdges();
        for(Edge e:edges){
            Vertex target=e.getTarget();
            if(target.Coordinates.Dimensions()==0){
                DFS(target,x+7,y);
                y=y+2;
            }
        }
   
    }*/
 
   public Object Run(Structure s, AlgorithmParameters p,Set<Object> selection, ProgressHandler onprogress) throws Exception {
       StringAlgorithmParameter sp = (StringAlgorithmParameter)(p);
        
        FirstOrderParser parser = new FirstOrderParser();
        FirstOrderFormula phi = parser.parseString(sp.parameter);
        PrintWriter out = new PrintWriter(new BufferedWriter(
                                                 new FileWriter("PreviousSearch.txt", false)));    
            out.println(sp.parameter);  
            out.close();
         
        Set<Vertex> V=s.getVertices();
        int i=0;
        for(Vertex v : V ){
            v.Label=String.valueOf(i);
            i++;
        }
         HashMap<String, Vertex> varassign = new HashMap<String, Vertex>();
         GameGraph gp=new GameGraph();
        GamePosition root= phi.ConstructGameGraph(s,varassign,gp,5.0,5.0);
      //  DFS(root,5.0,5.0);
        
        return gp;
   }
}
