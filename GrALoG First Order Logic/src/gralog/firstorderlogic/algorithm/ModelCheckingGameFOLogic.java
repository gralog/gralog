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
    /*void AssignCoordinates(Vertex root,Set<Vertex> V){
     //for graph with no cycles///
       int size=V.size();
        LinkedList<Vertex> queue = new LinkedList<Vertex>();
        queue.add(root);
        root.Coordinates.add((double)(size*4) );
        root.Coordinates.add((double)(1) );
        while (queue.size() != 0)
        {
            root = queue.poll();
            
            Set<Edge> conEdge=root.getConnectedEdges();
            
            VectorND coor=root.Coordinates;
            Double xcoor=coor.get(0);
            Double ycoor=coor.get(1);
            Double x=(2*xcoor)/size;
            Double y=ycoor+5;
            int i=1;
            for(Edge e : conEdge){
                Vertex target=e.getTarget();
                if(target.Coordinates.Dimensions()==0){
                     queue.add(target);
                     target.Coordinates.add(x*i);
                    target.Coordinates.add(y/i);
                    i++;
                }
                
               
            }
        }
    }
    
    */
   public Object Run(Structure s, AlgorithmParameters p, ProgressHandler onprogress) throws Exception {
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
        //AssignCoordinates(root,V);
        return gp;
   }
}
